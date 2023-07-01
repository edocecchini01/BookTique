package com.example.booktique

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapterDL(private val listaLibri : ArrayList<LibriDaL>) :
    RecyclerView.Adapter<MyAdapterDL.MyViewHolder>() {

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val cover : ImageButton = itemView.findViewById(R.id.coverDL)
        val titolo : TextView = itemView.findViewById(R.id.titoloDL)
        val genere : TextView = itemView.findViewById(R.id.genereDL)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_libri_da_leggere,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listaLibri.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listaLibri[position]

        Glide.with(holder.itemView.context)
            .load(currentItem.copertina)
            .into(holder.cover)

        holder.titolo.text = abbreviaInfo(currentItem?.titolo ?: "",25)

        holder.genere.text = "Genere"
    }

    //fattorizzare
    fun abbreviaInfo(stringa: String, lunghezzaMassima: Int): String {
        return if (stringa.length <= lunghezzaMassima) {
            stringa
        } else {
            val sottostringa = stringa.take(lunghezzaMassima)
            "$sottostringa..."
        }
    }


}