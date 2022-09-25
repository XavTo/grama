package fr.x.grama

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import fr.x.grama.fragments.ProfFragment
import fr.x.grama.fragments.SettingFragment

class SettingsActivity : AppCompatActivity() {
    private var username: String = ""
    private var password: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        val extra = intent.extras
        val id = extra!!.getInt("id")
        val tagFragment = extra.getString("tag")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (id == 1)
            loadFragment(SettingFragment())
        else
            loadFragment(ProfFragment())
        setupCross(tagFragment)
    }

    private fun setupConnexion() {
        val pseudoText = findViewById<TextView>(R.id.prof_pseudo)
        val passwordText = findViewById<TextView>(R.id.prof_password)
        findViewById<TextView>(R.id.prof_connexion_button).setOnClickListener {
            username = pseudoText.toString()
            password = passwordText.toString()
            // requete
        }
    }

    private fun setupCross(tagFragment: String?) {
        findViewById<ImageView>(R.id.id_cross).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("tag", tagFragment)
            startActivity(intent)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val containerLoad = supportFragmentManager.beginTransaction()
        containerLoad.replace(R.id.setting_box, fragment)
        containerLoad.addToBackStack(null)
        containerLoad.commit()
    }
}