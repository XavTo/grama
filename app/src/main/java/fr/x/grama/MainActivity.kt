package fr.x.grama

import android.content.Intent
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
        val containerLoad = supportFragmentManager.beginTransaction()
        containerLoad.replace(R.id.game_box, HomeGameFragment())
        containerLoad.addToBackStack(null)
        containerLoad.commit()
        setupNavigation()
        setupSetting()
        setupProf()
    }

    private fun setupProf() {
        findViewById<ImageView>(R.id.id_prof).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("id", 0)
            startActivity(intent)
        }
    }

    private fun setupSetting() {
        findViewById<ImageView>(R.id.id_param).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("id", 1)
            startActivity(intent)
        }
    }

    private fun setupNavigation() {
        val navigation = findViewById<BottomNavigationView>(R.id.id_navigation)
        navigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.id_home_page -> {
                    loadFragment(HomeGameFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.id_stat_page -> {
                    loadFragment(StatFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.id_duel_page -> {
                    loadFragment(DuelFragment())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val containerLoad = supportFragmentManager.beginTransaction()
        containerLoad.replace(R.id.game_box, fragment)
        containerLoad.addToBackStack(null)
        containerLoad.commit()
    }

}