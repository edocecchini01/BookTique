package com.example.booktique

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapterGenere(private val listaLibri: ArrayList<VolumeDet>) :
    RecyclerView.Adapter<MyAdapterGenere.MyViewHolder>() {

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val cover : ImageButton = itemView.findViewById(R.id.coverSG)
        val titolo : TextView = itemView.findViewById(R.id.titoloSG)
        val autore : TextView = itemView.findViewById(R.id.autoreSG)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_libri_scopri_genere,parent,false)
        Log.d("TAG","LIBRI:12" )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listaLibri[position]
        Log.d("TAGF", "LIBRI: $currentItem")

        Glide.with(holder.itemView.context)
            .load(currentItem.imageLinks.smallThumbnail)
            .into(holder.cover)

        holder.titolo.text = currentItem.title

        if (currentItem.authors.isNotEmpty()) {
            holder.autore.text = currentItem.authors.joinToString(", ")
        } else {
            holder.autore.text = "Autore sconosciuto"
        }
    }

    override fun getItemCount(): Int {
        return listaLibri.size
    }
}