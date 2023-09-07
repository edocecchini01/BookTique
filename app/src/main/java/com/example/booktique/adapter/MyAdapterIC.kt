package com.example.booktique.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booktique.dataModel.LibriInC
import com.example.booktique.R

class MyAdapterIC(private val listaLibri : ArrayList<LibriInC>) :
    RecyclerView.Adapter<MyAdapterIC.MyViewHolder>() {

    private lateinit var bListener : onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun hideShow(element: LinearLayout, arrow : ImageButton)
        fun moveBook(send : ImageButton, position: Int)
        fun dettaglioBook(cover: ImageButton, position: Int)

        fun reading(seekBar: SeekBar, pagAtt : TextView, pagTot : TextView, element: LinearLayout, position: Int)
    }

    fun setOnCLickItemListener(listener : onItemClickListener){
        bListener = listener
    }

    class MyViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){

        val cover : ImageButton = itemView.findViewById(R.id.coverIC)
        val titolo : TextView = itemView.findViewById(R.id.titoloIC)
        val autore : TextView = itemView.findViewById(R.id.autoreIC)
        val pagTot : TextView = itemView.findViewById(R.id.pag_tot)

        val seek : SeekBar = itemView.findViewById(R.id.seekBar2)
        val pagAtt: TextView = itemView.findViewById(R.id.pag_att)

        init {

            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }

            itemView.findViewById<ImageButton>(R.id.downIC).setOnClickListener {
                listener.hideShow(itemView.findViewById(R.id.hideLayout), itemView.findViewById(R.id.downIC))
                listener.reading(seek,pagAtt,pagTot,itemView.findViewById(R.id.hideLayout),bindingAdapterPosition)
            }

            itemView.findViewById<ImageButton>(R.id.deleteIC).setOnClickListener {
                listener.moveBook(itemView.findViewById(R.id.deleteIC), bindingAdapterPosition)
            }

            itemView.findViewById<ImageButton>(R.id.coverIC).setOnClickListener{
                listener.dettaglioBook(itemView.findViewById(R.id.coverIC),bindingAdapterPosition)

            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_libri_in_corso,parent,false)
        return MyViewHolder(itemView,bListener)
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

        holder.autore.text = abbreviaInfo(currentItem?.autori ?: "",25)

        holder.pagAtt.text = currentItem?.paginaAtt.toString()

        if(currentItem?.pagineTot != 0) {
            holder.pagTot.text = currentItem?.pagineTot.toString()
        }else{
            holder.seek.visibility = View.GONE
            holder.pagTot.visibility = View.GONE
            holder.pagAtt.text = "Pagine totali non disponibili"
            holder.pagAtt.gravity = Gravity.CENTER
        }

        holder.seek.progress = currentItem?.paginaAtt!!

    }

    fun abbreviaInfo(stringa: String, lunghezzaMassima: Int): String {
        return if (stringa.length <= lunghezzaMassima) {
            stringa
        } else {
            val sottostringa = stringa.take(lunghezzaMassima)
            "$sottostringa..."
        }
    }
}