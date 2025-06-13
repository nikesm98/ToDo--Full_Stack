package com.example

import com.mongodb.client.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import org.slf4j.event.*

fun Route.todoRoutes(service: TodoService){
    val logger = LoggerFactory.getLogger("TodoRoutes")

    route("/todos") {

        // GET all todos
        get {
            try {
                val todos = service.getAll()
                call.respond(HttpStatusCode.OK, todos)
            } catch (e: Exception) {
                logger.error("Error fetching todos: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Failed to fetch todos")
            }
        }

        // POST a new todo
        post {
            try {
                val todo = call.receive<Todo>()
                service.create(todo)
                call.respond(HttpStatusCode.Created, todo)
            } catch (e: Exception) {
                logger.error("Error creating todo: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, "Invalid todo data")
            }
        }

        // PUT (update) a todo by ID
        put("/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing todo ID")
                return@put
            }

            try {
                val updatedTodo = call.receive<Todo>()
                val result = service.update(id, updatedTodo)

                if (result != null) {
                    call.respond(HttpStatusCode.OK, result)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Todo not found with ID $id")
                }
            } catch (e: Exception) {
                logger.error("Error updating todo: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, "Invalid update payload")
            }
        }

        // DELETE a todo by ID
        delete("/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing todo ID")
                return@delete
            }

            try {
                val deleted = service.delete(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Todo deleted successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Todo not found with ID $id")
                }
            } catch (e: Exception) {
                logger.error("Error deleting todo: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Failed to delete todo")
            }
        }
    }
}
