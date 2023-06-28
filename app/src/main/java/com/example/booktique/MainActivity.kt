package com.example.booktique

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.booktique.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(CatalogoHome())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.catalogoPulsante -> replaceFragment(CatalogoHome())
                R.id.scopriPulsante -> replaceFragment(Scopri())
                R.id.impostazioniPulsante -> replaceFragment(ScopriGenere())

                else -> {}
            }

            true
        }

    }

        private fun replaceFragment(fragment: Fragment){
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView,  fragment)
            fragmentTransaction.commit()

        }


    }

