package com.example.bhavikDrive.services

import com.example.bhavikDrive.models.FileModel
import com.example.bhavikDrive.models.AuthorizationDetails
import com.google.api.services.drive.model.File
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.OutputStream
import java.util.function.Consumer

@Service
@RequiredArgsConstructor
class FileService(
    private val googleDriveService: GoogleDriveService
) {

    fun findAll(): List<FileModel> {
        val googleDriveFileDTOS: MutableList<FileModel> = ArrayList()
        val files: List<File> = googleDriveService.findAll()
        files.forEach(Consumer { file: File ->
            if (file.getSize() != null) {
                val driveFileDto = FileModel()
                fillGoogleDriveFileDTOList(googleDriveFileDTOS, file, driveFileDto)
            }
        })
        return googleDriveFileDTOS
    }

    fun findAllInFolder(folderId: String): List<FileModel> {
        val googleDriveFileDTOList: MutableList<FileModel> = ArrayList()
        val files: List<File> = googleDriveService.findAllFilesInFolderById(folderId)
        files.forEach(Consumer { file: File ->
            if (file.getSize() != null) {
                val driveFileDto = FileModel()
                fillGoogleDriveFileDTOList(googleDriveFileDTOList, file, driveFileDto)
            }
        })
        return googleDriveFileDTOList
    }

    private fun fillGoogleDriveFileDTOList(
        googleDriveFileDTOS: MutableList<FileModel>,
        file: File,
        driveFileDto: FileModel
    ) {
        driveFileDto.id = file.id
        driveFileDto.name = file.name
        driveFileDto.thumbnailLink = file.thumbnailLink
        driveFileDto.size = file.getSize().toString()
        driveFileDto.link = "https://drive.google.com/file/d/" + file.id + "/view?usp=sharing"
        driveFileDto.isShared = file.shared
        googleDriveFileDTOS.add(driveFileDto)
    }

    fun deleteById(fileId: String) {
        googleDriveService.deleteFileOrFolderById(fileId)
    }

    fun upload(file: MultipartFile, path: String = "Root", isPublic: Boolean): String {
        val authorizationDetails = AuthorizationDetails()
        if (isPublic) {
            authorizationDetails.type = "anyone"
            authorizationDetails.role = "reader"
        } else {
            authorizationDetails.type = "private"
            authorizationDetails.role = "private"
        }
        return googleDriveService.uploadFile(file, path, authorizationDetails)
    }

    fun download(fileId: String, outputStream: OutputStream) {
        googleDriveService.download(fileId, outputStream)
    }

    fun downloads(fileIds: List<String>, outputStream: OutputStream) {
        googleDriveService.downloads(fileIds, outputStream)
    }

    fun copyToFolder(fileId: String, folderId: String) {
        googleDriveService.copy(fileId, folderId)
    }

    fun moveToFolder(fileId: String, folderId: String) {
        googleDriveService.move(fileId, folderId)
    }

    fun shareFile(fileId: String, gmail: String) {
        val authorizationDetails = AuthorizationDetails()
        authorizationDetails.type = "user"
        authorizationDetails.role = "reader"
        googleDriveService.createPermissionForEmail(fileId, authorizationDetails)
    }

}