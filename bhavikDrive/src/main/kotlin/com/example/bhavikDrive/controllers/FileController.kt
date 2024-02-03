package com.example.bhavikDrive.controllers

import com.example.bhavikDrive.models.FileModel
import com.example.bhavikDrive.services.FileService
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import lombok.RequiredArgsConstructor
import io.swagger.v3.oas.annotations.tags.Tag
import kotlin.Throws
import java.io.IOException
import kotlin.String

import jakarta.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/google/drive/files")
@RequiredArgsConstructor
@Tag(name = "Google Drive Files", description = "Google Drive Files API")
class FileController(
    private val fileService: FileService
) {

    @GetMapping
    @ApiOperation(
        value = "Retrieves all files from Google Drive",
        notes = "Retrieves Google Drive files",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "GET"
    )
    fun findAll(): ResponseEntity<List<FileModel>> {
        return ResponseEntity.ok(fileService.findAll())
    }

    @GetMapping("/{folderId}")
    @ApiOperation(
        value = "Retrieves a file by Id from a folder",
        notes = "Retrieves Google Drive file",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "GET"
    )
    fun findAllInFolder(@PathVariable folderId: String): ResponseEntity<List<FileModel>> {
        return ResponseEntity.ok(fileService.findAllInFolder(folderId))
    }

    @PostMapping(value = ["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ApiOperation(
        value = "Uploads a file to a Google Drive",
        notes = "Uploads Google Drive file",
        produces = MediaType.MULTIPART_FORM_DATA_VALUE,
        httpMethod = "POST"
    )
    fun upload(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("path") path: String,
        @RequestParam("shared") shared: String
    ): String {
        return fileService.upload(file, path, shared.toBoolean())
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(
        value = "Deletes a file by id from a Google Drive",
        notes = "Deletes Google Drive file",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "DELETE"
    )
    fun delete(@PathVariable id: String) {
        fileService.deleteById(id)
    }

    @GetMapping("/download/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Downloads a file by Id from a Google Drive",
        notes = "Downloads Google Drive file",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "GET"
    )
    @Throws(
        IOException::class
    )
    fun download(@PathVariable id: String, response: HttpServletResponse) {
        fileService.download(id, response.outputStream)
    }

    @GetMapping("/downloads/{ids}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Downloads a files by Ids from a Google Drive",
        notes = "Downloads Google Drive files",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "GET"
    )
    @Throws(
        IOException::class
    )
    fun downloads(@PathVariable ids: List<String>, response: HttpServletResponse) {
        fileService.downloads(ids, response.outputStream)
    }

    @GetMapping("/{fileId}/copy/{folderId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Copy a file by Id to a folder in a Google Drive",
        notes = "Copy Google Drive file to a folder",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "GET"
    )
    fun copy(@PathVariable fileId: String, @PathVariable folderId: String) {
        fileService.copyToFolder(fileId, folderId)
    }

    @GetMapping("/{fileId}/move/{folderId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Moves a file by Id to a folder in a Google Drive",
        notes = "Moves Google Drive file to a folder",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "GET"
    )
    fun move(@PathVariable fileId: String, @PathVariable folderId: String) {
        fileService.moveToFolder(fileId, folderId)
    }

    @PostMapping("/{fileId}/authorization/{gmail}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Authorizes a file for a gmail in a Google Drive",
        notes = "Authorizes Google Drive file for a gmail",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "POST"
    )
    fun authorization(@PathVariable fileId: String, @PathVariable gmail: String) {
        fileService.shareFile(fileId, gmail)
    }

}