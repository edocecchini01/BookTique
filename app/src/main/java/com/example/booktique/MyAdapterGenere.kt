package com.example.booktique

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapterGenere (private val listaLibri : ArrayList<Libro>) :
    RecyclerView.Adapter<MyAdapterGenere.MyViewHolder>() {

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val cover : ImageButton = itemView.findViewById(R.id.coverSG)
        val titolo : TextView = itemView.findViewById(R.id.titoloSG)
        val autore : TextView = itemView.findViewById(R.id.autoreSG)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_libri_scopri_genere,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listaLibri[position]
        holder.cover.setImageResource(currentItem.cover)
        holder.titolo.text = currentItem.titolo
        holder.autore.text = currentItem.autore
    }

    override fun getItemCount(): Int {
        return listaLibri.size
    }
}