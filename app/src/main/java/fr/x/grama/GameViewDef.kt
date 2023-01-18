package fr.x.grama

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


class GameViewDef : View {
    private var gameDef: GameDef? = null
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)
    private var textInBox: String = ""
    var path: Path = Path()
    fun setGameDef(gameDef: GameDef) {
        gameDef.setText(this.context)
        this.gameDef = gameDef
    }

    init {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_image)
        val bitmapDrawable = BitmapDrawable(resources, bitmap)
        background = bitmapDrawable
        setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val edittext = EditText(context)
            builder.setView(edittext)
            builder.setPositiveButton("OK") { _, _ ->
                textInBox = edittext.text.toString()
            }
            val dialog: AlertDialog = builder.create()
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val wmlp: WindowManager.LayoutParams = dialog.window!!.attributes
            wmlp.gravity = Gravity.TOP
            wmlp.y = 700
            dialog.show()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        gameDef?.let {
            canvas.drawText("Score: ${it.getScore()}", 0f, 180f, it.paintScore)
            canvas.drawText("Time: ${it.timeLeft}", (it.screenWidth / 2 - 280).toFloat(), 700f, it.paint)
            canvas.drawRect(it.rectAnswer, it.rectPaint2)
            canvas.drawText("Réponse", it.rectAnswer.left.toFloat() + 200, it.rectAnswer.top.toFloat() + 150, it.paint)
            drawTextRect(canvas, it.definition, it.rect)
        }
    }

    fun update() {
        gameDef?.update()
        textInBox = if (textInBox != "" && textInBox.equals(gameDef?.word, true)) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            gameDef!!.updateScore(gameDef!!.getScore() + 1, Color.GREEN, Color.CYAN)
            gameDef!!.correctAnswer()
            ""
        } else if (textInBox != "") {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            ""
        } else
            ""
        invalidate()
    }

    private fun drawTextRect(canvas: Canvas, text: String, rect: Rect) {
        val textWidth = rect.width()
        rect.height()
        val textPaint = TextPaint()
        textPaint.color = Color.BLACK
        textPaint.textSize = 144f
        textPaint.setShadowLayer(2f, 5f, 5f, Color.BLACK)
        val staticLayout = StaticLayout.Builder
            .obtain(text, 0, text.length, textPaint, textWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .build()
        // set text pos
        canvas.save()
        canvas.translate(rect.left.toFloat(), rect.top.toFloat() - 100)
        staticLayout.draw(canvas)
        canvas.restore()
    }
}
