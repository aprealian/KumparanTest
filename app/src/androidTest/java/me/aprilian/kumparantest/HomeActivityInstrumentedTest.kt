package me.aprilian.kumparantest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import me.aprilian.kumparantest.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class HomeActivityInstrumentedTest : BaseUiTest() {

    @Rule
    @JvmField
    val activityRule  =  ActivityTestRule(MainActivity::class.java)

    @Test
    @Throws(Exception::class)
    fun homeActivityTest() {
        checkEveryPage()
    }

    @Test
    @Throws(Exception::class)
    fun checkEveryPage() {
        //waiting load the content
        delay(5000)
        //click item list
        onView(withId(R.id.rvPosts)).perform(click())
        //waiting load content
        delay(5000)
        //open user detail
        performClick(R.id.header)
        //waiting load content
        delay(10000)
    }

}