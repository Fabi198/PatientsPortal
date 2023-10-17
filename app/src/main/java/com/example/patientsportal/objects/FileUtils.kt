package com.example.patientsportal.objects

import android.content.Context
import android.database.Cursor
import android.net.Uri
import java.io.File

object FileUtils {
    fun getPath(context: Context, uri: Uri): String? {
        val projection = arrayOf("_data")
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        val columnIndex: Int? = cursor?.getColumnIndexOrThrow("_data")
        cursor?.moveToFirst()
        val path: String? = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return path ?: getFilePathForNonMediaFile(context, uri)
    }

    private fun getFilePathForNonMediaFile(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver
        val cacheDir = File(context.cacheDir, "uri_cache")
        cacheDir.mkdirs()
        val file = File(cacheDir, uri.lastPathSegment!!)
        if (file.exists()) {
            return file.absolutePath
        }
        contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file.absolutePath
    }
}
