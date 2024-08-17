package jp.cordea.closet.repository

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThumbnailRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun insert(uri: Uri): String {
        val id = UUID.randomUUID().toString()
        context.contentResolver.openInputStream(uri)?.use { input ->
            context.openFileOutput(id, Context.MODE_PRIVATE)
                .use { output ->
                    input.copyTo(output)
                }
        }
        return File(context.filesDir, id).absolutePath
    }

    fun delete(url: String) {
        context.deleteFile(url)
    }
}
