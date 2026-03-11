package com.smart.health.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.smart.health.R

object SoundPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private const val TAG = "SoundPlayer"
    
    /**
     * Play the completion sound for 5 seconds
     */
    fun playCompletionSound(context: Context) {
        try {
            // Release any existing player
            release()
            
            // Try to create MediaPlayer with custom sound
            mediaPlayer = try {
                MediaPlayer.create(context, R.raw.completion_sound)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load custom sound, using default", e)
                null
            }
            
            // Fallback to default notification sound if custom sound failed
            if (mediaPlayer == null) {
                try {
                    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(context, defaultSoundUri)
                        prepare()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load default sound", e)
                    return
                }
            }
            
            // Start playback
            mediaPlayer?.let { player ->
                try {
                    player.setOnCompletionListener {
                        release()
                    }
                    
                    player.setOnErrorListener { _, what, extra ->
                        Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                        release()
                        true
                    }
                    
                    player.start()
                    
                    // Stop after 5 seconds
                    handler.postDelayed({
                        try {
                            if (player.isPlaying) {
                                player.stop()
                            }
                            release()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error stopping player", e)
                        }
                    }, 5000)
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error starting playback", e)
                    release()
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in playCompletionSound", e)
            release()
        }
    }
    
    /**
     * Release the media player if still active
     */
    fun release() {
        try {
            handler.removeCallbacksAndMessages(null)
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                reset()
                release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing player", e)
            mediaPlayer = null
        }
    }
}
