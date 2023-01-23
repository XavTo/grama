package fr.x.grama

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class NetworkActivity  : AppCompatActivity() {
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (ServerClass.networkRunning) {
                ServerClass.reset()
            }
            val intent = Intent(this@NetworkActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_duel_wait_connexion)
        launchGame()
        checkButtonLaunchGame()
        checkButtonStopHost()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun checkButtonStopHost() {
        findViewById<Button>(R.id.button_cancel).setOnClickListener {
            if (ServerClass.networkRunning) {
                ServerClass.reset()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return@setOnClickListener
        }
    }

    private fun checkButtonLaunchGame() {
        findViewById<Button>(R.id.button_launch_game).setOnClickListener {
            val pairData = ServerClass.loadDataForGame(this)
            CoroutineScope(Dispatchers.IO).launch {
                ServerClass.sendNames()
                ServerClass.sendGameInfo(pairData.first, pairData.second)
            }
            findViewById<Button>(R.id.button_launch_game).visibility = View.GONE
            findViewById<Button>(R.id.button_cancel).visibility = View.GONE
            findViewById<TextView>(R.id.wait_host_text).text = "Lancement de la partie..."
            ServerClass.waitForStart = true
            CoroutineScope(Dispatchers.IO).launch {
                while (!ServerClass.isGameLaunched()) {
                    delay(100)
                }
            }
            return@setOnClickListener
        }
    }

    private fun launchGame() {
        val launchGameButton = findViewById<Button>(R.id.button_launch_game)
        val textWaitHost = findViewById<TextView>(R.id.wait_host_text)
        launchGameButton.visibility = View.INVISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            while (!ServerClass.waitForStart) {
                val clients = ServerClass.getNumberOfClients()
                if (clients > 0) {
                    withContext(Dispatchers.Main) {
                        launchGameButton.visibility = View.VISIBLE
                        textWaitHost.text = resources.getString(R.string.connected_clients, clients)
                    }
                    delay(100)
                } else {
                    delay(1000)
                }
            }
        }
    }
}