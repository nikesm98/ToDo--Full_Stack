package com.example

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId

class TodoService(private val database: MongoDatabase) {
    private val collection = database.getCollection("todos")

    suspend fun getAll(): List<Todo> = withContext(Dispatchers.IO) {
        collection.find().map { Todo.fromDocument(it) }.toList()
    }

    suspend fun create(todo: Todo): String = withContext(Dispatchers.IO) {
        val doc = todo.toDocument()
        collection.insertOne(doc)
        doc["_id"].toString()
    }

    suspend fun update(id: String, todo: Todo): Todo? = withContext(Dispatchers.IO) {
        val updated = todo.copy(id = id)
        collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), updated.toDocument())
        updated
    }

    suspend fun delete(id: String): Boolean = withContext(Dispatchers.IO) {
        collection.deleteOne(Filters.eq("_id", ObjectId(id))).deletedCount > 0
    }
}
