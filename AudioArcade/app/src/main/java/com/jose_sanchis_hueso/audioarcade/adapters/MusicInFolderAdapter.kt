package com.jose_sanchis_hueso.audioarcade.adapters

import android.content.Intent
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jose_sanchis_hueso.audioarcade.MusicActivity
import com.jose_sanchis_hueso.audioarcade.R
import java.io.File

class MusicInFolderAdapter(private val musicList: List<String>, private val albumName: String) :
    RecyclerView.Adapter<MusicInFolderAdapter.MusicInFolderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicInFolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_musica, parent, false)
        return MusicInFolderViewHolder(view, musicList, albumName)
    }

    override fun onBindViewHolder(holder: MusicInFolderViewHolder, position: Int) {
        val musicName = musicList[position]
        holder.bind(musicName)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    class MusicInFolderViewHolder(
        itemView: View,
        private val musicInFolderList: List<String>,
        private val albumName: String
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val musicTextView: TextView = itemView.findViewById(R.id.textViewAlbum)
        private val playButton: ImageButton = itemView.findViewById(R.id.playButton)

        init {
            itemView.setOnClickListener(this)
            playButton.setOnClickListener(this)
        }

        fun bind(musicInFolderName: String) {
            musicTextView.text = musicInFolderName
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.playButton -> {
                    val musicDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath
                    val clickedMusicInFolder = musicInFolderList[adapterPosition]
                    val filePath = "$musicDirectoryPath/$albumName/$clickedMusicInFolder"

                    Log.d("MusicInFolderAdapter", "Clicked music: $clickedMusicInFolder")
                    Log.d("MusicInFolderAdapter", "Album name: $albumName")
                    Log.d("MusicInFolderAdapter", "Full file path: $filePath")

                    if (File(filePath).exists()) {
                        val intent = Intent(v.context, MusicActivity::class.java)
                        intent.putExtra("FILE_PATH", filePath)
                        v.context.startActivity(intent)
                    } else {
                        Log.e("MusicInFolderAdapter", "File not found: $filePath")
                    }
                }
                else -> {
                }
            }
        }

    }
}



