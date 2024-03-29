package fr.x.grama
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.CountDownTimer


data class Word(val word: Array<String>, val good: Array<Boolean>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Word
        if (!word.contentEquals(other.word)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = word.contentHashCode()
        result = 31 * result + good.hashCode()
        return result
    }
}

class GameOrto : GameClass() {
    val rect: Array<Rect> = arrayOf(Rect(), Rect(), Rect())
    var wordInd: Int = 0
    var textArray: MutableList<Word> = mutableListOf()
    var endTime: Boolean = false
    var gradientStartColor:Array<Int> = arrayOf(Color.CYAN, Color.CYAN, Color.CYAN)
    var gradientEndColor:Array<Int> = arrayOf(Color.GREEN, Color.GREEN, Color.GREEN)
    private var y: Array<Float> = arrayOf(super.screenHeight / 2.6f , super.screenHeight / 2.6f + super.screenWidth / 2.6f, super.screenHeight / 2.6f + super.screenWidth / 1.3f)
    private val timer = object : CountDownTimer(5000, 100) {
        override fun onTick(millisUntilFinished: Long) {
            timeLeft = (millisUntilFinished / 1000f).toString().substring(0, 3).toFloat()
        }
        override fun onFinish() {
            endTime = true
        }
    }.start()


    fun update() {
        for (i in 0..2) {
            rect[i].set(screenWidth / 10, (y[i] - screenHeight / 11).toInt(), screenWidth - (screenWidth / 10), (y[i] + 80).toInt())
        }
        if (timeLeft <= 0f) {
            endTime = true
        }
        if (textArray[wordInd].word[0] == "" || textArray[wordInd].word[1] == "" || textArray[wordInd].word[2] == "") {
            correctAnswer()
        }
    }

    fun correctAnswer() {
        textArray.removeAt(wordInd)
        if (textArray.size == 0) {
            endGame = true
        }
        endTime = false
        gradientEndColor = arrayOf(Color.GREEN, Color.GREEN, Color.GREEN)
        gradientStartColor = arrayOf(Color.CYAN, Color.CYAN, Color.CYAN)
        timer.start()
    }

    fun getText(context: Context) : MutableList<Word> {
        var textArray = mutableListOf<Word>()
        val db = DatabaseManager(context).readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${DatabaseManager.TABLE_WORD}", null)
        if (cursor.moveToFirst()) {
            do {
                val word = arrayOf(cursor.getString(1), cursor.getString(2), cursor.getString(3))
                val good = arrayOf(true, false, false)
                val shuffledList = word.zip(good).shuffled()
                val wordShuffled = shuffledList.map { it.first }.toTypedArray()
                val goodShuffled = shuffledList.map { it.second }.toTypedArray()
                textArray.add(Word(wordShuffled, goodShuffled))
            } while (cursor.moveToNext())
        }
        if (textArray.size > 10) {
            textArray = textArray.subList(0, 10)
        }
        cursor.close()
        return textArray
    }
}

