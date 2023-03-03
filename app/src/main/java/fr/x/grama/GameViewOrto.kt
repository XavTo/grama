package fr.x.grama
import android.animation.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

class GameViewOrto : View {
    private var gameOrto: GameOrto? = null
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)
    private var newStartColor: Int = 0
    private var newEndColor: Int = 0
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
            canvas.drawText("Score: ${it.getScore()}", 0f, 230f, it.paintScore)
            canvas.drawText("Time: ${it.timeLeft}", (it.screenWidth / 2 - it.allTextSize * 2),
                it.screenHeight / 4.5f,
                it.paint
            )
            for (i in 0..2) {
                canvas.save()
                pos = animator?.animatedFraction ?: 0f
                newStartColor = evaluateColor(it.gradientStartColor[i], it.gradientEndColor[i], pos)
                newEndColor = evaluateColor(it.gradientEndColor[i], it.gradientStartColor[i], pos)
                val shadowRect = RectF(it.rect[i])
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
                    1f,
                    1f,
                    Path.Direction.CW
                )
                canvas.drawRect(shadowRect, it.rectPaint2)
                canvas.clipPath(path)
                canvas.drawRect(it.rect[i], it.rectPaint)
                it.paint.getTextBounds(it.textArray[it.wordInd].word[i], 0, it.textArray[it.wordInd].word[i].length, tempRect)
                canvas.drawText(
                    it.textArray[it.wordInd].word[i],
                    it.rect[i].centerX() - tempRect.width() / 2f,
                    it.rect[i].centerY() + tempRect.height() / 2f,
                    it.paint
                )
                canvas.restore()
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
                            it.updateScore(it.getScore() + 1, Color.GREEN, Color.CYAN)
                            performClick()
                        } else if (it.rect[i].contains(event.x.toInt(), event.y.toInt()) && !it.textArray[it.wordInd].good[i]) {
                            it.updateScore(it.getScore() - 1, Color.RED, Color.CYAN)
                            it.gradientStartColor[i] = Color.MAGENTA
                            it.gradientEndColor[i] = Color.RED
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
            (parent as ViewGroup).removeView(this)
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra("score", gameOrto?.getScore())
            intent.putExtra("winner", UserInfo.pseudo)
            intent.putExtra("gameType", 3)
            intent.putExtra("currentGame", 0)
            gameOrto = null
            context.startActivity(intent)
            (context as Activity).finish()
        }
        if (gameOrto?.endTime == true) {
            gameOrto?.correctAnswer()
        }
        invalidate()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    fun destroy() {
        val parentView = this.parent as ViewGroup
        this.visibility = GONE
        parentView.removeView(this)
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }
}
