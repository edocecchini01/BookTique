package com.example.booktique

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.booktique.databinding.FragmentRegistrazioneBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

private lateinit var binding: FragmentRegistrazioneBinding
class registrazione : Fragment() {

    lateinit var editTextEmail :EditText
    lateinit var editTextPassword :EditText
    lateinit var buttonReg :Button
    lateinit var mAuth :FirebaseAuth

    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    interface ClickListener {
        fun onScrittaClicked()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentRegistrazioneBinding>(inflater, R.layout.fragment_registrazione,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clickListener = object : ClickListener {
            override fun onScrittaClicked() {
                Navigation.findNavController(view).navigate(R.id.action_registrazione_to_login)
            }
        }
        mAuth = FirebaseAuth.getInstance()

        editTextEmail = binding.email
        editTextPassword = binding.password
        buttonReg = binding.btnRegister

        buttonReg.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if(TextUtils.isEmpty(email)){
                Toast.makeText(requireContext(),"Inserisci un email",Toast.LENGTH_SHORT).show()
            }

            if(TextUtils.isEmpty(password)){
                Toast.makeText(requireContext(),"Inserisci una password",Toast.LENGTH_SHORT).show()
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Account creato!",
                            Toast.LENGTH_SHORT,
                        ).show()
                        Navigation.findNavController(view).navigate(R.id.action_registrazione_to_login)

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            requireContext(),
                            "Creazione fallita!",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

        }
}
}