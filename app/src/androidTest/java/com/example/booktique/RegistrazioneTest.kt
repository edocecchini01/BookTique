package com.example.booktique

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.booktique.view.registrazione
import com.google.firebase.auth.FirebaseAuth
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RegistrazioneTest {

    private lateinit var scenario: FragmentScenario<registrazione>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer (themeResId = R.style.Theme_BookTique)
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    /*@Test
fun testRegisterSuccess() {

    FirebaseAuth.getInstance().signOut()
    Thread.sleep(6000)
    // Simula l'inserimento di un'email e password validi
    Espresso.onView(ViewMatchers.withId(R.id.username))
        .perform(ViewActions.typeText("prova"), ViewActions.closeSoftKeyboard())

    Espresso.onView(ViewMatchers.withId(R.id.email))
        .perform(ViewActions.typeText("laura2@gmail.com"), ViewActions.closeSoftKeyboard())
    Espresso.onView(ViewMatchers.withId(R.id.password))
        .perform(ViewActions.typeText("Lauretta"), ViewActions.closeSoftKeyboard())

    // Fai clic sul pulsante di login
    Espresso.onView(ViewMatchers.withId(R.id.btn_register)).perform(ViewActions.click())

    // Aspetta un po' per consentire il completamento del processo di login
    Thread.sleep(6000)

    // Verifica se l'utente è autenticato
    val firebaseAuth = FirebaseAuth.getInstance()
    val user = firebaseAuth.currentUser
    Thread.sleep(6000)

    // Assicurati che l'utente sia autenticato
    Assert.assertNotNull(user)
    Thread.sleep(6000)

    FirebaseAuth.getInstance().signOut()

}*/

    @Test
   fun testRegisterFailure() {
        //FirebaseAuth.getInstance().signOut()
        Thread.sleep(6000)
        // Simula l'inserimento di un'email e password non validi
        Espresso.onView(ViewMatchers.withId(R.id.username))
            .perform(ViewActions.typeText("laura01"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.email))
            .perform(ViewActions.typeText("example@gmail.c"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.password))
            .perform(ViewActions.typeText("wrongpassword"), ViewActions.closeSoftKeyboard())

        // Fai clic sul pulsante di login
        Espresso.onView(ViewMatchers.withId(R.id.btn_register)).perform(ViewActions.click())
        Thread.sleep(6000)
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        Assert.assertNull(user)

    }

}
