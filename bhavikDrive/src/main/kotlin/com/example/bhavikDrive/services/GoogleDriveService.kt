package com.example.bhavikDrive.services

import com.example.bhavikDrive.configuration.BeansConfiguration
import com.example.bhavikDrive.models.AuthorizationDetails
import com.example.bhavikDrive.utils.DataConstants
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.api.services.drive.model.Permission
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.function.Consumer


@Service
@RequiredArgsConstructor
class GoogleDriveService(
    private val beansConfiguration: BeansConfiguration
) {

    fun findAll(): List<File> {
        return try {
            val result: FileList = beansConfiguration.getDrive().files().list()
                .setFields("nextPageToken, files(id, name, size, thumbnailLink, shared)").execute()
            result.files
        } catch (e: IOException) {
            throw FileNotFoundException(e.message)
        }
    }

    fun findAllFilesInFolderById(folderId: String = "root"): List<File> {
        return try {
            val query = "'$folderId' in parents"
            val result: FileList = beansConfiguration.getDrive().files().list().setQ(query).setPageSize(10)
                .setFields("nextPageToken, files(id, name, size, thumbnailLink, shared)").execute()
            result.files
        } catch (e: IOException) {
            throw FileNotFoundException(e.message)
        }
    }

    fun findAllFoldersInFolderById(folderId: String): List<File> {
        return try {
            val query = String.format(
                "'%s' in parents and mimeType = '${DataConstants.GoogleDriveAPISURLS.FOLDER_MIMETYPE}' and trashed = false", folderId
            )
            beansConfiguration.getDrive().files().list().setQ(query).setSpaces("drive")
                .setFields("nextPageToken, files(id, name)").execute().files
        } catch (e: IOException) {
            throw FolderNotFoundException(e.message)
        }
    }

    fun download(fileId: String, outputStream: OutputStream) {
        try {
            beansConfiguration.getDrive().files().get(fileId).executeMediaAndDownloadTo(outputStream)
        } catch (e: IOException) {
            throw DownloadFileOrFolderException(e.message)
        }
    }

    fun downloads(fileIds: List<String>, outputStream: OutputStream) {
        try {
            fileIds.forEach { fileId ->
                beansConfiguration.getDrive().files().get(fileId).executeMediaAndDownloadTo(outputStream)
            }
        } catch (e: IOException) {
            throw DownloadFileOrFolderException(e.message)
        }
    }

    private fun setPermission(authorizationDetails: AuthorizationDetails): Permission {
        val permission = Permission()
        if (authorizationDetails.emailAddress != null) {
            permission.emailAddress = authorizationDetails.emailAddress
        }
        return permission.setType(authorizationDetails.type).setRole(authorizationDetails.role)
    }

    fun createPermissionForEmail(id: String, authorizationDetails: AuthorizationDetails) {
        val permission = setPermission(authorizationDetails)
        try {
            beansConfiguration.getDrive().permissions().create(id, permission).execute()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun uploadFile(multipartFile: MultipartFile, folderName: String, authorizationDetails: AuthorizationDetails): String {
        if (multipartFile.isEmpty) return ""
        val file = File()
        file.parents = listOf(getFolderId(folderName))
        file.name = multipartFile.originalFilename
        return try {
            val uploadedFile: File = beansConfiguration.getDrive().files()
                .create(file, InputStreamContent(multipartFile.contentType, ByteArrayInputStream(multipartFile.bytes)))
                .setFields("id").execute()
            if ("private" != authorizationDetails.type && "private" != authorizationDetails.role) {
                beansConfiguration.getDrive().permissions().create(uploadedFile.id, setPermission(authorizationDetails))
            }
            uploadedFile.id
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun getFolderId(folderName: String): String {
        var parentId: String = ""
        for (name in folderName.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            parentId = findOrCreateFolder(parentId, name)
        }
        return parentId
    }

    fun findFolderById(folderId: String): File {
        return try {
            beansConfiguration.getDrive().files().get(folderId).execute()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun findOrCreateFolder(parentId: String, folderName: String): String {
        val folderId = findFolderById(parentId, folderName)
        if (folderId.isNotEmpty()) {
            return folderId
        }
        val folder = File()
        folder.mimeType = DataConstants.GoogleDriveAPISURLS.FOLDER_MIMETYPE
        folder.name = folderName
        if (parentId.isNotEmpty()) {
            folder.parents = listOf(parentId)
        }
        return try {
            beansConfiguration.getDrive().files().create(folder).setFields("id").execute().id
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun findFolderById(parentId: String, folderName: String): String {
        var folderId: String = ""
        var pageToken: String = ""
        do {
            var query = " mimeType = '${DataConstants.GoogleDriveAPISURLS.FOLDER_MIMETYPE}' "

            query = if (parentId.isEmpty()) "$query and 'root' in parents" else "$query and '$parentId' in parents"

            try {
                val result: FileList = beansConfiguration.getDrive()
                    .files()
                    .list()
                    .setQ(query)
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute()
                for (file in result.files) {
                    if (file.name.equals(folderName, ignoreCase = true)) {
                        folderId = file.id
                    }
                }
                pageToken = result.nextPageToken
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        } while (pageToken == "" && folderName == "")
        return folderId
    }

    fun deleteFileOrFolderById(id: String) {
        try {
            beansConfiguration.getDrive().files().delete(id).execute()
        } catch (e: IOException) {
            throw DeleteFileOrFolderException(e.message)
        }
    }

    fun getFileAsInputStream(fileID: String): InputStream {
        return try {
            beansConfiguration.getDrive().files().get(fileID).executeMediaAsInputStream()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun copy(fileId: String, folderId: String) {
        if (folderId.isEmpty()) {
            throw RuntimeException("Folder not found.")
        }
        try {
            beansConfiguration.getDrive().files().copy(fileId, File().setParents(java.util.List.of(folderId))).execute()
        } catch (e: IOException) {
            throw CopyFileOrFolderException(e.message)
        }
    }

    fun move(fileId: String, folderId: String) {
        if (folderId.isEmpty()) {
            throw RuntimeException("Folder not found.")
        }
        try {
            val file: File = beansConfiguration.getDrive().files().get(fileId).setFields("parents").execute()
            val previousParents = StringBuilder()
            file.parents.forEach(Consumer { parent: String ->
                previousParents.append(parent)
                previousParents.append(',')
            })
            beansConfiguration.getDrive().files().update(fileId, null).setAddParents(folderId)
                .setRemoveParents(previousParents.toString()).setFields("id, parents").execute()
        } catch (e: IOException) {
            throw MoveFileOrFolderException(e.message)
        }
    }

    fun createFolder(folderName: String, parentId: String): String {
        val fileMetadata = File()
        if (parentId.isNotEmpty()) {
            fileMetadata.parents = listOf(parentId)
        }
        fileMetadata.name = folderName
        fileMetadata.mimeType = DataConstants.GoogleDriveAPISURLS.FOLDER_MIMETYPE
        return try {
            beansConfiguration.getDrive().files().create(fileMetadata).setFields("id").execute().id
        } catch (e: IOException) {
            throw CreateFolderException(e.message)
        }
    }
}

class FileNotFoundException(override val message: String?) : Throwable()

class FolderNotFoundException(override val message: String?) : Throwable()

class CreateFileException(override val message: String?) : Throwable()

class CreateFolderException(override val message: String?) : Throwable()

class FileDownloadFailedException(override val message: String?) : Throwable()

class FolderDownloadFailedException(override val message: String?) : Throwable()

class MoveFileOrFolderException(override val message: String?) : Throwable()

class CopyFileOrFolderException(override val message: String?) : Throwable()

class DeleteFileOrFolderException(override val message: String?) : Throwable()

class DownloadFileOrFolderException(override val message: String?) : Throwable()

