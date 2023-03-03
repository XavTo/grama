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
import android.widget.Button
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
    private var deleteButton: Button? = null
    var path: Path = Path()
    fun setGameDef(tempGameDef: GameDef, tempEditText: EditText, tempDeleteButton: Button) {
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
        setDeleteButton(tempDeleteButton)
    }

    private fun setDeleteButton(tempDeleteButton: Button) {
        deleteButton = tempDeleteButton
        deleteButton?.setOnClickListener {
            if (textInBox.isNotEmpty()) {
                textInBox = ""
                editText?.setText(textInBox)
            }
        }
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
        if (gameDef?.endGame == true) {
            if (NetworkClass.isClientConnnected) {
                val ed = UserInfo.sp?.edit()
                val scoreEn = NetworkClass.getScore()
                var winner = ""
                var maxScore = 0
                for (score in scoreEn) {
                    if (score.second > maxScore) {
                        winner = score.first
                        maxScore = score.second
                    }
                }
                Toast.makeText(context, "Winner is $winner", Toast.LENGTH_LONG).show()
                if (winner == UserInfo.pseudo) {
                    UserInfo.wins++
                    ed?.putInt("Wins", UserInfo.wins)
                } else {
                    UserInfo.losses++
                    ed?.putInt("Loses", UserInfo.losses)
                }
                ed?.apply()
                destroy(winner, maxScore)
            } else {
                Toast.makeText(context, "Winner is ${UserInfo.pseudo}", Toast.LENGTH_LONG).show()
                destroy(UserInfo.pseudo, gameDef!!.getScore())
            }
        }
        if (gameDef?.endTime == true) {
            gameDef?.correctAnswer()
            return
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
        canvas.drawRect(-10f, -10f, textWidth.toFloat() + 10,
            staticLayout.height.toFloat() + 10, gameDef?.rectPaint3!!)
        staticLayout.draw(canvas)
        canvas.restore()
    }

    fun destroy(winner: String = "", score : Int = 0) {
        (parent as ViewGroup).removeView(this)
        val intent = Intent(context, GameActivity::class.java)
        intent.putExtra("score", score)
        intent.putExtra("winner", winner)
        intent.putExtra("gameType", 3)
        intent.putExtra("currentGame", 1)
        this.gameDef = null
        context.startActivity(intent)
        (context as Activity).finish()
    }
}
