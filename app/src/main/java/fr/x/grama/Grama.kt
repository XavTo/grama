package fr.x.grama

import android.app.Application
import android.media.MediaPlayer

class GramaClass : Application() {
    var mediaPlayer: MediaPlayer? = null
    var isPlaying: Boolean = false
    var cutMusic: Boolean = true

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayer?.isLooping = true
    }

    fun waitForUserInput() {
        if (UserInfo.sp?.getBoolean("sound", true) == true) {
            mediaPlayer?.start()
            isPlaying = true
            cutMusic = false
        }
    }

    override fun onTerminate() {
        mediaPlayer?.release()
        mediaPlayer = null

        super.onTerminate()
    }

    fun stopMusic() {
        if (isPlaying && !cutMusic) {
            mediaPlayer?.pause()
            isPlaying = false
        }
    }

    fun startMusic() {
        if (!isPlaying && !cutMusic) {
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }
}