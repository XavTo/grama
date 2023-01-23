package fr.x.grama

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private var lastFrameTime = 0L
    private var gameViewOrto: GameViewOrto? = null
    private var gameViewDef: GameViewDef? = null
    private var endGame: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        val gameType = intent.extras!!.getInt("gameType")
        GameClass.init(this)
        when (gameType) {
            0 -> {
                setContentView(R.layout.activity_game_orto)
                gameViewOrto = findViewById(R.id.gameViewOrto)
                gameViewOrto?.setGameOrto(GameOrto())
                println("GameActivity: onCreate: gameType = 0")
            }
            1 -> {
                setContentView(R.layout.activity_game_def)
                gameViewDef = findViewById(R.id.gameViewDef)
                val editText = findViewById<EditText>(R.id.edit_text)
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    editText?.requestFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                }, 200)
                gameViewDef?.setGameDef(GameDef(), editText)
            }
            2 -> {
                intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra("id", 1)
                intent.putExtra("tag", "setting")
                startActivity(intent)
                finish()
            }
            else -> {
                val score = intent.extras!!.getInt("score")
                setContentView(R.layout.fragment_wait_host)
            }
        }
    }

    override fun onResume() {
        super.onResume()
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
        Choreographer.getInstance().removeFrameCallback {}
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            gameViewDef?.destroy()
            gameViewOrto?.destroy()
            endGame = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameViewDef?.visibility = View.GONE
        gameViewOrto?.visibility = View.GONE
    }
}
