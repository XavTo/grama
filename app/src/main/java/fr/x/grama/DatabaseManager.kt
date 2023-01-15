package fr.x.grama

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "database.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_USER = "user_table"
        const val TABLE_WORD = "TABLE_WORD"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_USER (ID INTEGER PRIMARY KEY AUTOINCREMENT, EMAIL TEXT, USERNAME TEXT, PASSWORD TEXT);"
        val createTableQuery2 = "CREATE TABLE $TABLE_WORD (ID INTEGER PRIMARY KEY AUTOINCREMENT, VWORD TEXT, BADWORD TEXT, BADWORD2 TEXT);"
        db.execSQL(createTableQuery)
        db.execSQL(createTableQuery2)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORD")
        onCreate(db)
    }
}