package fr.x.grama

import android.content.Context
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.ConcurrentLinkedQueue

object ServerClass {
    data class Client(val socket: Socket, val reader: BufferedReader, val writer: PrintWriter, var name: String, var ready: Boolean, var score: Int)

    var waitForStart: Boolean = false
    var networkRunning: Boolean = false
    private var server = ServerSocket()
    private val clients = ConcurrentLinkedQueue<Client>()
    private var gameStarted: Boolean = false

    fun stop() {
        networkRunning = false
        server.close()
    }

    suspend fun start(port: Int) {
        withContext(Dispatchers.IO) {
            server = ServerSocket(port)
            networkRunning = true
            var client: Socket
            while (networkRunning) {
                println(clients.size)
                println("server waiting for client")
                try {
                    client = server.accept()
                } catch (e: SocketException) {
                    println("Server socket closed")
                    return@withContext
                }
                clients.add(Client(client, BufferedReader(
                        InputStreamReader(client.getInputStream())
                    ), PrintWriter(client.getOutputStream(), true), "", false, 0)
                )
                handleClient(clients.size - 1)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun handleClient(index: Int) {
        var message: String
        GlobalScope.launch(Dispatchers.IO) {
            while (networkRunning) {
                try {
                    message = clients.elementAt(index).reader.readLine()
                } catch (e: SocketException) {
                    println("Socket close")
                    return@launch
                } catch (e: Exception) {
                    println("Client disconnected")
                    return@launch
                }
                if (message.startsWith("#name :")) {
                    clients.elementAt(index).name = message.substring(7)
                }
                if (message.startsWith("#WordFound :")) {
                    sendMessageSkipSender("#WordFound :${message.substring(12)}")
                }
                if ((message.startsWith("#ready :")) && (message.substring(8) == "true")) {
                    clients.elementAt(index).ready = true
                }
                if (message.startsWith("#Score :")) {
                    clients.elementAt(index).score = message.split(":")[1].split("|")[0].toInt()
                    sendScore()
                }
            }
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

    fun reset() {
        server.close()
        clients.forEach { client ->
            CoroutineScope(Dispatchers.IO).launch {
                client.writer.println("#end")
            }
            client.socket.close()
            client.reader.close()
            client.writer.close()
        }
        clients.clear()
        networkRunning = false
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

    private suspend fun sendMessage(s: String) {
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

    private suspend fun sendScore() {
        withContext(Dispatchers.IO) {
            var scores = "#score :"
            for (client in clients) {
                scores += "${client.name}|${client.score},"
            }
            scores = scores.substring(0, scores.length - 1)
            for (client in clients) {
                client.writer.println(scores)
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

    fun isGameLaunched(): Boolean {
        for (client in clients) {
            if (!client.ready) {
                return false
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            sendMessage("#start")
        }
        gameStarted = true
        return true
    }

fun loadDataForGame(context: Context): Pair<MutableList<String>, MutableList<String>> {
        val db = DatabaseManager(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseManager.TABLE_DEF}", null)
        val tempWord: MutableList<String> = mutableListOf()
        val tempDef: MutableList<String> = mutableListOf()
        if (cursor.moveToFirst()) {
            do {
                tempDef.add(cursor.getString(1))
                tempWord.add(cursor.getString(2))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        val shuffledList = tempWord.zip(tempDef).shuffled()
        return Pair(shuffledList.map { it.first }.toMutableList(), shuffledList.map { it.second }.toMutableList())
    }
}