package fr.x.grama

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import fr.x.grama.fragments.ConnectedFragment
import fr.x.grama.fragments.ProfFragment
import fr.x.grama.fragments.SettingFragment

class SettingsActivity : AppCompatActivity() {
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            intent.putExtra("tag", findTagFragment())
            startActivity(intent)
            finish()
        }
    }
    private lateinit var gramaClass: GramaClass
    override fun onCreate(savedInstanceState: Bundle?) {
        gramaClass = applicationContext as GramaClass
        val extra = intent.extras
        val id = extra!!.getInt("id")
        val tagFragment = extra.getString("tag")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (id == 1)
            loadFragment(SettingFragment())
        else {
            if (UserInfo.pseudo == "" && UserInfo.email == "")
                loadFragment(ProfFragment())
            else
                loadFragment(ConnectedFragment())
        }
        setupCross(tagFragment)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun findTagFragment(): String {
        val fragment = supportFragmentManager.findFragmentById(R.id.setting_box)
        return fragment?.tag.toString()
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

    override fun onResume() {
        gramaClass.startMusic()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        gramaClass.stopMusic()
    }
}