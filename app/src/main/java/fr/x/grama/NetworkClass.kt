package fr.x.grama

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class NetworkClass {
    var server = ServerSocket()
    var networkRunning: Boolean = false
    var wordFound: Boolean = false
    private var input: BufferedReader? = null
    private var output: PrintWriter? = null
    private var clients = mutableListOf<Socket>()

    fun start(port: Int) {
        server = ServerSocket(port)
        networkRunning = true
        while(networkRunning) {
            println("server waiting for client")
            val client = server.accept()
            handleClient(client)
        }

    }

    fun stop() {
        networkRunning = false
        server.close()
    }

    private fun handleClient(client: Socket) {
        clients.add(client)
        println("Client connected: ${client.inetAddress.hostAddress}")
        thread {
            val input = BufferedReader(InputStreamReader(client.getInputStream()))
            val output = PrintWriter(client.getOutputStream(), true)
            while (networkRunning) {
                val message = input.readLine()
                if (message == null) {
                    println("Client disconnected: ${client.inetAddress.hostAddress}")
                    clients.remove(client)
                    client.close()
                    break
                }
                println("Message received: $message")
                output.println("Message received: $message")
            }
        }
    }

    fun connect(host: String, port: Int) {
        try {
            val client = Socket(host, port)
            println("Connected to server")
            input = BufferedReader(InputStreamReader(client.getInputStream()))
            output = PrintWriter(client.getOutputStream(), true)
            val message = "Hello from client"
            output!!.println(message)
            val response = input!!.readLine()
            println("Message received: $response")
        } catch (e: Exception) {
            println("Failed to connect to server: ${e.message}")
        }
    }

    fun checkPort(port: Int): Int {
        return try {
            val server = ServerSocket(port)
            server.close()
            0
        }
        catch (e: SecurityException) {
            1
        }
        catch (e: Exception) {
            2
        }
    }

    fun sendWord(textInBox: String) {
        output!!.println(textInBox)
        val response = input!!.readLine()
        wordFound = response == "true"
    }
}