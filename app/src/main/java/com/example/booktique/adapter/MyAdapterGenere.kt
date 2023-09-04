package com.example.booktique.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booktique.R
import com.example.booktique.dataModel.VolumeDet

class MyAdapterGenere(private val listaLibri: ArrayList<VolumeDet>) :
    RecyclerView.Adapter<MyAdapterGenere.MyViewHolder>() {

    private lateinit var bListener : onItemClickListener

    interface onItemClickListener{
        fun dettaglioBook(cover: ImageButton, position: Int)
    }

    fun setOnCLickItemListener(listener: onItemClickListener){
        bListener = listener
    }

    class MyViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val cover: ImageButton = itemView.findViewById(R.id.coverSG)
        val titolo: TextView = itemView.findViewById(R.id.titoloSG)
        val autore: TextView = itemView.findViewById(R.id.autoreSG)


        init {
            itemView.findViewById<ImageButton>(R.id.coverSG).setOnClickListener {
                listener.dettaglioBook(itemView.findViewById(R.id.coverSG), bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_libri_scopri_genere,parent,false)
        val flag = ::bListener.isInitialized
        Log.d("TAG", "LIBRI:12")
        return MyViewHolder(itemView, bListener)
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listaLibri[position]
        Log.d("TAGF", "LIBRI: $currentItem")

        Glide.with(holder.itemView.context)
            .load(currentItem.imageLinks.thumbnail)
            .into(holder.cover)

        holder.titolo.text = abbreviaInfo(currentItem?.title ?: "",35)

        if (currentItem.authors.isNotEmpty()) {
            holder.autore.text = abbreviaInfo(currentItem.authors.joinToString(", "),35)
        } else {
            holder.autore.text = "Autore sconosciuto"
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