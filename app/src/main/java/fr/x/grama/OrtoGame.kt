package fr.x.grama
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
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

class GameOrto {
    companion object {
        private lateinit var resources: Resources
        fun init(context: Context) {
            resources = context.resources
        }
        var score: Int = 0
    }
    var endGame: Boolean = false
    val allTextSize = 140f
    val screenWidth = resources.displayMetrics.widthPixels
    private val screenHeight = resources.displayMetrics.heightPixels
    val rect: Array<Rect> = arrayOf(Rect(), Rect(), Rect())
    val rectPaint = Paint().apply {
        color = Color.RED
        strokeWidth = 5f
        style = Paint.Style.FILL
        setShadowLayer(5f, 5f, 5f, Color.GRAY)
    }
    val paint = Paint().apply {
        color = Color.rgb(0, 150, 255)
        setShadowLayer(5f, 5f, 5f, Color.BLACK)
        textSize = allTextSize
    }
    val paintScore = Paint().apply {
        color = Color.GREEN
        textSize = allTextSize
        setShadowLayer(5f, 5f, 5f, Color.BLACK)
    }
    var wordInd: Int = 0
    var textArray: MutableList<Word> = mutableListOf()
    private var y: Array<Float> = arrayOf(screenHeight / 2f, screenHeight / 2f + 200, screenHeight / 2f + 400f)
    var timeLeft: Float = 0f
    private val timer = object : CountDownTimer(5000, 100) {
        override fun onTick(millisUntilFinished: Long) {
            timeLeft = (millisUntilFinished / 1000f).toString().substring(0, 3).toFloat()
        }
        override fun onFinish() {
            endGame = true
        }
    }.start()


    fun update() {
        for (i in 0..2) {
            rect[i].set(screenWidth / 10, (y[i] - 140).toInt(), screenWidth - (screenWidth / 10), (y[i] + 20).toInt())
        }
        if (textArray[wordInd].word[0] == "" || textArray[wordInd].word[1] == "" || textArray[wordInd].word[2] == "") {
            textArray.removeAt(wordInd)
            if (textArray.size == 0) {
                endGame = true
            }
            timer.start()
        }
    }

    fun getText(context: Context) : MutableList<Word> {
        val textArray = mutableListOf<Word>()
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
        cursor.close()
        return textArray
    }

    fun getScore(): Int {
        return score
    }

    fun setScore(newScore: Int) {
        score = newScore
    }
}

