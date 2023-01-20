package fr.x.grama

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.text.*
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast


class GameViewDef : View {
    private var gameDef: GameDef? = null
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)
    private var textInBox: String = ""
    private var editText: EditText? = null
    var path: Path = Path()
    var network: NetworkClass? = null
    fun setGameDef(gameDef: GameDef, tempEditText: EditText) {
        gameDef.setText(this.context)
        this.gameDef = gameDef
        editText = tempEditText
        editText?.setTextColor(Color.WHITE)
        editText?.gravity = Gravity.CENTER
        val shadow = GradientDrawable()
        shadow.setColor(Color.parseColor("#B3E5FC"))
        shadow.setStroke(5, Color.BLACK)
        shadow.cornerRadius = 10f
        editText?.background = shadow
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        editText?.setOnFocusChangeListener {_, hasFocus -> if (hasFocus) {editText?.hint = "" } }
        editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInBox = s.toString()
            }
        })
    }

    init {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_image)
        val bitmapDrawable = BitmapDrawable(resources, bitmap)
        background = bitmapDrawable
        if (NetworkClass().networkRunning) {
            network = NetworkClass()
            Toast.makeText(context, "Network", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Local", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        gameDef?.let {
            canvas.drawText("Score: ${it.getScore()}", 0f, 180f, it.paintScore)
            canvas.drawText("Time: ${it.timeLeft}", (it.screenWidth / 2 - it.allTextSize * 2), it.screenHeight / 6f, it.paint)
            drawTextRect(canvas, it.definition, it.rect)
        }
    }

    fun update() {
        gameDef?.update()
        if (textInBox != "" && textInBox.equals(gameDef?.word, true)) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            gameDef!!.updateScore(gameDef!!.getScore() + 1, Color.GREEN, Color.CYAN)
            gameDef!!.correctAnswer()
            editText?.setText("")
            if (network?.networkRunning == true && network?.wordFound == false) {
                network!!.sendWord(textInBox)
            }
            textInBox = ""
        }
        invalidate()
    }

    private fun drawTextRect(canvas: Canvas, text: String, rect: Rect) {
        val textWidth = rect.width()
        rect.height()
        val textPaint = TextPaint()
        textPaint.color = Color.BLACK
        textPaint.textSize = GameDef().screenWidth / 12f
        textPaint.setShadowLayer(2f, 2f, 2f, Color.BLACK)
        val staticLayout = StaticLayout.Builder
            .obtain(text, 0, text.length, textPaint, textWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .build()
        canvas.save()
        canvas.translate(rect.left.toFloat(), rect.top.toFloat() - GameDef().screenHeight / 4f)
        staticLayout.draw(canvas)
        canvas.restore()
    }
}
