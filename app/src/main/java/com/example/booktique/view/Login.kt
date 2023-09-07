package com.example.booktique.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.booktique.R
import com.example.booktique.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth


class Login : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    lateinit var editTextEmail : EditText
    lateinit var editTextPassword : EditText
    lateinit var buttonLogin : Button
    lateinit var mAuth : FirebaseAuth


    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

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

        mAuth = FirebaseAuth.getInstance()

        editTextEmail = binding.email
        editTextPassword = binding.password
        buttonLogin = binding.btnLogin

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()


            if ( email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNotEmpty() && password.isNotEmpty()) {

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Login effettuato!",
                                Toast.LENGTH_SHORT,
                            ).show()
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Login fallito!",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }else{
                Toast.makeText(requireContext(), "Inserisci email e password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

