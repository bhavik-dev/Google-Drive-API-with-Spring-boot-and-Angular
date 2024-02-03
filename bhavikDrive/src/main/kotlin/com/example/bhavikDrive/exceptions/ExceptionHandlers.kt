package com.example.bhavikDrive.exceptions

import com.example.bhavikDrive.services.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlers {

    @ExceptionHandler(FileNotFoundException::class)
    fun handleFileNotFoundException(exception: FileNotFoundException): ResponseEntity<Any> {
        return ResponseEntity(mapOf("message" to exception.message),null, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(FolderNotFoundException::class)
    fun handleFolderNotFoundException(exception: FolderNotFoundException): ResponseEntity<Any> {
        return ResponseEntity(mapOf("message" to exception.message),null, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(FileDownloadFailedException::class)
    fun handleFileDownloadFailedException(exception: FileDownloadFailedException): ResponseEntity<Any> {
        return ResponseEntity(mapOf("message" to exception.message),null, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(FolderDownloadFailedException::class)
    fun handleFolderDownloadFailedException(exception: FolderDownloadFailedException): ResponseEntity<Any> {
        return ResponseEntity(mapOf("message" to exception.message),null, HttpStatus.NOT_FOUND)
    }

}