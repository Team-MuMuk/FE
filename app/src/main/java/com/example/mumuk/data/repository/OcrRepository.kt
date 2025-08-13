package com.example.mumuk.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.recommend.OcrResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class OcrRepository(private val context: Context) {
    private val recipeApiService = RetrofitClient.getRecipeApiService(context)

    suspend fun uploadImageForOcr(imageUri: Uri): OcrResponse {
        val filePart = createMultipartBodyPart(imageUri, "image")
        return recipeApiService.postOcrImage(filePart)
    }

    private fun createMultipartBodyPart(uri: Uri, partName: String): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Failed to open InputStream for URI: $uri")

        // 파일 이름 가져오기
        val cursor = contentResolver.query(uri, null, null, null, null)
        val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor?.moveToFirst()
        val fileName = nameIndex?.let { cursor.getString(it) } ?: "upload.jpg"
        cursor?.close()

        // RequestBody 생성
        val requestBody = inputStream.readBytes().toRequestBody(
            contentResolver.getType(uri)?.toMediaTypeOrNull()
        )

        return MultipartBody.Part.createFormData(partName, fileName, requestBody)
    }
}