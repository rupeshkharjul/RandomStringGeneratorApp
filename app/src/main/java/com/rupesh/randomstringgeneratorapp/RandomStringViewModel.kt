package com.rupesh.randomstringgeneratorapp

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RandomStringViewModel : ViewModel() {
    var stringDataState = mutableStateListOf<StringData>() /*Use mutable state to update the data as list*/

    fun generateString(contentResolver: ContentResolver, length: Int) {
        
        /* Use Coroutine scope to get data from ContentResolver */
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = Uri.parse(ContentContract.CONTENT_URI)
                val cursor: Cursor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { /*In older version we can not pass bundle*/
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
                        try {

                            val payload = Gson().fromJson(jsonStr, RandomTextPayload::class.java)  // Use Gson to parse the json
                            val randomText = payload.randomText


                            // Switch to main thread to update data on activity
                            withContext(Dispatchers.Main) {
                                stringDataState.add(
                                    0,
                                    StringData(value = randomText.value, length = randomText.length, created = randomText.created)
                                )
                            }

                        } catch (e: Exception) {
                            Log.d("generateString", "Error while json parsing ${e.message}")
                        }
                    }
                }
            } catch (e : Exception){
                Log.d("generateString", "Error when querying content provider ${e.message}")
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