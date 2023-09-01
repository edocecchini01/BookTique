package com.example.booktique

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AutenticazioneViewModel: ViewModel() {
    lateinit var cUser : FirebaseUser

    fun registrazione(username: String, password: String, email: String){
        if (FirebaseAuth.getInstance().currentUser != null) {
            cUser = FirebaseAuth.getInstance().currentUser!!
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")

            usersRef.child(cUser.uid ?: "")
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {

                            val user = Utenti(
                                email = email,
                                password = password,
                                username = username,
                                catalogo = Catalogo(
                                    libriDaLeggere = emptyList(),
                                    libriInCorso = emptyList(),
                                    libriLetti = emptyList()
                                )
                            )
                            usersRef.child(cUser.uid).setValue(user)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }
    }

}