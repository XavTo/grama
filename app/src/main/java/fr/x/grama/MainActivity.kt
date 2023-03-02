package fr.x.grama

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.x.grama.fragments.DuelFragment
import fr.x.grama.fragments.HomeGameFragment
import fr.x.grama.fragments.StatFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tag = intent.extras?.getString("tag")
        setupFragment(tag.toString())
        setupNavigation()
        setupSetting()
        setupProf()
        val db = DatabaseManager(this).writableDatabase
        db.execSQL("CREATE TABLE IF NOT EXISTS TABLE_USER (id INTEGER PRIMARY KEY AUTOINCREMENT, EMAIL TEXT, USERNAME TEXT, PASSWORD TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS TABLE_WORD (id INTEGER PRIMARY KEY AUTOINCREMENT, VWORD TEXT, BADWORD TEXT, BADWORD2 TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS TABLE_DEF (id INTEGER PRIMARY KEY AUTOINCREMENT, DEFINITION TEXT, WORD TEXT)")
        fillDbIfEmpty(db)
        db.close()
        if (UserInfo.sp == null) {
            UserInfo.setSp(this)
        }
        UserInfo.email = UserInfo.sp?.getString("Unm", "").toString()
        UserInfo.pseudo = UserInfo.sp?.getString("Psw", "").toString()
        UserInfo.wins = UserInfo.sp?.getInt("Wins", 0)!!
        UserInfo.losses = UserInfo.sp?.getInt("Losses", 0)!!
    }

    private fun fillDbIfEmpty(db: SQLiteDatabase) {
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM TABLE_WORD", null)
            if (cursor.count == 0) {
                db.execSQL("INSERT INTO TABLE_WORD (VWORD, BADWORD, BADWORD2) VALUES ('reblochon', 'roblochon', 'roblechon')")
                db.execSQL("INSERT INTO TABLE_WORD (VWORD, BADWORD, BADWORD2) VALUES ('comment', 'commant', 'coment')")
                db.execSQL("INSERT INTO TABLE_WORD (VWORD, BADWORD, BADWORD2) VALUES ('pomme', 'pome', 'pom')")
            }
        } finally {
            cursor?.close()
        }
        try {
            cursor = db.rawQuery("SELECT * FROM TABLE_DEF", null)
            if (cursor.count == 0) {
                db.execSQL("INSERT INTO TABLE_DEF (DEFINITION, WORD) VALUES ('Fromage au lait de vache, à pâte grasse et de saveur douce, fabriqué en Savoie.', 'reblochon')")
                db.execSQL("INSERT INTO TABLE_DEF (DEFINITION, WORD) VALUES ('Sauce froide composée d''huile, d''œufs et d''assaisonnements.', 'mayonnaise')")
                db.execSQL("INSERT INTO TABLE_DEF (DEFINITION, WORD) VALUES ('moyen de transport urbain individuel, composé d''une plaque métallique montée sur deux roues.', 'trottinette')")
            }
        } finally {
            cursor?.close()
        }
    }

    private fun setupFragment(tag : String) {
        val containerLoad = supportFragmentManager.beginTransaction()
        when (tag) {
            "Stat_Fragment" -> {
                containerLoad.replace(R.id.game_box, StatFragment(), "Stat_Fragment")
                findViewById<BottomNavigationView>(R.id.id_navigation).selectedItemId = R.id.id_stat_page
            }
            "Duel_Fragment" -> {
                containerLoad.replace(R.id.game_box, DuelFragment(), "Duel_Fragment")
                findViewById<BottomNavigationView>(R.id.id_navigation).selectedItemId = R.id.id_duel_page
            }
            else -> {
                containerLoad.replace(R.id.game_box, HomeGameFragment(), "Home_Fragment")
                findViewById<BottomNavigationView>(R.id.id_navigation).selectedItemId = R.id.id_home_page
            }
        }
        containerLoad.addToBackStack(null)
        containerLoad.commit()
    }

    private fun setupProf() {
        findViewById<ImageView>(R.id.id_prof).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("id", 0)
            intent.putExtra("tag", findTagFragment())
            startActivity(intent)
        }
    }

    private fun setupSetting() {
        findViewById<ImageView>(R.id.id_param).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("id", 1)
            intent.putExtra("tag", findTagFragment())
            startActivity(intent)
        }
    }

    private fun findTagFragment(): String {
        var fragment: Fragment? = supportFragmentManager.findFragmentByTag("Home_Fragment")
        if (fragment != null && fragment.isVisible)
            return "Home_Fragment"
        fragment = supportFragmentManager.findFragmentByTag("Stat_Fragment")
        if (fragment != null && fragment.isVisible)
            return "Stat_Fragment"
        fragment = supportFragmentManager.findFragmentByTag("Duel_Fragment")
        if (fragment != null && fragment.isVisible)
            return "Duel_Fragment"
        return "null"
    }

    private fun setupNavigation() {
        val navigation = findViewById<BottomNavigationView>(R.id.id_navigation)
        navigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.id_home_page -> {
                    loadFragment(HomeGameFragment(), "Home_Fragment")
                    return@setOnItemSelectedListener true
                }
                R.id.id_stat_page -> {
                    loadFragment(StatFragment(), "Stat_Fragment")
                    return@setOnItemSelectedListener true
                }
                R.id.id_duel_page -> {
                    loadFragment(DuelFragment(), "Duel_Fragment")
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        val containerLoad = supportFragmentManager.beginTransaction()
        containerLoad.replace(R.id.game_box, fragment, tag)
        containerLoad.addToBackStack(null)
        containerLoad.commit()
    }
}