package io.roadzen.rzcameraandroid.util

interface FileDirectoryProvider {

    fun getPublicDirectory(): String?
    fun getPrivateDirectory(): String
}