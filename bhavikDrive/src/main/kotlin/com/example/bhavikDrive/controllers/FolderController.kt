package com.example.bhavikDrive.controllers

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor

import com.example.bhavikDrive.models.FolderModel
import com.example.bhavikDrive.services.FolderService
import io.swagger.annotations.ApiOperation


@RestController
@RequestMapping("/google/drive/folders")
@RequiredArgsConstructor
@Tag(name = "Google Drive Folder", description = "Google Drive Folders API")
class FolderController(
    private val folderService: FolderService
) {

    @GetMapping
    @ApiOperation(
        value = "Retrieves all Folders from Google Drive",
        notes = "Retrieves Google Drive Folders",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "GET"
    )
    fun findAll(): ResponseEntity<List<FolderModel>> {
        return ResponseEntity.ok(folderService.findAll())
    }

    @GetMapping("/{folderName}")
    @ApiOperation(
        value = "Retrieves a folder by its name",
        notes = "Retrieves Google Drive folder",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "GET"
    )
    fun getFolderId(@PathVariable folderName: String): FolderModel {
        return folderService.getFolderId(folderName)
    }

    @PostMapping("/create", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(
        value = "Creates a folder in a Google Drive",
        notes = "Creates Google Drive folder",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "POST"
    )
    fun create(@RequestParam("folderName") folderName: String, @RequestParam("parentId") parentId: String): FolderModel {
        return folderService.create(folderName, parentId)
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(
        value = "Deletes a folder by id from a Google Drive",
        notes = "Deletes Google Drive folder",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "DELETE"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) {
        folderService.delete(id)
    }

    @GetMapping(value = ["/download/{id}"], produces = ["application/zip"])
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Downloads a folder by Id from a Google Drive",
        notes = "Downloads Google Drive folder",
        produces = MediaType.ALL_VALUE,
        httpMethod = "GET"
    )
    fun download(@PathVariable id: String): ResponseEntity<ByteArray> {
        val headers = HttpHeaders()
        val filename = String.format("%s.zip", id)
        headers.add("Content-Disposition", "inline; filename=$filename")
        return ResponseEntity.ok().headers(headers).contentType(MediaType.valueOf("application/zip"))
            .body(folderService.download(id))
    }

    @PostMapping(value = ["/downloads"], produces = ["application/zip"])
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Downloads list of folders by Id from a Google Drive",
        notes = "Downloads list of Google Drive folders",
        produces = MediaType.ALL_VALUE,
        httpMethod = "POST"
    )
    fun downloads(@RequestBody ids: List<String>): ResponseEntity<ByteArray> {
        val headers = HttpHeaders()
        val filename = String.format("%s.zip", ids)
        headers.add("Content-Disposition", "inline; filename=$filename")
        return ResponseEntity.ok().headers(headers).contentType(MediaType.valueOf("application/zip"))
            .body(folderService.downloads(ids))
    }

    @GetMapping("/{fromFolderId}/move/{toFolderId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Moves a folder by Id to a folder in a Google Drive",
        notes = "Moves Google Drive folder to a folder",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "GET"
    )
    fun move(@PathVariable fromFolderId: String, @PathVariable toFolderId: String) {
        folderService.moveFolderToAnother(fromFolderId, toFolderId)
    }

    @GetMapping("/{fromFolderId}/copy/{toFolderId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Copy a folder by Id to a folder in a Google Drive",
        notes = "Copy Google Drive folder to a folder",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "GET"
    )
    fun copy(@PathVariable fromFolderId: String, @PathVariable toFolderId: String) {
        folderService.copyFolderToAnother(fromFolderId, toFolderId)
    }

    @GetMapping("/{folderId}/authorization/{gmail}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Authorizes a folder for a gmail in a Google Drive",
        notes = "Authorizes Google Drive folder for a gmail",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "GET"
    )
    fun authorization(@PathVariable folderId: String, @PathVariable gmail: String) {
        folderService.shareFolder(folderId, gmail)
    }
}