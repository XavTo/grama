package fr.x.grama

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import fr.x.grama.fragments.ProfFragment
import fr.x.grama.fragments.SettingFragment

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val extra = intent.extras
        val id = extra!!.getInt("id")
        val homeFrag = extra!!.getInt("fragment")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (id == 1)
            loadFragment(SettingFragment())
        else
            loadFragment(ProfFragment())
        setupCross(homeFrag)
    }

    private fun setupCross(homeFrag: Int) {
        findViewById<ImageView>(R.id.id_cross).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragment", homeFrag);
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