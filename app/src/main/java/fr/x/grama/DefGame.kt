package fr.x.grama

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer

class GameDef {
    companion object {
        private lateinit var resources: Resources
        fun init(context: Context) {
            resources = context.resources
        }
        var score: Int = 0
    }

    val screenWidth = resources.displayMetrics.widthPixels
    private val screenHeight = resources.displayMetrics.heightPixels
    var endGame: Boolean = false
    private val allTextSize = 140f
    val paint = Paint().apply {
        color = Color.rgb(0, 150, 255)
        setShadowLayer(5f, 5f, 5f, Color.BLACK)
        textSize = allTextSize
    }

    var word: String = ""
    var definition: String = ""
    var goodWord: Boolean = false
    var timeLeft: Float = 0f
    private val timer = object : CountDownTimer(5000, 100) {
        override fun onTick(millisUntilFinished: Long) {
            timeLeft = (millisUntilFinished / 1000f).toString().substring(0, 3).toFloat()
        }
        override fun onFinish() {
            endGame = true
        }
    }.start()

    fun getScore(): String {
        return score.toString()
    }

    fun update() {
        if (timeLeft <= 0f) {
            endGame = true
        }
        if (goodWord) {
            score++
            goodWord = false
            timer.start()
        }
    }
}