package com.jose_sanchis_hueso.audioarcade.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jose_sanchis_hueso.audioarcade.MusicInFolderActivity
import com.jose_sanchis_hueso.audioarcade.R

class AlbumAdapter(private val albumList: List<String>) :
    RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view, albumList)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val albumName = albumList[position]
        holder.bind(albumName)
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    class AlbumViewHolder(itemView: View, private val albumList: List<String>) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val albumTextView: TextView = itemView.findViewById(R.id.textViewAlbum)

        init {

            itemView.setOnClickListener(this)
        }

        fun bind(albumName: String) {
            albumTextView.text = albumName
        }


        override fun onClick(v: View) {

            val clickedAlbum = albumList[adapterPosition]


            val intent = Intent(v.context, MusicInFolderActivity::class.java)
            intent.putExtra("ALBUM_NAME", clickedAlbum)
            Log.e("ALBUM_NAME", clickedAlbum)
            v.context.startActivity(intent)
        }
    }
}

