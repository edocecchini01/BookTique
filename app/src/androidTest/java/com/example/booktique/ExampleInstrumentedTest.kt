package com.example.booktique

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import com.google.firebase.auth.FirebaseAuth


import org.junit.Test
import org.junit.runner.RunWith


import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule




/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    private lateinit var scenario:  FragmentScenario<Login>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer (themeResId = R.style.Theme_BookTique)
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun testLoginSuccess() {
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
