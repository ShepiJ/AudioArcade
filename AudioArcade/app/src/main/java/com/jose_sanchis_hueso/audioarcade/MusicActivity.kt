package com.jose_sanchis_hueso.audioarcade

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jose_sanchis_hueso.audioarcade.databinding.ActivityMusicBinding
import io.github.jeffshee.visualizer.painters.fft.*
import io.github.jeffshee.visualizer.painters.misc.SimpleIcon
import io.github.jeffshee.visualizer.painters.modifier.Beat
import io.github.jeffshee.visualizer.painters.modifier.Move
import io.github.jeffshee.visualizer.painters.modifier.Rotate
import io.github.jeffshee.visualizer.painters.modifier.Shake
import io.github.jeffshee.visualizer.painters.waveform.Waveform
import io.github.jeffshee.visualizer.utils.Preset
import io.github.jeffshee.visualizer.utils.VisualizerHelper
import io.github.jeffshee.visualizer.views.VisualizerView
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

    private lateinit var helper: VisualizerHelper
    private lateinit var background: Bitmap
    private lateinit var bitmap: Bitmap
    private lateinit var circleBitmap: Bitmap
    private var current = 0

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

                init()
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
    
    private fun init() {
        var visual = binding.visual

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        } else {
            background = BitmapFactory.decodeResource(resources, R.drawable.background)
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.logo)
            circleBitmap = SimpleIcon.getCircledBitmap(bitmap)

            helper = VisualizerHelper(mediaPlayer.audioSessionId)

            val painterLists = listOf(
                listOf(FftBar(), Move(FftWave(), yR = .5f)),
                listOf(FftLine(), Move(FftWaveRgb(), yR = .5f)),
                listOf(Rotate(SimpleIcon(circleBitmap).apply { radiusR = .5f }).apply { rpm = 2f }),
                listOf(Preset.getPresetWithBitmap("cIcon", circleBitmap)),
                listOf(Beat(Preset.getPresetWithBitmap("cIcon", circleBitmap))),
                listOf(
                    Waveform().apply { paint.alpha = 150 },
                    Shake(Preset.getPresetWithBitmap("cWaveRgbIcon", circleBitmap)).apply {
                        animX.duration = 1000
                        animY.duration = 2000
                    }),
                listOf(
                    Preset.getPresetWithBitmap("liveBg", background),
                    FftCircle().apply { paint.strokeWidth = 8f; paint.strokeCap = Paint.Cap.ROUND }
                )
            )

            visual.setPainterList(helper, painterLists[current])

            visual.setOnLongClickListener {
                if (current < painterLists.lastIndex) current++ else current = 0
                visual.setPainterList(helper, painterLists[current])
                true
            }

            Toast.makeText(this, "Try long-click \ud83d\ude09", Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()

        if (isPlaying) {
            stopMusic()
        }
        helper.release()
        super.onDestroy()
    }
}









