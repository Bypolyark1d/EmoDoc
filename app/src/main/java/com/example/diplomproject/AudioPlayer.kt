package com.example.diplomproject

import android.content.Context
import android.media.MediaPlayer

class AudioPlayer(private val context: Context, private val resId: Int) {

    private var mediaPlayer: MediaPlayer? = null

    init {
        try {
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.isLooping = true
        } catch (e: Exception) {
        }
    }

    fun start() {
        try {
            if (mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
        }
    }

    fun pause() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
        }
    }

    fun reset() {
        try {
            mediaPlayer?.seekTo(0)
        } catch (e: Exception) {
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}