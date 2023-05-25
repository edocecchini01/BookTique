package com.example.booktique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.booktique.databinding.FragmentLoginBinding

private lateinit var binding: FragmentLoginBinding
class Login : Fragment() {

    interface ClickListenerLogin {
        fun onScrittaClicked()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
         binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater,
            R.layout.fragment_login,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clickListenerLogin = object: ClickListenerLogin {
            override fun onScrittaClicked() {
                Navigation.findNavController(view).navigate(R.id.action_login_to_registrazione)
            }
        }
    }
}

