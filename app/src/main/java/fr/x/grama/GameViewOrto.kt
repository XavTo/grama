package fr.x.grama
import android.animation.*
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class GameViewOrto : View {
    private var gameOrto: GameOrto? = null
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)
    private var gradientStartColor = Color.CYAN
    private var gradientEndColor = Color.GREEN
    private var newStartColor = Color.CYAN
    private var newEndColor = Color.GREEN
    private var animator: ValueAnimator? = null
    private var pos = 0f
    private var tempRect = Rect()
    var path: Path = Path()

    fun setGameOrto(gameOrto: GameOrto) {
        gameOrto.textArray = gameOrto.getText(this.context)
        gameOrto.textArray.shuffle()
        this.gameOrto = gameOrto
    }

    init {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_image)
        val bitmapDrawable = BitmapDrawable(resources, bitmap)
        background = bitmapDrawable
        startGradientAnimation()
    }

    private fun startGradientAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f)
        animator?.addUpdateListener {
            invalidate()
        }
        animator?.duration = 1000
        animator?.repeatCount = ValueAnimator.INFINITE
        animator?.repeatMode = ValueAnimator.REVERSE
        animator?.start()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        gameOrto?.let {
            canvas.drawText("Score: ${it.getScore()}", 0f, 180f, it.paintScore)
            canvas.drawText("Time: ${it.timeLeft}", (it.screenWidth / 2 - 200).toFloat(), 700f, it.paint)
            for (i in 0..2) {
                pos = animator?.animatedFraction ?: 0f
                newStartColor = evaluateColor(gradientStartColor, gradientEndColor, pos)
                newEndColor = evaluateColor(gradientEndColor, gradientStartColor, pos)
                it.rectPaint.setShadowLayer(20f, 10f, 10f, Color.BLACK)
                it.rectPaint.shader = LinearGradient(
                    it.rect[i].left.toFloat(),
                    it.rect[i].top.toFloat(),
                    it.rect[i].right.toFloat(),
                    it.rect[i].bottom.toFloat(),
                    newStartColor,
                    newEndColor,
                    Shader.TileMode.CLAMP
                )
                path.addRoundRect(
                    it.rect[i].left.toFloat(),
                    it.rect[i].top.toFloat(),
                    it.rect[i].right.toFloat(),
                    it.rect[i].bottom.toFloat(),
                    50f,
                    50f,
                    Path.Direction.CW
                )
                canvas.clipPath(path)
                canvas.drawRect(it.rect[i], it.rectPaint)
                it.paint.getTextBounds(it.textArray[it.wordInd].word[i], 0, it.textArray[it.wordInd].word[i].length, tempRect)
                canvas.drawText(
                    it.textArray[it.wordInd].word[i],
                    it.rect[i].centerX() - tempRect.width() / 2f,
                    it.rect[i].centerY() + tempRect.height() / 2f,
                    it.paint
                )
            }
        }
    }

    private fun evaluateColor(startColor: Int, endColor: Int, fraction: Float): Int {
        val startR = startColor shr 16 and 0xff
        val startG = startColor shr 8 and 0xff
        val startB = startColor and 0xff
        val startA = startColor shr 24 and 0xff
        val endR = endColor shr 16 and 0xff
        val endG = endColor shr 8 and 0xff
        val endB = endColor and 0xff
        val endA = endColor shr 24 and 0xff

        return (startA + (fraction * (endA - startA)).toInt() shl 24) or
                (startR + (fraction * (endR - startR)).toInt() shl 16) or
                (startG + (fraction * (endG - startG)).toInt() shl 8) or
                startB + (fraction * (endB - startB)).toInt()
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                gameOrto?.let {
                    for (i in 0..2) {
                        if (it.rect[i].contains(event.x.toInt(), event.y.toInt()) && it.textArray[it.wordInd].good[i]) {
                            it.textArray[it.wordInd].word[i] = ""
                            updateScore(it.getScore() + 1, Color.GREEN, Color.CYAN)
                            performClick()
                        }
                    }
                }
            }
        }
        return true
    }

    fun update() {
        gameOrto?.update()
        if (gameOrto?.endGame == true) {
            gameOrto?.let {
                val bundle = Bundle()
                bundle.putInt("score", it.getScore())
            }
        }
        invalidate()
    }

    private fun updateScore(newScore: Int, color1: Int, color2: Int) {
        val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), color1, color2)
        colorAnimator.addUpdateListener {
            gameOrto!!.paintScore.color = it.animatedValue as Int
            invalidate()
        }
        colorAnimator.duration = 500
        val values = if (color1 == Color.GREEN) 1f else 1.5f
        val floatV = if (color1 == Color.GREEN) 1.5f else 1f
        val scaleAnimator = ValueAnimator.ofFloat(values, floatV)
        scaleAnimator.addUpdateListener {
            gameOrto!!.paintScore.textSize = (gameOrto!!.allTextSize) * it.animatedValue as Float
            println(gameOrto!!.paintScore.textSize)
            invalidate()
        }
        scaleAnimator.duration = 500
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(colorAnimator, scaleAnimator)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (color1 == Color.GREEN) {
                    updateScore(newScore, color2, Color.GREEN)
                }
                invalidate()
            }
        })
        gameOrto!!.setScore(newScore)
        animatorSet.start()
    }
}
