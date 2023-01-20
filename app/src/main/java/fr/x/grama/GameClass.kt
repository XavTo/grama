package fr.x.grama

import android.animation.*
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint

open class GameClass {
    companion object {
        private lateinit var resources: Resources
        fun init(context: Context) {
            resources = context.resources
        }
    }
    var sco: Int = 0
    var endGame: Boolean = false
    val screenWidth = resources.displayMetrics.widthPixels
    val screenHeight = resources.displayMetrics.heightPixels
    val allTextSize = 140f / 1080f * screenWidth
    var timeLeft: Float = 0f

    val paint = Paint().apply {
        color = Color.rgb(0, 150, 255)
        setShadowLayer(5f, 5f, 5f, Color.BLACK)
        textSize = allTextSize
    }
    val paintScore = Paint().apply {
        color = Color.GREEN
        textSize = allTextSize
        setShadowLayer(5f, 5f, 5f, Color.BLACK)
    }
    val rectPaint = Paint().apply {
        color = Color.CYAN
        strokeWidth = 5f
        style = Paint.Style.FILL
        setShadowLayer(10f, 5f, 5f, Color.BLACK)
    }
    val rectPaint2 = Paint().apply {
        color = Color.GREEN
        strokeWidth = 5f
        style = Paint.Style.FILL
        setShadowLayer(20f, 20f, 20f, Color.BLACK)
    }

    fun getScore(): Int {
        return sco
    }

    private fun setScore(newScore: Int) {
        this.sco = newScore
    }

    fun updateScore(newScore: Int, color1: Int, color2: Int) {
        val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), color1, color2)
        colorAnimator.addUpdateListener {
            paintScore.color = it.animatedValue as Int
        }
        colorAnimator.duration = 500
        val values = if (color1 == Color.GREEN) 1f else 1.5f
        val floatV = if (color1 == Color.GREEN) 1.5f else 1f
        val scaleAnimator = ValueAnimator.ofFloat(values, floatV)
        scaleAnimator.addUpdateListener {
            paintScore.textSize = (allTextSize) * it.animatedValue as Float
        }
        scaleAnimator.duration = 500
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(colorAnimator, scaleAnimator)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (color1 == Color.GREEN) {
                    updateScore(newScore, color2, Color.GREEN)
                }
            }
        })
        setScore(newScore)
        animatorSet.start()
    }
}