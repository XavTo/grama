package fr.x.grama

import android.content.Context
import android.graphics.Rect
import android.os.CountDownTimer

class GameDef : GameClass() {
    var listWord: MutableList<String> = mutableListOf()
    var listDefinition: MutableList<String> = mutableListOf()
    var word = ""
    var definition = ""
    var endTime: Boolean = false
    private var y: Float = super.screenHeight / 2f
    var rect: Rect = Rect(50, 50, 200, 150)
    private val timer = object : CountDownTimer(15000, 100) {
        override fun onTick(millisUntilFinished: Long) {
            timeLeft = (millisUntilFinished / 1000f).toString().substring(0, 3).toFloat()
        }
        override fun onFinish() {
            endTime = true
        }
    }.start()

    fun update() {
        rect.set(screenWidth / 10, (y - 140).toInt(), screenWidth - (screenWidth / 10), (y + 20).toInt())
        if (timeLeft <= 0f) {
            endTime = true
        }
    }

    fun correctAnswer() {
        listWord.removeAt(0)
        listDefinition.removeAt(0)
        if (listWord.size == 0) {
            endGame = true
        }
        word = listWord[0]
        definition = listDefinition[0]
        timer.start()
        endTime = false
    }

    fun setText(context: Context) {
        val db = DatabaseManager(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseManager.TABLE_DEF}", null)
        val tempWord: MutableList<String> = mutableListOf()
        val tempDef: MutableList<String> = mutableListOf()
        if (cursor.moveToFirst()) {
            do {
                tempDef.add(cursor.getString(1))
                tempWord.add(cursor.getString(2))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        val shuffledList = tempWord.zip(tempDef).shuffled()
        listWord = shuffledList.map { it.first }.toMutableList()
        listDefinition = shuffledList.map { it.second }.toMutableList()
        word = listWord[0]
        definition = listDefinition[0]
    }
}