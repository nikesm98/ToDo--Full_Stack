package com.example

import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.routing


import org.litote.kmongo.KMongo


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)

}

fun Application.module() {

    configureSerialization()

    embeddedServer(Netty, port = 8080) {
        val client = KMongo.createClient("mongodb://localhost:27017")
        val database = client.getDatabase("ktor_todo")
        val todoService = TodoService(database)

        install(ContentNegotiation) { json() }
        install(CORS) {
            anyHost()
            allowHeader("Content-Type")
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
        }

        routing {
            todoRoutes(todoService)
        }
    }.start(wait = true)
}
