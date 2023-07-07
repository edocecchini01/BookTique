package com.example.booktique

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.booktique.databinding.FragmentLibroInCorsoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LibroInCorso : Fragment() {
    private lateinit var binding: FragmentLibroInCorsoBinding
    private lateinit var cUser : FirebaseUser
    private lateinit var libroIncorso: LibriInC
    private lateinit var activity : FragmentActivity
    private val sezioni = arrayListOf("Letti","Da Leggere")

    private val args by navArgs<LibroInCorsoArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = DataBindingUtil.inflate<FragmentLibroInCorsoBinding>(inflater,
            R.layout.fragment_libro_in_corso,container,false)

        return binding.root
    }


    companion object {
        fun newInstance(libroIncorso: LibriInC): LibroInCorso {
            val fragment = LibroInCorso()
            fragment.libroIncorso = libroIncorso
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                if (FirebaseAuth.getInstance().currentUser != null) {
                    val cUser = FirebaseAuth.getInstance().currentUser!!
                    Log.d("TAG", "Sono :")
                    val database =
                        FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
                    val usersRef = database.reference.child("Utenti")
                    val childRef = usersRef.child(cUser.uid)
                    val catalogoRef = childRef.child("Catalogo")
                    val inCorsoRef = catalogoRef.child("InCorso")

                    if (bookId != null) {
                        val pagAconv = binding.textView25.text.toString()
                        inCorsoRef.child(bookId).child("paginaAtt").setValue(pagAconv.toInt())
                    }
                }

                if (binding.textView25.text == binding.textView26.text && bookId != null) {
                    moveBooks(bookId,false)
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
                        Log.d("TAG", "idLibro: $bookId")
                        moveBooks(bookId,where)
                    }
                } else {
                    Toast.makeText(requireContext(), "Seleziona un elemento!", Toast.LENGTH_SHORT).show()
                }
                // Chiudi il dialog
                dialog?.dismiss()
            }

            dialog = builder.create()
            dialog?.show()
        }

        binding.elimina.setOnClickListener {
            var dialog: AlertDialog? = null
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.dialog_elimina, null)
            val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)
            val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
            builder.setView(dialogView)

            btnConfirm.setOnClickListener {
                if (bookId != null) {
                    removeBook(bookId)
                }else{
                    Toast.makeText(requireContext(), "Errore nell'eliminazione!", Toast.LENGTH_SHORT).show()
                }
                dialog?.dismiss()
            }

            dialog = builder.create()
            dialog?.show()
        }


    }

    private fun moveBooks(bookId : String, where : Boolean) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val daLeggereRef = catalogoRef.child("DaLeggere")
            val inCorsoRef = catalogoRef.child("InCorso")
            val lettiRef = catalogoRef.child("Letti")

            Log.d("TAG", "bookId: $bookId")

            if (!where){
                inCorsoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (childSnapshot in dataSnapshot.children) {
                            val libro = childSnapshot.getValue(LibriDaL::class.java)

                            if (libro != null && libro.id == bookId) {

                                // Hai individuato il libro desiderato
                                Log.d("Libro", "Libro trovato: $libro")
                                lettiRef.child(bookId).setValue(libro)
                                val libroRef = childSnapshot.ref
                                Log.d("Libro", "Libro da eliminare: $libro")
                                libroRef.removeValue()

                                Toast.makeText(
                                    activity,
                                    "${libro.titolo?.take(50)}, spostato in \"Letti\"",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val navController = Navigation.findNavController(activity, R.id.fragmentContainerView)
                                navController.navigate(R.id.action_libroInCorso_to_catalogoLetti)

                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            } else{
                inCorsoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (childSnapshot in dataSnapshot.children) {
                            val libro = childSnapshot.getValue(LibriDaL::class.java)

                            if (libro != null && libro.id == bookId) {

                                // Hai individuato il libro desiderato
                                Log.d("Libro", "Libro trovato: $libro")
                                daLeggereRef.child(bookId).setValue(libro)
                                val libroRef = childSnapshot.ref
                                Log.d("Libro", "Libro da eliminare: $libro")
                                libroRef.removeValue()
                                Toast.makeText(
                                    activity,
                                    "${libro.titolo?.take(50)}, spostato in \"Da leggere\"",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val navController = Navigation.findNavController(activity, R.id.fragmentContainerView)
                                navController.navigate(R.id.action_libroInCorso_to_catalogoDaLeggere)


                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }
        }

    }

    private fun removeBook(bookId : String){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val inCorsoRef = catalogoRef.child("InCorso")

            inCorsoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        val libro = childSnapshot.getValue(LibriDaL::class.java)

                        if (libro != null && libro.id == bookId) {
                            val libroRef = childSnapshot.ref

                            val navController = findNavController()
                            navController.navigate(R.id.action_libroInCorso_to_catalogoHome)
                            libroRef.removeValue()
                            Toast.makeText(
                                requireContext(),
                                "${libro.titolo?.take(50)}, eliminato con successo!",
                                Toast.LENGTH_SHORT
                            ).show()
                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Errore nello spostamento!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
        }
    }

}