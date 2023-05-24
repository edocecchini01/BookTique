package com.example.booktique

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }


        //provare con l'explicit intent

        /*
        bottomNavView = findViewById(R.id.bottomNavigationView)
        navController = findNavController(R.id.fragmentContainerView)
        bottomNavView.setupWithNavController(navController)

        bottomNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.catalogo-> {
                    navController.navigate(R.id.catalogoHome)
                    return@setOnItemSelectedListener true
                }
                R.id.scopriPulsante -> {
                    navController.navigate(R.id.action_catalogoHome_to_scopri)
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }*/


    }
}