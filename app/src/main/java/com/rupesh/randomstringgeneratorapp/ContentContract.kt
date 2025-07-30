package com.rupesh.randomstringgeneratorapp

object ContentContract {
    const val CONTENT_URI = "content://com.iav.contestdataprovider/text"

    object JsonEntry {
        const val DATA_OBJ = "data"
        const val RANDOM_TEXT_JSON_OBJ = "randomText"
        const val VALUE = "value"
        const val LENGTH = "length"
        const val CREATED = "created"
    }
}