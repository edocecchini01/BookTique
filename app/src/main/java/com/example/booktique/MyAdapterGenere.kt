package com.example.booktique

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
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

        holder.titolo.text = abbreviaInfo(currentItem.title,25)

        if (currentItem.authors.isNotEmpty()) {
            holder.autore.text = abbreviaInfo(currentItem.authors.joinToString(", "),25)
        } else {
            holder.autore.text = "Autore sconosciuto"
        }

        holder.cover.setOnClickListener {
            BookHolder.book = currentItem
            val intent = Intent(holder.itemView.context, DettaglioLibro::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }

    fun abbreviaInfo(stringa: String, lunghezzaMassima: Int): String {
        return if (stringa.length <= lunghezzaMassima) {
            stringa
        } else {
            val sottostringa = stringa.take(lunghezzaMassima)
            "$sottostringa..."
        }
    }

    override fun getItemCount(): Int {
        return listaLibri.size
    }
}