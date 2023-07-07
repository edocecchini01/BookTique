package com.example.booktique

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.booktique.databinding.FragmentDettaglioLibroScopriBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class DettaglioLibroScopri : Fragment() {

    private lateinit var binding: FragmentDettaglioLibroScopriBinding
    private lateinit var cUser : FirebaseUser
    private val args by navArgs<DettaglioLibroScopriArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentDettaglioLibroScopriBinding>(inflater,
            R.layout.fragment_dettaglio_libro_scopri,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkBookAdded()

        binding.titolo.text = args.LibroLeggere.titolo
        binding.autore.text = args.LibroLeggere.autori
        binding.descrizione.text = args.LibroLeggere.descrizione




        val imageView = binding.copertina
        Glide.with(this)
            .load(args.LibroLeggere.copertina)
            .into(imageView)

        binding.buttonAggiungi.setOnClickListener {
            aggiungiLibro()
        }
        binding.imageButton2.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_dettaglioLibroScopri_to_scopri)
        }


    }


    private fun aggiungiLibro(){
        if(FirebaseAuth.getInstance().currentUser != null) {
            cUser = FirebaseAuth.getInstance().currentUser!!
            Log.d("TAG", "Sono qui")
            val database = FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val daLeggereRef = catalogoRef.child("DaLeggere")

            val libro = args.LibroLeggere
            Log.d("TAG", "libro: $libro")



            val nuovoLibroRef = daLeggereRef.push()
            nuovoLibroRef.setValue(libro)
                .addOnSuccessListener {
                    val grayColor = ContextCompat.getColor(requireContext(), R.color.gray) // Ottieni il colore grigio dal tuo file di risorse colors.xml
                    val whiteColor = ContextCompat.getColor(requireContext(), R.color.white)

                    binding.buttonAggiungi.isEnabled = false
                    binding.buttonAggiungi.text = "Aggiunto"
                    binding.buttonAggiungi.setBackgroundColor(grayColor)
                    binding.buttonAggiungi.setTextColor(whiteColor)
                }
                .addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Errore durante l'aggiunta del libro",
                        Toast.LENGTH_SHORT
                    ).show()
                }


        }else{
            Toast.makeText(
                requireContext(),
                "Errore",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }


    private fun checkBookAdded() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            Log.d("TAG", "Sono :")
            val database = FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val daLeggereRef = catalogoRef.child("DaLeggere")

            // Recupera l'ID del libro corrente
            val bookId = args.LibroLeggere.id

            Log.d("TAG", "Sono : $bookId")
            // Verifica se il libro è stato aggiunto dall'utente
            daLeggereRef.orderByChild("id").equalTo(bookId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookExists = snapshot.exists()

                    // Imposta lo stato del pulsante "Aggiungi" in base al libro aggiunto
                    binding.buttonAggiungi.isEnabled = !bookExists

                    if (bookExists) {
                        val grayColor = ContextCompat.getColor(requireContext(), R.color.gray) // Ottieni il colore grigio dal tuo file di risorse colors.xml
                        val whiteColor = ContextCompat.getColor(requireContext(), R.color.white)

                        binding.buttonAggiungi.isEnabled = false
                        binding.buttonAggiungi.text = "Aggiunto"
                        binding.buttonAggiungi.setBackgroundColor(grayColor)
                        binding.buttonAggiungi.setTextColor(whiteColor)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Gestisci eventuali errori nella lettura dei dati
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })
        }
    }


}