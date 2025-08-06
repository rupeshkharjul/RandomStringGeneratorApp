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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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

                        val jsonStr = it.getString(it.getColumnIndexOrThrow(ContentContract.DATA_OBJ))
                        try {

                            val payload = Gson().fromJson(jsonStr, RandomTextPayload::class.java)  // Use Gson to parse the json
                            val randomText = payload.randomText

                            val formattedDate = formatDate(randomText.created)

                            // Switch to main thread to update data on activity
                            withContext(Dispatchers.Main) {
                                stringDataState.add(
                                    StringData(value = randomText.value, length = randomText.length, created = formattedDate)
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

    private fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat(AppConstants.ISO_DATE_FORMAT, Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone(AppConstants.UTC_TIME_ZONE)
            val date = inputFormat.parse(inputDate)
            val outputFormat = SimpleDateFormat(AppConstants.SIMPLE_DATE_FORMAT, Locale.getDefault())
            outputFormat.format(date ?: "")
        } catch (e: Exception) {
            Log.d("DateParsing", "Error when parsing date ${e.message}")
            ""
        }
    }

    fun clearAll() {
        stringDataState.clear()
    }

    fun delete(entry: StringData) {
        stringDataState.remove(entry)
    }
}