package fr.x.grama

import android.app.Application
import android.media.MediaPlayer

class GramaClass : Application() {
    var mediaPlayer: MediaPlayer? = null
    var isPlaying = true

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    override fun onTerminate() {
        mediaPlayer?.release()
        mediaPlayer = null

        super.onTerminate()
    }

    fun stopMusic() {
        mediaPlayer?.pause()
        isPlaying = false
    }

    fun startMusic() {
        mediaPlayer?.start()
        isPlaying = true
    }

    fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }
}