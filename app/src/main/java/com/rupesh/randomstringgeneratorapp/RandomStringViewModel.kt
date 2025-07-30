package com.rupesh.randomstringgeneratorapp

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import org.json.JSONObject

class RandomStringViewModel : ViewModel() {
    var stringDataState = mutableStateListOf<StringData>()

    fun generateString(contentResolver: ContentResolver, length: Int) {
        val uri = Uri.parse(ContentContract.CONTENT_URI)
        val cursor: Cursor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bundle = Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_LIMIT, length)
            }
            contentResolver.query(uri, null, bundle, null)
        } else {
            contentResolver.query(uri, null, null, null, null)
        }


        cursor?.use {
            if (it.moveToFirst()) {
                val jsonStr = it.getString(it.getColumnIndexOrThrow(ContentContract.JsonEntry.DATA_OBJ))
                val json = JSONObject(jsonStr).getJSONObject(ContentContract.JsonEntry.RANDOM_TEXT_JSON_OBJ)
                val value = json.getString(ContentContract.JsonEntry.VALUE)
                val len = json.getInt(ContentContract.JsonEntry.LENGTH)
                val created = json.getString(ContentContract.JsonEntry.CREATED)
                stringDataState.add(0, StringData(value = value, length = len, created = created))
            }
        }
    }

    fun clearAll() {
        stringDataState.clear()
    }

    fun delete(entry: StringData) {
        stringDataState.remove(entry)
    }
}