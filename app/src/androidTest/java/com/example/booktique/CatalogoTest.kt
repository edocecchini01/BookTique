package com.example.booktique

import androidx.test.core.app.ActivityScenario
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.booktique.view.MainActivity
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


    @get:Rule
    var scenario: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testMoveInCorso() {
            // Altre asserzioni e azioni necessarie per preparare il test



            // Verifica se l'utente è autenticato
            //val firebaseAuth = FirebaseAuth.getInstance()
            //val user = firebaseAuth.currentUser
            //assertNotNull(user)
            //onView(allOf(withId(R.id.catalogoPulsante), isDescendantOfA(withId(R.id.bottomNavigationView))))
            //  .perform(click()) // Fai clic sul pulsante di scopri
            Thread.sleep(6000)

            onView(withId(R.id.book_letti1)).perform(click())

            Thread.sleep(2000)

            onView(withId(R.id.linearLayout4)).check(matches(isDisplayed()))

            Thread.sleep(2000)

            onView(withId(R.id.buttonSposta1)).perform(click())


            onView(withId(R.id.spinner)).check(matches(isDisplayed()))

            Thread.sleep(2000)
            onView(withId(R.id.btn_confirm)).perform(click())

            Thread.sleep(3000)

            // Verifica se l'utente viene reindirizzato correttamente in CatalogoInCorso
            onView(withId(R.id.lista_libri_corso)).check(matches(isDisplayed()))

    }
}
