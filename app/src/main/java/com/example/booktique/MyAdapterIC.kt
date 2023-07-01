package com.example.booktique

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapterIC(private val listaLibri : ArrayList<Libro>) :
    RecyclerView.Adapter<MyAdapterIC.MyViewHolder>() {

    private lateinit var bListener : onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun hideShow(element: LinearLayout, arrow : ImageButton)
    }

    fun setOnCLickItemListener(listener : onItemClickListener){
        bListener = listener
    }

    class MyViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){

        val cover : ImageButton = itemView.findViewById(R.id.coverIC)
        val titolo : TextView = itemView.findViewById(R.id.titoloIC)
        val genere : TextView = itemView.findViewById(R.id.genereIC)

        init {

            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }

            itemView.findViewById<ImageButton>(R.id.downIC).setOnClickListener {
                listener.hideShow(itemView.findViewById(R.id.hideLayout), itemView.findViewById(R.id.downIC))
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_libri_in_corso,parent,false)
        return MyViewHolder(itemView,bListener)
    }

    override fun getItemCount(): Int {
        return listaLibri.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listaLibri[position]
        holder.cover.setImageResource(currentItem.cover)
        holder.titolo.text = currentItem.titolo
        holder.genere.text = currentItem.genere
    }
}