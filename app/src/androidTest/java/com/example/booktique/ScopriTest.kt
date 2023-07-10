package com.example.booktique

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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
class ScopriTest {

    private lateinit var scenario:  FragmentScenario<Scopri>

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val command = "settings put global window_animation_scale 0"
        instrumentation.uiAutomation.executeShellCommand(command)
        instrumentation.waitForIdleSync()

        scenario = launchFragmentInContainer (themeResId = R.style.Theme_BookTique)
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun testSearchBook() {
        // Simula l'inserimento di un testo di ricerca
        FirebaseAuth.getInstance().signInWithEmailAndPassword("laura@gmail.com", "Lauretta")
        Thread.sleep(6000)
        val searchQuery = "Harry Potter"
        onView(withId(R.id.searchView)).perform(typeText(searchQuery), pressImeActionButton())
        Thread.sleep(6000)
        onView(withId(R.id.lista_libri_scopri_genere)).check(matches(isDisplayed()))
        Thread.sleep(6000)
        // Verifica che almeno un elemento sia presente nella RecyclerView
        onView(withId(R.id.lista_libri_scopri_genere)).check(matches(hasMinimumChildCount(1)))
        Thread.sleep(6000)

        // Verifica che nella RecyclerView sia presente un elemento con il titolo "Harry Potter"
        onView(withId(R.id.lista_libri_scopri_genere)).check(matches(hasDescendant(withText("Harry Potter"))))
    }



}