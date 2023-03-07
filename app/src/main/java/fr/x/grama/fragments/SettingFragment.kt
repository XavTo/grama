package fr.x.grama.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.x.grama.GramaClass
import fr.x.grama.R
import fr.x.grama.UserInfo

class SettingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val current = inflater.inflate(R.layout.fragment_setting, container, false)
        val seekBar = current.findViewById<android.widget.SeekBar>(R.id.sound_seekbar)
        val gramaClass = activity?.applicationContext as GramaClass
        current.findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.sound_switch).isChecked = UserInfo.sp?.getBoolean("sound", true) ?: true
        seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                gramaClass.setVolume(progress.toFloat() / 100)
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
            }
        })
        current.findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.sound_switch).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                gramaClass.cutMusic = false
                gramaClass.startMusic()
                UserInfo.sp?.edit()?.putBoolean("sound", true)?.apply()
            }
            else {
                gramaClass.stopMusic()
                gramaClass.cutMusic = true
                UserInfo.sp?.edit()?.putBoolean("sound", false)?.apply()
            }
        }
        return current
    }
}