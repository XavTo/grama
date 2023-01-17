package fr.x.grama

import android.os.Bundle
import android.view.Choreographer
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private var lastFrameTime = 0L
    private var gameViewOrto: GameViewOrto? = null
    private var gameViewDef: GameViewDef? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val gameType = intent.extras!!.getInt("gameType")
        if (gameType == 0) {
            gameViewOrto = findViewById(R.id.gameViewOrto)
            GameOrto.init(this)
            gameViewOrto?.setGameOrto(GameOrto())
        } else if (gameType == 1) {
            gameViewDef = findViewById(R.id.gameViewDef)
            GameDef.init(this)
            gameViewDef?.setGameDef(GameDef())
        }
    }

    override fun onResume() {
        super.onResume()
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                val currentTime = System.nanoTime()
                if (lastFrameTime == 0L) {
                    lastFrameTime = currentTime
                }
                val deltaTime = currentTime - lastFrameTime
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
        Choreographer.getInstance().removeFrameCallback {

        }
    }
}
