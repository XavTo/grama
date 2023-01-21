package fr.x.grama

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentLinkedQueue

object NetworkClass {
    data class Client(val socket: Socket, val reader: BufferedReader, val writer: PrintWriter, var name: String)

    var scoreReceived: Boolean = false
    var gameStarted: Boolean = false
    private var server = ServerSocket()
    var networkRunning: Boolean = false
    var wordFound: String = ""
    var isClientConnnected: Boolean = false
    var isHost: Boolean = false
    var dataReceived: Boolean = false
    private var listWord: ArrayList<String> = ArrayList()
    private var listDefinition: ArrayList<String> = ArrayList()
    private val namesClients: MutableList<String> = mutableListOf()
    private val clients = ConcurrentLinkedQueue<Client>()
    private var uniqueClient: Client? = null

    fun stop() {
        networkRunning = false
        server.close()
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

    fun reset() {
        server.close()
        clients.forEach { client ->
            client.socket.close()
            client.reader.close()
            client.writer.close()
        }
        clients.clear()
        networkRunning = false
        wordFound = ""
        isClientConnnected = false
    }

    suspend fun start(port: Int) {
        withContext(Dispatchers.IO) {
            server = ServerSocket(port)
            networkRunning = true
            while (networkRunning) {
                println(clients.size)
                println("server waiting for client")
                val client = server.accept()
                clients.add(Client(client, BufferedReader(InputStreamReader(client.getInputStream())), PrintWriter(client.getOutputStream(), true), ""))
                handleClient(clients.size - 1)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun handleClient(index: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            while (networkRunning) {
                val message = clients.elementAt(index).reader.readLine()
                if (message.startsWith("#name :")) {
                    clients.elementAt(index).name = message.substring(7)
                }
                if (message.startsWith("#WordFound :")) {
                    sendMessageSkipSender("#WordFound :${message.substring(12)}")
                }
            }
        }
    }

    suspend fun connect(host: String, port: Int, context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val client = Socket(host, port)
                isClientConnnected = true
                networkRunning = true
                val input = BufferedReader(InputStreamReader(client.getInputStream()))
                val output = PrintWriter(client.getOutputStream(), true)
                uniqueClient = Client(client, input, output, UserInfo.pseudo)
                output.println("#name :" + UserInfo.pseudo)
                handleMessages(context, input, output)
            } catch (e: Exception) {
                println("Failed to connect to server: ${e.message}")
            }
        }
    }

    private fun handleMessages(context: Context, input: BufferedReader, output: PrintWriter) {
        while (isClientConnnected) {
            val message = input.readLine()
            println("Le client reçoit : $message")
            if (message.startsWith("#clients name :")) {
                val names = message.split(":")[1].split(",")
                namesClients.addAll(names)
            }
            if (message.startsWith("#WordFound :")) {
                wordFound = message.split(":")[1]
            }
            if (message.startsWith("#words :")) {
                val words = message.split(":")[1].split("|")
                listWord.addAll(words)
            }
            if (message.startsWith("#definitions :")) {
                val definitions = message.split(":")[1].split("|")
                listDefinition.addAll(definitions)
                dataReceived = true
            }
            if (message == "start") {
                val intent = Intent(context, GameActivity::class.java)
                intent.putExtra("gameType", 1)
                context.startActivity(intent)
            }
            Thread.sleep(1000)
        }
    }

    suspend fun sendMessageToServ(word: String) {
        withContext(Dispatchers.IO) {
            uniqueClient?.writer?.println(word + "|" + UserInfo.pseudo)
        }
    }

    private suspend fun sendMessageSkipSender(s: String) {
        withContext(Dispatchers.IO) {
            val splitS = s.split("|")
            val message = splitS[0]
            val name = splitS[1]
            for (client in clients) {
                if (client.name != name) {
                    client.writer.println(message)
                }
            }
        }
    }

    suspend fun sendMessage(s: String) {
        withContext(Dispatchers.IO) {
            for (client in clients) {
                client.writer.println(s)
            }
        }
    }

    suspend fun sendNames() {
        withContext(Dispatchers.IO) {
            var names = "#clients name :"
            for (client in clients) {
                names = names.plus(client.name)
                names = names.plus(",")
            }
            names = names.substring(0, names.length - 1)
            for (client in clients) {
                client.writer.println(names)
            }
        }
    }

    suspend fun sendGameInfo(listWord: MutableList<String>, listDefinition: MutableList<String>) {
        withContext(Dispatchers.IO) {
            var stringWord = "#words :"
            var stringDefinition = "#definitions :"
            for (word in listWord) {
                stringWord = stringWord.plus(word)
                stringWord = stringWord.plus("|")
            }
            for (definition in listDefinition) {
                stringDefinition = stringDefinition.plus(definition)
                stringDefinition = stringDefinition.plus("|")
            }
            stringWord = stringWord.substring(0, stringWord.length - 1)
            stringDefinition = stringDefinition.substring(0, stringDefinition.length - 1)
            for (client in clients) {
                client.writer.println(stringWord)
                client.writer.println(stringDefinition)
            }
        }
    }

    fun getNumberOfClients(): Int {
        return clients.size
    }

    fun getClientsNames(): MutableList<String> {
        return namesClients
    }

    fun getWord(): MutableList<String> {
        return listWord
    }

    fun getDefinition(): MutableList<String> {
        return listDefinition
    }
}