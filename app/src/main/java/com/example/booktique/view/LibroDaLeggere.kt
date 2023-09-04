package com.example.booktique.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.booktique.dataModel.LibriDaL
import com.example.booktique.R
import com.example.booktique.databinding.FragmentLibroDaLeggereBinding
import com.example.booktique.viewModel.CatalogoViewModel
import com.google.firebase.auth.FirebaseUser

class LibroDaLeggere : Fragment() {
    private lateinit var binding: FragmentLibroDaLeggereBinding
    private lateinit var cUser : FirebaseUser
    private lateinit var libroDaL: LibriDaL
    private val args by navArgs<LibroDaLeggereArgs>()
    private lateinit var origin: String
    private lateinit var viewModel: CatalogoViewModel
    private val sezioni = arrayListOf("In corso","Letti")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        binding = DataBindingUtil.inflate<FragmentLibroDaLeggereBinding>(
            inflater,
            R.layout.fragment_libro_da_leggere, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CatalogoViewModel::class.java)
        val bookId = args.LibroDaLeg.id
        val imageView = binding.imageView5
        Glide.with(this)
            .load(args.LibroDaLeg.copertina)
            .into(imageView)

        binding.textView26.text = args.LibroDaLeg.titolo
        binding.textView27.text = args.LibroDaLeg.autori
        binding.textView15.text = args.LibroDaLeg.descrizione

        origin = args.origin

        if (origin == "catalogoDaLeggere") {
            binding.imageButton2.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.action_libroDaLeggere_to_catalogoDaLeggere)
            }
        }
        else if (origin == "catalogoHome") {
            binding.imageButton2.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.action_libroDaLeggere_to_catalogoHome)
            }
        }

        binding.buttonSposta1.setOnClickListener {
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
                // Verifica se è stato selezionato un elemento
                if (select.selectedItem != null) {
                    val selectedItem = select.selectedItem.toString()
                    var where = false

                    if(selectedItem == "In corso")
                        where = false
                    if(selectedItem == "Letti")
                        where = true

                    if (bookId != null) {
                        viewModel.moveBooks(bookId, where, "da leggere")
                        if(!where) {
                            val navController = findNavController()
                            navController.navigate(R.id.action_libroDaLeggere_to_catalogoInCorso)
                        } else{
                            val navController = findNavController()
                            navController.navigate(R.id.action_libroDaLeggere_to_catalogoLetti)
                        }
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

        binding.elimina.setOnClickListener {
            var dialog: AlertDialog? = null
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.dialog_elimina, null)
            val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)
            val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
            builder.setView(dialogView)

            btnConfirm.setOnClickListener {
                if (bookId != null) {
                    viewModel.removeBook(bookId, "da leggere")
                    val navController = findNavController()
                    navController.navigate(R.id.action_libroDaLeggere_to_catalogoDaLeggere)
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

    /*private fun moveBooks(bookId : String, where : Boolean) {
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
                daLeggereRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (childSnapshot in dataSnapshot.children) {
                            val libro = childSnapshot.getValue(LibriDaL::class.java)

                            if (libro != null && libro.id == bookId) {

                                // Hai individuato il libro desiderato
                                Log.d("Libro", "Libro trovato: $libro")
                                inCorsoRef.child(bookId).setValue(libro)
                                val libroRef = childSnapshot.ref
                                Log.d("Libro", "Libro da eliminare: $libro")
                                libroRef.removeValue()
                                Toast.makeText(
                                    requireContext(),
                                    "${libro.titolo?.take(50)}, spostato in \"In corso\"",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val navController = findNavController()
                                navController.navigate(R.id.action_libroDaLeggere_to_catalogoInCorso)

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
            }else{
                daLeggereRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
                                    requireContext(),
                                    "${libro.titolo?.take(50)}, spostato in \"Letti\"",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val navController = findNavController()
                                navController.navigate(R.id.action_libroDaLeggere_to_catalogoLetti)

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

    private fun removeBook(bookId : String){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val daLeggereRef = catalogoRef.child("DaLeggere")
            daLeggereRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (childSnapshot in dataSnapshot.children) {
                            val libro = childSnapshot.getValue(LibriDaL::class.java)

                            if (libro != null && libro.id == bookId) {
                                val libroRef = childSnapshot.ref

                                val navController = findNavController()
                                navController.navigate(R.id.action_libroDaLeggere_to_catalogoHome)
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
    }*/

}