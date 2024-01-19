package com.jose_sanchis_hueso.audioarcade

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.jose_sanchis_hueso.audioarcade.databinding.ActivityMusicBinding
import kotlinx.coroutines.*
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class MusicActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityMusicBinding
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying: Boolean = false
    private var duration: Int = 0
    private var job: Job = Job()
    private var playMode: PlayMode = PlayMode.NORMAL

    enum class PlayMode {
        NORMAL,
        RANDOM
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filePath = intent.getStringExtra("FILE_PATH")

        launch(Dispatchers.IO) {
            mediaPlayer = filePath?.let { createAndPrepareMediaPlayer(it) }!!

            withContext(Dispatchers.Main) {
                duration = mediaPlayer.duration
                playMusic()
                setupSeekBar()
                updateSeekBar()

                binding.playPauseButton.setOnClickListener {
                    if (isPlaying) {
                        pauseMusic()
                    } else {
                        playMusic()
                    }
                }

                binding.progressSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            mediaPlayer.seekTo(progress)
                            binding.elapsedTimeTextView.text = formatTime(progress)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        // Do nothing
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        // Do nothing
                    }
                })

                binding.backButton.setOnClickListener {
                    playPrevious()
                }

                binding.nextButton.setOnClickListener {
                    playNext()
                }

                binding.playMode.setOnClickListener {
                    togglePlayMode()
                }
            }
        }
    }

    private suspend fun createAndPrepareMediaPlayer(filePath: String): MediaPlayer =
        withContext(Dispatchers.Main) {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(filePath)
            mediaPlayer.prepare()
            mediaPlayer
        }

    private fun setupSeekBar() {
        binding.progressSeekBar.max = duration
        binding.totalDurationTextView.text = formatTime(duration)
    }

    private fun updateSeekBar() {
        launch(Dispatchers.Default) {
            while (isActive) {
                delay(1000)
                withContext(Dispatchers.Main) {
                    binding.progressSeekBar.progress = mediaPlayer.currentPosition
                    binding.elapsedTimeTextView.text = formatTime(mediaPlayer.currentPosition)
                }
            }
        }
    }

    private fun formatTime(timeMillis: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis.toLong()) % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun playMusic() {
        mediaPlayer.start()
        isPlaying = true
        binding.playPauseButton.setImageResource(R.drawable.pause)
    }

    private fun pauseMusic() {
        mediaPlayer.pause()
        isPlaying = false
        binding.playPauseButton.setImageResource(R.drawable.play)
    }

    private fun stopMusic() {
        mediaPlayer.stop()
        mediaPlayer.release()
        isPlaying = false
        finish()
    }

    private fun playNext() {

        if (playMode == PlayMode.RANDOM) {

        } else {

        }
    }

    private fun playPrevious() {

        if (playMode == PlayMode.RANDOM) {

        } else {

        }
    }

    private fun togglePlayMode() {

        playMode = if (playMode == PlayMode.NORMAL) PlayMode.RANDOM else PlayMode.NORMAL

    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()

        if (isPlaying) {
            stopMusic()
        }
    }
}









