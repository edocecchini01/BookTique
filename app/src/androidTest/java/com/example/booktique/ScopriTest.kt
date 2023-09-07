package com.example.booktique

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Rule
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.booktique.view.MainActivity
import org.hamcrest.CoreMatchers.allOf

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ScopriTest {


    @get:Rule
     var scenario: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun testGeneriBook() {
        // Simula l'inserimento di un testo di ricerca
        //FirebaseAuth.getInstance().signInWithEmailAndPassword("laura@gmail.com", "Lauretta")

        //Thread.sleep(6000)

        //onView(allOf(withId(R.id.scopriPulsante), isDescendantOfA(withId(R.id.bottomNavigationView))))
          //  .perform(click()) // Fai clic sul pulsante di scopri
        Thread.sleep(6000)

        onView(withId(R.id.book_in_c1))
            .perform(click())

        onView(withId(R.id.linearLayout4))
            .check(matches(isDisplayed()))
        Thread.sleep(2000)

        onView(withId(R.id.imageButton2))
            .perform(click())

        Thread.sleep(2000)
        onView(withId(R.id.button3))
            .perform(click())

        Thread.sleep(6000)
        // Verifica che la RecyclerView dei risultati di ricerca sia visualizzata
        onView(withId(R.id.lista_libri_scopri_genere))
            .check(matches(isDisplayed()))
        Thread.sleep(2000)

        onView(withId(R.id.backbuttonGen))
            .perform(click())

        Thread.sleep(4000)

        onView(withId(R.id.book_leggere2))
            .perform(click())

        onView(withId(R.id.linearLayout4))
            .check(matches(isDisplayed()))
        Thread.sleep(2000)

        onView(withId(R.id.buttonAggiungi))
            .perform(click())
        Thread.sleep(2000)

        onView(withId(R.id.buttonAggiungi))
            .check(matches(withText("Aggiunto")))

        Thread.sleep(2000)

        onView(withId(R.id.imageButton2))
            .perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.buttonPerTe))
            .perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.linearL))
            .check(matches(isDisplayed()))

    }

}