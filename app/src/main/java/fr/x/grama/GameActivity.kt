package fr.x.grama

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {
    private var lastFrameTime = 0L
    private var gameViewOrto: GameViewOrto? = null
    private var gameViewDef: GameViewDef? = null
    private var endGame: Boolean = false
    private lateinit var gramaClass: GramaClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gramaClass = applicationContext as GramaClass
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        val gameType = intent.extras!!.getInt("gameType")
        GameClass.init(this)
        when (gameType) {
            0 -> {
                setContentView(R.layout.activity_game_orto)
                gameViewOrto = findViewById(R.id.gameViewOrto)
                gameViewOrto?.setGameOrto(GameOrto())
            }
            1 -> {
                setContentView(R.layout.activity_game_def)
                gameViewDef = findViewById(R.id.gameViewDef)
                val editText = findViewById<EditText>(R.id.edit_text)
                val deleteButton = findViewById<Button>(R.id.button_delete)
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    editText?.requestFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                }, 200)
                gameViewDef?.setGameDef(GameDef(), editText, deleteButton)
            }
            2 -> {
                finish()
            }
            else -> {
                setContentView(R.layout.fragment_win_game)
                val textView = findViewById<View>(R.id.victory_message) as android.widget.TextView
                val yourScore = findViewById<View>(R.id.your_score) as android.widget.TextView
                val winner = intent.extras!!.getString("winner")
                textView.text = getString(R.string.winner, winner, intent.extras!!.getInt("score"))
                if (NetworkClass.isClientConnnected && winner != UserInfo.pseudo) {
                    yourScore.text = getString(R.string.your_score, intent.extras!!.getInt("yourScore"))
                    yourScore.visibility = View.VISIBLE
                } else {
                    yourScore.visibility = View.GONE
                }
                findViewById<Button>(R.id.return_button).setOnClickListener {
                    if (NetworkClass.isClientConnnected) {
                        CoroutineScope(Dispatchers.IO).launch {
                            NetworkClass.sendMessageToServ("#Leave")
                        }
                    }
                    if (ServerClass.networkRunning) {
                        ServerClass.reset()
                    }
                    val newIntent = Intent(this, MainActivity::class.java)
                    startActivity(newIntent)
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        gramaClass.startMusic()
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                if (endGame) {
                    if (NetworkClass.isClientConnnected) {
                        NetworkClass.disconnect()
                    }
                    if (ServerClass.networkRunning) {
                        ServerClass.reset()
                    }
                    val intent = Intent(this@GameActivity, MainActivity::class.java)
                    intent.putExtra("tag", "game")
                    startActivity(intent)
                    finish()
                    return
                }
                val currentTime = System.nanoTime()
                if (lastFrameTime == 0L) {
                    lastFrameTime = currentTime
                }
                lastFrameTime = currentTime
                if (gameViewOrto != null) {
                    gameViewOrto?.update()
                } else if (gameViewDef != null) {
                    gameViewDef?.update()
                }
                Choreographer.getInstance().postFrameCallback(this)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        gramaClass.stopMusic()
        Choreographer.getInstance().removeFrameCallback {}
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (NetworkClass.isClientConnnected) {
                CoroutineScope(Dispatchers.IO).launch {
                    NetworkClass.sendMessageToServ("#disconnect")
                }
            }
            gameViewDef = null
            gameViewOrto = null
            endGame = true
            val intent = Intent(this@GameActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameViewDef?.visibility = View.GONE
        gameViewOrto?.visibility = View.GONE
    }
}
