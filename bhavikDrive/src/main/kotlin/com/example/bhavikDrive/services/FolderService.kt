package com.example.bhavikDrive.services

import com.example.bhavikDrive.models.FolderModel
import com.example.bhavikDrive.models.AuthorizationDetails
import com.google.api.services.drive.model.File
import com.google.common.io.ByteStreams
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.util.function.Consumer
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service
@RequiredArgsConstructor
class FolderService(
    private val googleDriveService: GoogleDriveService
) {

    fun findAll(): List<FolderModel> {
        val folders: List<File> = googleDriveService.findAllFilesInFolderById("root")
        val googleDriveFolderDTOS: MutableList<FolderModel> = ArrayList()
        folders.forEach(Consumer { folder: File ->
            if (folder.getSize() == null) {
                val dto = FolderModel()
                dto.id = folder.id
                dto.name = folder.name
                dto.link = "https://drive.google.com/drive/u/0/folders/" + folder.id
                googleDriveFolderDTOS.add(dto)
            }
        })
        return googleDriveFolderDTOS
    }

    fun create(folderName: String, parentId: String): String {
        return googleDriveService.createFolder(folderName, parentId)
    }

    fun getFolderId(folderName: String): String {
        return googleDriveService.getFolderId(folderName)
    }

    fun delete(id: String) {
        googleDriveService.deleteFileOrFolderById(id)
    }

    fun download(folderId: String): ByteArray {
        val files = googleDriveService.findAllFilesInFolderById(folderId)
        return zipFiles(files)
    }

    private fun zipFiles(files: List<File>): ByteArray {
        var result: ByteArray

        try {
            ByteArrayOutputStream().use { byteArrayOutputStream ->
                ZipOutputStream(byteArrayOutputStream).use { zipOutputStream ->
                    for (file in files) {
                        googleDriveService.getFileAsInputStream(file.id).use { fileInputStream ->
                            val zipEntry = ZipEntry(file.name)
                            zipOutputStream.putNextEntry(zipEntry)
                            ByteStreams.copy(fileInputStream, zipOutputStream)
                        }
                    }
                    zipOutputStream.close()
                    byteArrayOutputStream.close()
                    result = byteArrayOutputStream.toByteArray()
                }
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return result
    }

    fun moveFolderToAnother(fromFolderId: String, toFolderId: String) {
        googleDriveService.move(fromFolderId, toFolderId)
    }

    fun copyFolderToAnother(fromId: String, toId: String) {
        val fromFolder = googleDriveService.findFolderById(fromId)
        val newFolder = File()
        if (fromFolder.isNotEmpty()) {
            newFolder.name = fromFolder.name
        }
        newFolder.parents = listOf(toId)
        newFolder.mimeType = "application/vnd.google-apps.folder"
        val folders = googleDriveService.findAllFoldersInFolderById(fromId)
        val files = googleDriveService.findAllFilesInFolderById(fromId)
        files.forEach(Consumer { file: File ->
            googleDriveService.copy(
                file.id,
                newFolder.id
            )
        })
        folders.forEach(Consumer { folder: File ->
            copyFolderToAnother(
                folder.id,
                newFolder.id
            )
        })
    }

    fun shareFolder(folderId: String, gmail: String) {
        val authorizationDetails = AuthorizationDetails()
        authorizationDetails.type = "user"
        authorizationDetails.role = "reader"
        googleDriveService.createPermissionForEmail(folderId, authorizationDetails)
    }
}