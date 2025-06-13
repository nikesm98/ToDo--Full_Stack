package com.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import org.bson.Document
import org.bson.types.ObjectId

@Serializable
data class Todo(
    val id: String = ObjectId().toHexString(),
    val title: String,
    val completed: Boolean = false
) {
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }
        fun fromDocument(doc: Document): Todo = json.decodeFromString(doc.toJson())
    }
}
