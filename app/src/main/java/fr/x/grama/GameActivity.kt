package fr.x.grama

import android.os.Bundle
import android.view.Choreographer
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private var lastFrameTime = 0L
    private var gameView: GameView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        gameView = findViewById(R.id.game_view)
        GameOrto.init(this)
        gameView?.setGameOrto(GameOrto())
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
                gameView?.update()
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
