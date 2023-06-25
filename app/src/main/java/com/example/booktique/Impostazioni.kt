package com.example.booktique

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.booktique.databinding.FragmentImpostazioniBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

private lateinit var binding: FragmentImpostazioniBinding

class Impostazioni : Fragment() {

    lateinit var auth : FirebaseAuth
    lateinit var btn: Button
    lateinit var textView: TextView
    lateinit var user : FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentImpostazioniBinding>(
            inflater,
            R.layout.fragment_impostazioni, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        btn = binding.logout
        textView = binding.username
        user = auth.currentUser!!
        if(user == null){
            //qualcosa che manda al login
        }
        else {
            textView.setText(user.email)
        }

        btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), AutenticazioneActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}