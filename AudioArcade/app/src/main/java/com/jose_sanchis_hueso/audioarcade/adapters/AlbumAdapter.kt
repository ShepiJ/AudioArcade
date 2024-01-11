package com.jose_sanchis_hueso.audioarcade.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jose_sanchis_hueso.audioarcade.R

class AlbumAdapter(private val albumList: List<String>) :
    RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val albumName = albumList[position]
        holder.bind(albumName)
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val albumTextView: TextView = itemView.findViewById(R.id.textViewAlbum)

        fun bind(albumName: String) {
            albumTextView.text = albumName
        }
    }
}
