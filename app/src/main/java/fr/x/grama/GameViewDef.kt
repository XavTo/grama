package fr.x.grama

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View

class GameViewDef : View {
    private var gameDef: GameDef? = null
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    fun setGameDef(gameDef: GameDef) {
        this.gameDef = gameDef
    }

    init {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_image)
        val bitmapDrawable = BitmapDrawable(resources, bitmap)
        background = bitmapDrawable
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        gameDef?.let {
            canvas.drawText("Score: ${it.getScore()}", 100f, 100f, it.paint)
            canvas.drawText("Time: ${it.timeLeft}", (it.screenWidth / 2 - 200).toFloat(), 700f, it.paint)
        }
    }

    fun update() {
        gameDef?.update()
        invalidate()
    }
}