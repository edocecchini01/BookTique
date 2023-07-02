package com.example.booktique

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.booktique.databinding.FragmentCatalogoHomeBinding
import com.example.booktique.AutenticazioneActivity
import com.example.booktique.FragmentUtils.replaceFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CatalogoHome : Fragment() {
    private lateinit var binding: FragmentCatalogoHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentCatalogoHomeBinding>(
            inflater,
            R.layout.fragment_catalogo_home, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val fragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView)

        if (fragment is NavHostFragment) {
            val navController = fragment.navController
            binding.daLeggereLabel.setOnClickListener {
                navController.navigate(R.id.action_catalogoHome_to_catalogoDaLeggere)
            }
        } else {
            // Il frammento corrente non è un'istanza di NavHostFragment
            // Gestisci l'errore o il flusso alternativo
        }

        //val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        //val navController = navHostFragment.navController

        checkBookCatalogo()

        if (FirebaseAuth.getInstance().currentUser != null) {
            binding.myButton.setOnClickListener {
                replaceFragment(Impostazioni())
                val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNavigationView.selectedItemId = R.id.impostazioniPulsante
            }
        } else {
            replaceFragment(CatalogoHomeBlock())
            binding.myButton.setOnClickListener {
                val intent = Intent(requireContext(), AutenticazioneActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()
    }

    private fun checkBookCatalogo(){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            Log.d("TAG", "Sono :")
            val database = FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val daLeggereRef = catalogoRef.child("DaLeggere")
            /*
            val lettiRef = catalogoRef.child("Letti")
            val inCorsoRef = catalogoRef.child("InCorso")

             */

            daLeggereRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val daLeggereBooks = arrayListOf<LibriDaL>()
                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val libriDaL = bookSnapshot.getValue(LibriDaL::class.java)
                            Log.d("TAG", "VolumeDet : $libriDaL")
                            daLeggereBooks.add(libriDaL!!)

                        }
                    }

                    // Richiama la funzione per i libri "DaLeggere"

                        loadImagesIntoImageButtonsCatalogo(daLeggereBooks, "daLeggere")

                }

                override fun onCancelled(error: DatabaseError) {
                    // Gestisci eventuali errori nella lettura dei dati
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })
            /*

            lettiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lettiBooks = mutableListOf<LibriDaL>()
                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val volumeDet = bookSnapshot.getValue(LibriDaL::class.java)
                            volumeDet?.let {
                                lettiBooks.add(it)
                            }
                        }
                    }


                    loadImagesIntoImageButtonsCatalogo(lettiBooks, "Letti")
                }

                override fun onCancelled(error: DatabaseError) {
                    // Gestisci eventuali errori nella lettura dei dati
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })


            inCorsoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val inCorsoBooks = mutableListOf<LibriDaL>()
                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val volumeDet = bookSnapshot.getValue(LibriDaL::class.java)
                            volumeDet?.let {
                                inCorsoBooks.add(it)
                            }
                        }
                    }

                    // Richiama la funzione per i libri "InCorso"
                    loadImagesIntoImageButtonsCatalogo(inCorsoBooks, "InCorso")
                }

                override fun onCancelled(error: DatabaseError) {

                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })

             */
        }


        }



    private fun loadImagesIntoImageButtonsCatalogo(books: List<LibriDaL>?, tipologia: String) {
        val imageButtons = listOf(
            binding.bookLetti1,
            binding.bookLetti2,
            binding.bookLetti3,
            binding.bookLetti4,
            binding.bookLetti5,
            binding.bookLetti6,

        )

        val targetButtons = if (tipologia == "daLeggere") {
            imageButtons
        } else if (tipologia == "Letti") {
            listOf(
                binding.bookLeggere1,
                binding.bookLeggere2,
                binding.bookLeggere3,
                binding.bookLeggere4,
                binding.bookLeggere5,
                binding.bookLeggere6
            )
        } else {
            listOf(
                binding.bookInC1,
                binding.bookInC2,
                binding.bookInC3,
                binding.bookInC4,
                binding.bookInC5,
                binding.bookInC6
            )
        }

        // Verifica che la lista dei libri non sia nulla e contenga almeno 6 elementi
        if (books != null ) {
            for (i in 0 until minOf(books.size, 6)) {
                val book = books[i]
                val imageUrl = book.copertina
                Log.d("Image", "imageUrl: $imageUrl")
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(binding.root.context)
                        .load(imageUrl)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                e?.let {
                                    // Ottieni la lista delle cause radice dell'eccezione
                                    val rootCauses = e.rootCauses
                                    for (cause in rootCauses) {
                                        // Stampa le informazioni sulla causa dell'errore
                                        Log.e("Glide1", "Root cause: ${cause.message}")
                                    }
                                }
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                // L'immagine è stata caricata con successo
                                return false
                            }
                        })

                        .into(targetButtons[i])

                    //setupImageButtonClickListener(book, targetButtons[i])

                }
            }
        }
    }



    private fun setupImageButtonClickListener(book: LibriDaL, imageButton: ImageButton) {
        imageButton.setOnClickListener {

        }
    }


}
