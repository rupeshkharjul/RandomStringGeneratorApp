package com.rupesh.randomstringgeneratorapp

import java.util.UUID

data class StringData(
    val id: UUID = UUID.randomUUID(),
    val value: String,
    val length: Int,
    val created: String
)