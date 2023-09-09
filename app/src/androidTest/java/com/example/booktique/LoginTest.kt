package com.example.booktique

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.booktique.view.Login
import com.google.firebase.auth.FirebaseAuth


import org.junit.Test
import org.junit.runner.RunWith


import org.junit.Assert.*
import org.junit.Before


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class LoginTest {

    private lateinit var scenario:  FragmentScenario<Login>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer (themeResId = R.style.Theme_BookTique)
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun testLoginSuccess() {
        FirebaseAuth.getInstance().signOut()
        Thread.sleep(3000)
        // Simula l'inserimento di un'email e password validi
        onView(withId(R.id.email)).perform(typeText("laura@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(typeText("Lauretta"), closeSoftKeyboard())

        // Fai clic sul pulsante di login
        onView(withId(R.id.btn_login)).perform(click())

        // Aspetta un po' per consentire il completamento del processo di login
        Thread.sleep(6000)

        // Verifica se l'utente è autenticato
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        // Assicurati che l'utente sia autenticato
        assertNotNull(user)

        FirebaseAuth.getInstance().signOut()

    }

        @Test
        fun testLoginFailure() {
            FirebaseAuth.getInstance().signOut()
            Thread.sleep(3000)
            // Simula l'inserimento di un'email e password non validi
            onView(withId(R.id.email)).perform(typeText("example@gmail.com"), closeSoftKeyboard())
            onView(withId(R.id.password)).perform(typeText("wrongpassword"), closeSoftKeyboard())

            // Fai clic sul pulsante di login
            onView(withId(R.id.btn_login)).perform(click())
            Thread.sleep(6000)
            val firebaseAuth = FirebaseAuth.getInstance()
            val user = firebaseAuth.currentUser

            assertNull(user)
        }

    }
