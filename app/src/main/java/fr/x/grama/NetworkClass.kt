package fr.x.grama

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

object NetworkClass {
    data class Client(val socket: Socket, val reader: BufferedReader, val writer: PrintWriter, var name: String)

    var wordFound: String = ""
    var isClientConnnected: Boolean = false
    private var listWord: ArrayList<String> = ArrayList()
    private var listDefinition: ArrayList<String> = ArrayList()
    private var uniqueClient: Client? = null
    private val infoClients: MutableList<Pair<String, Int>> = mutableListOf()

    suspend fun connect(host: String, port: Int, context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val client = Socket(host, port)
                isClientConnnected = true
                val input = BufferedReader(InputStreamReader(client.getInputStream()))
                val output = PrintWriter(client.getOutputStream(), true)
                uniqueClient = Client(client, input, output, UserInfo.pseudo)
                output.println("#name :" + UserInfo.pseudo)
                handleMessages(context, input)
            } catch (e: Exception) {
                println("Failed to connect to server: ${e.message}")
            }
        }
    }

    private fun handleMessages(context: Context, input: BufferedReader) {
        while (isClientConnnected) {
            val message = input.readLine()
            if (message == "#end" || message == null) {
                disconnect()
                return
            }
            if (message.startsWith("#clients name :")) {
                val names = message.split(":")[1].split(",")
                for (name in names) {
                    infoClients.add(Pair(name, 0))
                }
            }
            if (message.startsWith("#WordFound :")) {
                wordFound = message.split(":")[1]
            }
            if (message.startsWith("#words :")) {
                listWord.clear()
                val words = message.split(":")[1].split("|")
                listWord.addAll(words)
            }
            if (message.startsWith("#definitions :")) {
                listDefinition.clear()
                val definitions = message.split(":")[1].split("|")
                listDefinition.addAll(definitions)
                CoroutineScope(Dispatchers.IO).launch {
                    uniqueClient?.writer?.println("#ready :true")
                }
            }
            if (message.startsWith("#score :")) {
                val scores = message.split(":")[1].split(",")
                for (score in scores) {
                    val name = score.split("|")[0]
                    val value = score.split("|")[1].toInt()
                    for (info in infoClients) {
                        if (info.first == name) {
                            infoClients[infoClients.indexOf(info)] = Pair(info.first, value)
                        }
                    }
                }
            }
            if (message == "#start") {
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

    fun getWord(): MutableList<String> {
        return listWord
    }

    fun getDefinition(): MutableList<String> {
        return listDefinition
    }

    fun getScore(): MutableList<Pair<String, Int>> {
        return infoClients
    }

    fun disconnect() {
        uniqueClient?.socket?.close()
        uniqueClient?.reader?.close()
        uniqueClient?.writer?.close()
        isClientConnnected = false
    }
}