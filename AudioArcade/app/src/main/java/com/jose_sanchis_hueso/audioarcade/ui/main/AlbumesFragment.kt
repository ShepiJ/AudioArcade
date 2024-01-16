package com.jose_sanchis_hueso.audioarcade.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.jose_sanchis_hueso.audioarcade.R
import com.jose_sanchis_hueso.audioarcade.adapters.AlbumAdapter
import java.io.File

class AlbumesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_albumes, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewAlbums)


        val musicFolderPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                .absolutePath

        val albumList = getAlbumsList(musicFolderPath)
        albumAdapter = AlbumAdapter(albumList)
        recyclerView.adapter = albumAdapter

        return view
    }

    private fun getAlbumsList(musicFolderPath: String): List<String> {
        val musicFolder = File(musicFolderPath)
        val albumList = mutableListOf<String>()


        if (musicFolder.exists() && musicFolder.isDirectory) {

            musicFolder.listFiles { file -> file.isDirectory }?.forEach {
                val albumName = it.name
                if (albumName != ".thumbnails") {
                    albumList.add(albumName)
                    Log.d("AlbumList", "Added album: $albumName")
                }
            }
        } else {
            Log.e(
                "AlbumList",
                "Music folder does not exist or is not a directory: $musicFolderPath"
            )
        }

        return albumList
    }


    companion object {
        fun newInstance(): AlbumesFragment {
            return AlbumesFragment()
        }
    }
}




