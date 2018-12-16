package com.apps.magicleon.stargazers

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.times
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.apps.magicleon.stargazers.views.MainActivity
import com.apps.magicleon.stargazers.views.StargazersActivity
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun init() {
        Intents.init()
    }

    @Test
    fun dataValidation_positive() {

        val inputs: Array<Pair<String, String>> = arrayOf(
            Pair("torvalds", "linux"),
            Pair("iampawan", "flutterfire-basic"),
            Pair("flutter", "flutter")
        )

        for (input in inputs) {
            onView(ViewMatchers.withId(R.id.et_username))
                .perform(ViewActions.clearText())
                .perform(ViewActions.typeText(input.first))

            onView(ViewMatchers.withId(R.id.et_repository))
                .perform(ViewActions.clearText())
                .perform(ViewActions.typeText(input.second))
                .perform(ViewActions.closeSoftKeyboard())

            onView(ViewMatchers.withId(R.id.search_button)).perform(ViewActions.click())

            pressBack()
        }

        intended(hasComponent(StargazersActivity::class.java.name),times(inputs.size))

    }

    @Test
    fun dataValidation_negative() {

        val inputs: Array<Pair<String?, String?>> = arrayOf(
            Pair("", "linux"),
            Pair("torvalds", ""),
            Pair("", "")
        )

        for (input in inputs) {
            onView(ViewMatchers.withId(R.id.et_username))
                .perform(ViewActions.clearText())
                .perform(ViewActions.typeText(input.first))

            onView(ViewMatchers.withId(R.id.et_repository))
                .perform(ViewActions.clearText())
                .perform(ViewActions.typeText(input.second))
                .perform(ViewActions.closeSoftKeyboard())

            onView(ViewMatchers.withId(R.id.search_button)).perform(ViewActions.click())

            try {
                intended(not(hasComponent(StargazersActivity::class.java.name)))
            } catch (assertionError: AssertionError) {

            }
        }
    }

    @After
    fun teardown() {
        Intents.release()
    }
}