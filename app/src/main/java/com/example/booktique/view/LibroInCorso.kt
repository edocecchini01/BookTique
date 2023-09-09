package com.example.booktique.view

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.booktique.dataModel.LibriInC
import com.example.booktique.R
import com.example.booktique.databinding.FragmentLibroInCorsoBinding
import com.example.booktique.viewModel.CatalogoViewModel
import com.google.firebase.auth.FirebaseUser

class LibroInCorso : Fragment() {
    private lateinit var binding: FragmentLibroInCorsoBinding
    private lateinit var cUser : FirebaseUser
    private lateinit var libroIncorso: LibriInC
    private lateinit var activity : FragmentActivity
    private lateinit var viewModel: CatalogoViewModel
    private val sezioni = arrayListOf("Letti","Da Leggere")

    private val args by navArgs<LibroInCorsoArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = DataBindingUtil.inflate<FragmentLibroInCorsoBinding>(
            inflater,
            R.layout.fragment_libro_in_corso, container, false
        )

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CatalogoViewModel::class.java)
        activity = requireActivity()
        val bookId = args.LibroInC.id
        val imageView = binding.imageView3
        Glide.with(this)
            .load(args.LibroInC.copertina)
            .into(imageView)

        binding.textView22.text = args.LibroInC.titolo
        binding.textView23.text = args.LibroInC.autori

        if (args.LibroInC.pagineTot != 0){
            binding.textView26.text = args.LibroInC.pagineTot.toString()
        binding.seekBar4.max = args.LibroInC.pagineTot!!
        binding.seekBar4.progress = args.LibroInC.paginaAtt!!
        binding.textView25.text = args.LibroInC.paginaAtt.toString()

        //gestione dell'elemento seekBar usato per salvare il numero di pagina attuale
        binding.seekBar4.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                binding.textView25.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                if (bookId != null) {
                    val pagAconv = binding.textView25.text.toString()
                    viewModel.numPage(bookId, pagAconv)
                }

                if (binding.textView25.text == binding.textView26.text && bookId != null) {
                    viewModel.moveBooks(bookId,false, "in corso")
                    Toast.makeText(
                        activity,
                        "Complimenti hai terminato la tua lettura!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })
    }else{
            binding.seekBar4.visibility = View.GONE
            binding.textView26.visibility = View.GONE
            binding.textView25.text = "Pagine totali non disponibili"
            binding.textView25.gravity = Gravity.CENTER
    }
        val origin = args.origin
        //gestione del pulsante indietro in base alla pagina di provenienza
        if (origin == "catalogoInCorso") {
            binding.imageButton.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.action_libroInCorso_to_catalogoInCorso)
            }
        }
        else if (origin == "catalogoHome") {
            binding.imageButton.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.action_libroInCorso_to_catalogoHome)
            }
        }
        //gestione del click sul pulsante sposta
        binding.buttonSposta.setOnClickListener {
            var dialog: AlertDialog? = null
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.move_spinner, null)
            var select = dialogView.findViewById<Spinner>(R.id.spinner)
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
                if (select.selectedItem != null) {
                    val selectedItem = select.selectedItem.toString()
                    var where = false

                    if(selectedItem == "Letti")
                        where = false
                    if(selectedItem == "Da Leggere")
                        where = true

                    if (bookId != null) {
                        viewModel.moveBooks(bookId,where, "in corso")
                        if(!where) {
                            Toast.makeText(requireContext(), "Libro spostato nella sezione Letti!", Toast.LENGTH_SHORT).show()

                        } else{
                            Toast.makeText(requireContext(), "Libro spostato nella sezione Da Leggere!", Toast.LENGTH_SHORT).show()
                        }

                        val navController = findNavController()
                        navController.navigate(R.id.action_libroInCorso_to_catalogoHome)
                    }
                } else {
                    Toast.makeText(requireContext(), "Seleziona un elemento!", Toast.LENGTH_SHORT).show()
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
        //gestione del click sul pulsante elimina
        binding.elimina.setOnClickListener {
            var dialog: AlertDialog? = null
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.dialog_elimina, null)
            val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)
            val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
            builder.setView(dialogView)

            btnConfirm.setOnClickListener {
                if (bookId != null) {
                    viewModel.removeBook(bookId, "In Corso")
                    val navController = findNavController()
                    navController.navigate(R.id.action_libroInCorso_to_catalogoHome)
                    Toast.makeText(
                        requireContext(),
                        "Libro eliminato con successo!",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    Toast.makeText(
                        requireContext(),
                        "Errore nell'eliminazione!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog?.dismiss()
            }

            btnCancel.setOnClickListener {
                dialog?.dismiss()
            }

            dialog = builder.create()
            dialog?.show()
        }


    }

}