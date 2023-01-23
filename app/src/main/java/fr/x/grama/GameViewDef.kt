package fr.x.grama

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.text.*
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GameViewDef : View {
    var gameDef: GameDef? = null

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)
    private var textInBox: String = ""
    private var editText: EditText? = null
    var path: Path = Path()
    fun setGameDef(tempGameDef: GameDef, tempEditText: EditText) {
        if (!NetworkClass.isClientConnnected) {
            tempGameDef.setText(this.context)
        } else {
            tempGameDef.listDefinition = NetworkClass.getDefinition()
            tempGameDef.listWord = NetworkClass.getWord()
            tempGameDef.word = tempGameDef.listWord[0]
            tempGameDef.definition = tempGameDef.listDefinition[0]
        }
        this.gameDef = tempGameDef
        println("GameViewDef : ${gameDef!!.listWord} - ${gameDef!!.listDefinition}")
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
        editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editText?.hint = ""
            }
        }
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
            if (NetworkClass.isClientConnnected) {
                val scoreEn = NetworkClass.getScore()
                val yPos = 100f
                for (score in scoreEn) {
                    if (score.first == UserInfo.pseudo) {
                        continue
                    }
                    canvas.drawText(
                        "${score.first} : ${score.second}",
                        it.screenWidth / 1.5f,
                        yPos,
                        it.paintScoreLittle
                    )
                    yPos + 50f
                }
            }
            canvas.drawText("Score: ${it.getScore()}", 0f, 180f, it.paintScore)
            canvas.drawText(
                "Time: ${it.timeLeft}",
                (it.screenWidth / 2 - it.allTextSize * 2),
                it.screenHeight / 6f,
                it.paint
            )
            drawTextRect(canvas, it.definition, it.rect)
        }
    }

    fun update() {
        gameDef?.update()
        if (gameDef?.endTime == true) {
            gameDef?.correctAnswer()
            return
        }
        if (gameDef?.endGame == true) {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra("score", gameDef?.getScore())
            intent.putExtra("gameType", "3")
            context.startActivity(intent)
        }
        if (textInBox != "" && textInBox.equals(gameDef?.word, true)) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            gameDef!!.updateScore(gameDef!!.getScore() + 1, Color.GREEN, Color.CYAN)
            gameDef!!.correctAnswer()
            editText?.setText("")
            textInBox = ""
            if (NetworkClass.isClientConnnected) {
                CoroutineScope(Dispatchers.IO).launch {
                    NetworkClass.sendMessageToServ("#WordFound :${gameDef!!.word}")
                    NetworkClass.sendMessageToServ("#Score :${gameDef!!.getScore()}")
                }
            }
        } else if (NetworkClass.isClientConnnected && NetworkClass.wordFound != "") {
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

    fun destroy() {
        if (NetworkClass.isClientConnnected) {
            CoroutineScope(Dispatchers.IO).launch {
                NetworkClass.sendMessageToServ("#Leave")
            }
        } else if (ServerClass.networkRunning) {
            ServerClass.reset()
        }
        println("GameViewDef : destroy")
        this.gameDef = null
        val parentView = this.parent as ViewGroup
        this.visibility = GONE
        parentView.removeView(this)
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }
}
