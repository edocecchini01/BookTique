package com.example.booktique

import android.content.Intent
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.lifecycle.Lifecycle
import com.example.booktique.MainActivity
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.booktique.LibroDaLeggere
import com.example.booktique.LibriDaL
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith


import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class CatalogoTest{

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun testMoveInCorso() {
        scenario.onActivity { mainActivity ->
            // Altre asserzioni e azioni necessarie per preparare il test

            Thread.sleep(3000)

            // Verifica se l'utente è autenticato
            val firebaseAuth = FirebaseAuth.getInstance()
            val user = firebaseAuth.currentUser
            assertNotNull(user)

            Thread.sleep(2000)

            onView(withId(R.id.book_letti1)).perform(click())

            Thread.sleep(2000)

            onView(withId(R.id.libroDaLeggere)).check(matches(isDisplayed()))

            // Esegui il codice che interagisce con l'interfaccia utente sul thread principale

                // Fai clic sul pulsante "Sposta"
                onView(withId(R.id.buttonSposta1)).perform(click())

                // Verifica se viene visualizzato l'AlertDialog di selezione
                onView(withId(R.id.spinner)).check(matches(isDisplayed()))

                // Seleziona l'opzione "In Corso" dalla selezione
                onView(withId(R.id.spinner)).perform(click())
                onData(allOf(`is`(instanceOf(String::class.java)), `is`("In corso"))).perform(click())

                // Fai clic sul pulsante di conferma
                onView(withId(R.id.btn_confirm)).perform(click())

            Thread.sleep(3000)

            // Verifica se l'utente viene reindirizzato correttamente in CatalogoInCorso
            onView(withId(R.id.catalogoInCorso)).check(matches(isDisplayed()))
        }
    }
}
