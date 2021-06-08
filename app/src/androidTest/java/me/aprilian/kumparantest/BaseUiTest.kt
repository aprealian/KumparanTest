package me.aprilian.kumparantest

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.hamcrest.Matcher
import org.junit.Before


open class BaseUiTest{

    private lateinit var uiDevice: UiDevice

    @Before
    fun setUpAll(){
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    private fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun perform(uiController: UiController?, view: View?) {
                uiController?.loopMainThreadForAtLeast(delay)
            }

            override fun getConstraints(): Matcher<View>? {
                return isRoot()
            }

            override fun getDescription(): String {
                return "wait for " + delay + "milliseconds"
            }
        }
    }

    fun delay(delay: Long = 1000){
        Espresso.onView(isRoot()).perform(waitFor(delay))
    }

    fun performClick(view: Int){
        Espresso.onView(withId(view)).perform(click())
    }
}