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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GameViewDef : View {
    private var gameDef: GameDef? = null
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)
    private var textInBox: String = ""
    private var editText: EditText? = null
    var path: Path = Path()
    fun setGameDef(gameDef: GameDef, tempEditText: EditText) {
        if (!NetworkClass.networkRunning || NetworkClass.isHost) {
            gameDef.setText(this.context)
            if (NetworkClass.networkRunning && NetworkClass.isHost) {
                CoroutineScope(Dispatchers.IO).launch {
                    NetworkClass.sendGameInfo(gameDef.listWord, gameDef.listDefinition)
                }
            }
        }
        this.gameDef = gameDef
        setEditText(tempEditText)
    }

    private fun setEditText(tempEditText: EditText) {
        editText = tempEditText
        editText?.setTextColor(Color.BLACK)
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
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        gameDef?.let {
            if (NetworkClass.networkRunning) {
                if (NetworkClass.scoreReceived) {
                    var y = 180f
                    for ((ind, name) in NetworkClass.getClientsNames().withIndex()) {
                        canvas.drawText(name, 0f, y, it.paintScoreLittle)
                        y += 90f
                    }
                }
            } else {
                canvas.drawText("Score: ${it.getScore()}", 0f, 180f, it.paintScore)
            }
            canvas.drawText("Time: ${it.timeLeft}", (it.screenWidth / 2 - it.allTextSize * 2), it.screenHeight / 6f, it.paint)
            drawTextRect(canvas, it.definition, it.rect)
        }
    }

    fun update() {
        gameDef?.update()
        if (NetworkClass.networkRunning && NetworkClass.dataReceived && !NetworkClass.isHost) {
            this.gameDef!!.listWord = NetworkClass.getWord()
            this.gameDef!!.listDefinition = NetworkClass.getDefinition()
            gameDef!!.word = gameDef!!.listWord[0]
            gameDef!!.definition = gameDef!!.listDefinition[0]
            NetworkClass.dataReceived = false
        }
        if (textInBox != "" && textInBox.equals(gameDef?.word, true)) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            gameDef!!.updateScore(gameDef!!.getScore() + 1, Color.GREEN, Color.CYAN)
            gameDef!!.correctAnswer()
            editText?.setText("")
            textInBox = ""
            if (NetworkClass.networkRunning) {
                CoroutineScope(Dispatchers.IO).launch {
                    NetworkClass.sendMessageToServ("#WordFound :${gameDef!!.word}")
                }
            }
        } else if (NetworkClass.networkRunning && NetworkClass.wordFound != "") {
            Toast.makeText(context, "Le mot était ${gameDef!!.word}", Toast.LENGTH_SHORT).show()
            gameDef!!.correctAnswer()
            editText?.setText("")
            textInBox = ""
            NetworkClass.wordFound = ""
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
