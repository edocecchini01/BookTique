package com.example.booktique

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

object FragmentUtils {
        fun replaceFragment(fragmentManager: FragmentManager, containerId: Int, fragment: Fragment) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(containerId, fragment)
            fragmentTransaction.commit()
        }

    }

