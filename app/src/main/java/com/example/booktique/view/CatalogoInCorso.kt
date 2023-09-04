package com.example.booktique.view

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booktique.dataModel.LibriInC
import com.example.booktique.adapter.MyAdapterIC
import com.example.booktique.R
import com.example.booktique.databinding.FragmentCatalogoInCorsoBinding
import com.example.booktique.viewModel.CatalogoViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CatalogoInCorso : Fragment() {
    private lateinit var binding: FragmentCatalogoInCorsoBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: MyAdapterIC
    private lateinit var listaLibri: ArrayList<LibriInC>
    private lateinit var select: Spinner
    private lateinit var viewModel: CatalogoViewModel
    private val sezioni = arrayListOf("Letti","Da Leggere")

    private lateinit var activity : FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
            binding = DataBindingUtil.inflate<FragmentCatalogoInCorsoBinding>(
                inflater,
                R.layout.fragment_catalogo_in_corso, container, false
            )

        binding.backbuttonIc.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.action_catalogoInCorso_to_catalogoHome)
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CatalogoViewModel::class.java)

        listaLibri = ArrayList()

        recyclerView = binding.listaLibriCorso
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        adapter = MyAdapterIC(listaLibri)
        recyclerView.adapter = adapter
        activity = requireActivity()

        adapter.setOnCLickItemListener(object : MyAdapterIC.onItemClickListener {
            override fun onItemClick(position: Int) {

            }

            override fun moveBook(send: ImageButton, position: Int) {
                val btn = send

                btn.setOnClickListener {
                    val bookPos = position
                    val bookId = getIdPos(bookPos)
                    var dialog: AlertDialog? = null
                    val builder = AlertDialog.Builder(requireContext())
                    val dialogView = layoutInflater.inflate(R.layout.move_spinner, null)
                    select = dialogView.findViewById<Spinner>(R.id.spinner)
                    val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)
                    val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
                    builder.setView(dialogView)

                    val arrayAdapter = ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item
                    )
                    select.adapter = arrayAdapter
                    arrayAdapter.addAll(sezioni)

                    btnConfirm.setOnClickListener {
                        // Verifica se è stato selezionato un elemento
                        if (select.selectedItem != null) {
                            val selectedItem = select.selectedItem.toString()
                            var where = false

                            if(selectedItem == "Letti")
                                where = false
                            if(selectedItem == "Da Leggere")
                                where = true

                            if (bookId != null) {
                                Log.d("TAG", "idLibro: $bookId")
                                viewModel.moveBooks(bookId, where, "in corso")
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Seleziona un elemento!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        // Chiudi il dialog
                        dialog?.dismiss()
                    }

                    btnCancel.setOnClickListener {
                        dialog?.dismiss()
                    }

                    dialog = builder.create()
                    dialog?.show()
                }
            }

            override fun reading(seekBar: SeekBar, pagAtt: TextView, pagTot : TextView, element: LinearLayout, position: Int) {
                val seek = seekBar
                val pagA = pagAtt
                val pagT = pagTot
                seek.max = pagT.text.toString().toInt()
                val linearL = element
                val bookPos = position
                val bookId = getIdPos(bookPos)

                if(linearL.visibility == View.VISIBLE) {
                    seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar?,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            pagA.text = progress.toString()
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {

                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                            val pagAconv = pagA.text.toString()
                            if (bookId != null) {
                                viewModel.numPage(bookId, pagAconv)
                                listaLibri[position].paginaAtt = pagAconv.toInt()
                            }

                            if(pagA.text == pagT.text && bookId != null){
                                viewModel.moveBooks(bookId,false, "in corso")
                                Toast.makeText(
                                    activity,
                                    "Complimenti hai terminato la tua lettura!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    })
                }
            }

            override fun hideShow(element: LinearLayout, arrow : ImageButton) {
                val linearL = element
                val btn = arrow
                if(linearL.visibility == View.GONE) {
                    val rotateAnimation = ObjectAnimator.ofFloat(btn, "rotation", 0f, 180f)
                    rotateAnimation.duration = 100
                    rotateAnimation.start()
                    linearL.visibility = View.VISIBLE
                }
                else {
                    val rotateAnimation = ObjectAnimator.ofFloat(btn, "rotation", 180f, 0f)
                    rotateAnimation.duration = 100
                    rotateAnimation.start()
                    linearL.visibility = View.GONE
                }


            }

            override fun dettaglioBook(cover: ImageButton, position: Int) {
                lifecycleScope.launch {
                    val libro = getLibro(position)

                    val navController = findNavController()
                    val action = CatalogoInCorsoDirections.actionCatalogoInCorsoToLibroInCorso(
                        libro!!,
                        "catalogoInCorso"
                    )
                    findNavController().navigate(action)
                }
            }

        })
        viewModel.checkBookCatalogo()
        if (!viewModel.libriInCorso.hasObservers()) {
            viewModel.libriInCorso.observe(viewLifecycleOwner, Observer { InCorsoBooksList ->
                loadBooks(InCorsoBooksList)
            })
        }

    }

    private suspend fun getLibro(position: Int): LibriInC? {
        val bookId = listaLibri[position].id
        var bookD: LibriInC? = null

        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val inCorsoRef = catalogoRef.child("InCorso")

            if (bookId != null) {
                val dataSnapshot = inCorsoRef.child(bookId).get().await()
                bookD = dataSnapshot.getValue(LibriInC::class.java)
            }
        }
        return bookD
    }

    private fun loadBooks(books: List<LibriInC>?){
        if (books != null) {
            listaLibri.clear()
            listaLibri.addAll(books)
            Log.d("lista", listaLibri.toString())
            adapter.notifyDataSetChanged()
        }
    }
    private fun getIdPos(position : Int): String? {
        if(listaLibri.isNotEmpty()){
            val bookId = listaLibri[position].id
            return bookId
        }
        return null
    }


}