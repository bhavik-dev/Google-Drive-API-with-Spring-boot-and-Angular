package com.example.bhavikDrive.configuration

import com.example.bhavikDrive.utils.DataConstants
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.security.GeneralSecurityException

@Configuration
class BeansConfiguration(
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance(),
    private val SCOPES: List<String> = listOf(DriveScopes.DRIVE),
    private val CREDENTIALS_FILE_PATH: String = DataConstants.GoogleDriveBeansVariables.CREDENTIALS_FILE_PATH,
    private val APPLICATION_NAME: String =  DataConstants.GoogleDriveBeansVariables.APPLICATION_NAME,
    private val TOKENS_DIRECTORY_PATH: String = DataConstants.GoogleDriveBeansVariables.TOKENS_DIRECTORY_PATH
) {

    @Bean
    fun getDrive(): Drive {
        return try {
            val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

            Drive.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName(APPLICATION_NAME).build()

        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @Throws(IOException::class)
    private fun getCredentials(httpTransport: NetHttpTransport): Credential {

        val inputStream = BeansConfiguration::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")

        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))

        val flow = GoogleAuthorizationCodeFlow
            .Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()

        val receiver = LocalServerReceiver.Builder().setPort(7272).build()

        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

}