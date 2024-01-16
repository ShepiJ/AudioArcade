package com.jose_sanchis_hueso.audioarcade

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.jose_sanchis_hueso.audioarcade.adapters.MusicInFolderAdapter
import java.io.File

class MusicInFolderActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var musicInFolderAdapter: MusicInFolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_in_folder)

        recyclerView = findViewById(R.id.recyclerViewMusicInFolder)

        val albumName = intent.getStringExtra("ALBUM_NAME")


        if (albumName != null) {
            val albumFolderPath = getAlbumFolderPath(albumName)

            val musicInFolderList = getmusicInFolderList(albumFolderPath)

            musicInFolderAdapter = MusicInFolderAdapter(musicInFolderList,albumName)
            recyclerView.adapter = musicInFolderAdapter
        } else {
            Log.e("MusicInFolderActivity", "Album name is null")
        }
    }

    private fun getAlbumFolderPath(albumName: String): String {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            .absolutePath + File.separator + albumName
    }

    private fun getmusicInFolderList(musicFolderPath: String): List<String> {
        val musicFolder = File(musicFolderPath)
        val musicInFolder = mutableListOf<String>()

        if (musicFolder.exists() && musicFolder.isDirectory) {
            musicFolder.listFiles()?.forEach { file ->
                if (file.isFile && file.extension.equals("mp3", ignoreCase = true)) {
                    val musicFileName = file.name
                    musicInFolder.add(musicFileName)
                    Log.d("MusicList", "Added music file: $musicFileName")
                }
            }
        } else {
            Log.e(
                "musicInFolderList",
                "Music folder does not exist or is not a directory: $musicFolderPath"
            )
        }

        return musicInFolder
    }
}

