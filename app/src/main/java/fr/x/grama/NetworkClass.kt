package fr.x.grama

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class NetworkClass {
    fun client(ip: String, port: Int) {
        println()
        val client = Socket(ip, port)
        val output = PrintWriter(client.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(client.inputStream))

        println("Client sending [Hello]")
        output.println("Hello")
        println("Client receiving [${input.readLine()}]")
        client.close()
    }

    fun server(port: Int) {
        val server = ServerSocket(port)
        val client = server.accept()
        val output = PrintWriter(client.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(client.inputStream))

        output.println("${input.readLine()} back")
    }
}