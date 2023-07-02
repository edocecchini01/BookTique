package com.example.booktique

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.booktique.databinding.ActivityMainBinding


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

        navController = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.catalogoPulsante -> navController.navigate(R.id.catalogoHome)
                R.id.scopriPulsante -> navController.navigate(R.id.scopri)
                R.id.impostazioniPulsante -> navController.navigate(R.id.impostazioni)
            }
            true
        }

    }

    }

