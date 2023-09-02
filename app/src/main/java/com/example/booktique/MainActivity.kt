package com.example.booktique

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.booktique.R
import com.example.booktique.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController


        if(FirebaseAuth.getInstance().currentUser != null) {
            binding.bottomNavigationView.selectedItemId = R.id.scopriPulsante
            binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->


                when (menuItem.itemId) {

                    R.id.scopriPulsante -> navController.navigate(R.id.scopri)
                    R.id.catalogoPulsante -> navController.navigate(R.id.catalogoHome)
                    R.id.impostazioniPulsante -> navController.navigate(R.id.impostazioni)
                }
                true
            }
        }else{
            binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.catalogoPulsante -> navController.navigate(R.id.catalogoHomeBlock)
                    R.id.scopriPulsante -> navController.navigate(R.id.scopri)
                    R.id.impostazioniPulsante -> navController.navigate(R.id.impostazioni)
                }
                true
            }
        }

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        if (!sharedPrefs.contains("currentIndex")) {
            val editor = sharedPrefs.edit()
            editor.putInt("currentIndex", 0)
            editor.apply()
        }else{
            val currentIndex = 0
            sharedPrefs.edit().putInt("currentIndex", currentIndex).apply()
        }

    }


    override fun onBackPressed() {
        val currentDestinationId = navController.currentDestination?.id

        // Verifica se l'utente è in una delle pagine raggiungibili dalla bottom navigation bar
        val isBottomNavPage = when (currentDestinationId) {
            R.id.scopri, R.id.catalogoHome, R.id.impostazioni -> true
            else -> false
        }

        if (isBottomNavPage) {
            finish()
        } else {

            super.onBackPressed()
        }
    }

    }