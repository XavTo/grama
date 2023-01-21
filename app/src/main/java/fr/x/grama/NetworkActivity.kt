package fr.x.grama

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class NetworkActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_duel_wait_connexion)
        launchGame()
        checkButtonLaunchGame()
        checkButtonStopHost()
    }

    private fun checkButtonStopHost() {
        findViewById<Button>(R.id.button_cancel).setOnClickListener {
            NetworkClass.reset()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return@setOnClickListener
        }
    }

    private fun checkButtonLaunchGame() {
        findViewById<Button>(R.id.button_launch_game).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                NetworkClass.sendMessage("start")
                NetworkClass.sendNames()
            }
            NetworkClass.gameStarted = true
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("gameType", 1)
            startActivity(intent)
            finish()
        }
    }

    private fun launchGame() {
        val launchGameButton = findViewById<Button>(R.id.button_launch_game)
        val textWaitHost = findViewById<TextView>(R.id.wait_host_text)
        launchGameButton.visibility = View.INVISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            while (!NetworkClass.gameStarted) {
                val clients = NetworkClass.getNumberOfClients()
                if (clients > 0) {
                    launchGameButton.visibility = View.VISIBLE
                    textWaitHost.text = resources.getString(R.string.connected_clients, clients)
                    delay(100)
                } else {
                    delay(1000)
                }
            }
        }
    }
}