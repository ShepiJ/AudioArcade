package com.jose_sanchis_hueso.audioarcade

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.jose_sanchis_hueso.audioarcade.databinding.ActivityMusicBinding
import kotlinx.coroutines.*
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class MusicActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityMusicBinding
    private lateinit var exoPlayer: SimpleExoPlayer
    private var isPlaying: Boolean = false
    private var duration: Long = 0
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filePath = intent.getStringExtra("FILE_PATH")

        launch(Dispatchers.IO) {
            exoPlayer = filePath?.let { createAndPrepareExoPlayer(it) }!!

            withContext(Dispatchers.Main) {
                duration = exoPlayer.duration
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
            }
        }
    }
    private suspend fun createAndPrepareExoPlayer(filePath: String): SimpleExoPlayer =
        withContext(Dispatchers.Main) {
            val player = SimpleExoPlayer.Builder(this@MusicActivity).build()
            val dataSourceFactory = DefaultDataSourceFactory(this@MusicActivity, Util.getUserAgent(this@MusicActivity, "YourAppName"))
            val mediaSource = buildMediaSource(filePath, dataSourceFactory)

            player.setMediaSource(mediaSource)
            player.prepare()
            player
        }


    private fun buildMediaSource(filePath: String, dataSourceFactory: DefaultDataSourceFactory): MediaSource {
        val uri = android.net.Uri.parse(filePath)
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri)
    }

    private fun setupSeekBar() {
        binding.progressSeekBar.max = duration.toInt()
        binding.totalDurationTextView.text = formatTime(duration.toInt())
    }

    private fun updateSeekBar() {
        launch(Dispatchers.Default) {
            while (isActive) {
                delay(1000)
                withContext(Dispatchers.Main) {
                    binding.progressSeekBar.progress = exoPlayer.currentPosition.toInt()
                    binding.elapsedTimeTextView.text = formatTime(exoPlayer.currentPosition.toInt())
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
        exoPlayer.playWhenReady = true
        isPlaying = true
        binding.playPauseButton.setImageResource(R.drawable.pause)
    }

    private fun pauseMusic() {
        exoPlayer.playWhenReady = false
        isPlaying = false
        binding.playPauseButton.setImageResource(R.drawable.play)
    }

    private fun stopMusic() {
        exoPlayer.stop()
        exoPlayer.release()
        isPlaying = false
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()

        if (isPlaying) {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }
}




