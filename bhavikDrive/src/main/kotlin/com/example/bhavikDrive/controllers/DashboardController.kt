package com.example.bhavikDrive.controllers

import com.example.bhavikDrive.configuration.BeansConfiguration
import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/signIn")
@Tag(name = "Dashboard", description = "Google Drive Dashboard")
class DashboardController(
    @Autowired
    private val beansConfiguration: BeansConfiguration
)  {

    @GetMapping
    @ApiOperation(
        value = "SigIn to Google Drive",
        notes = "Google Drive SigIn",
        produces = MediaType.APPLICATION_JSON_VALUE,
        httpMethod = "GET"
    )
    fun signIn(): String {
        return beansConfiguration.getDrive().baseUrl
    }

}