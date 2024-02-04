package com.example.bhavikDrive.utils

import com.example.bhavikDrive.services.GoogleDriveService
import com.google.api.services.drive.model.File
import com.google.common.io.ByteStreams
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DataConstants {

    object GoogleDriveBeansVariables {
        const val CREDENTIALS_FILE_PATH: String = "/credentials.json"
        const val APPLICATION_NAME: String = "Web client 1"
        const val TOKENS_DIRECTORY_PATH: String = "tokens"
    }

    object GoogleDriveAPISURLS {
        const val FOLDER_MIMETYPE: String = "application/vnd.google-apps.folder"
        const val FOLDER_PATH: String = "https://drive.google.com/drive/u/0/folders/"
        const val FILE_PATH: String = "https://drive.google.com/file/d/"
    }

    object CommonFunction {
        fun zipFiles(files: List<File>, googleDriveService: GoogleDriveService): ByteArray {
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
    }

}