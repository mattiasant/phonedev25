package ut.cs.ee.phonedev25

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainMenuUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(HomePage::class.java)

    @Test
    fun mainMenu_displaysAllButtons() {
        onView(withId(R.id.playNupp)).check(matches(isDisplayed()))
        onView(withId(R.id.statsNupp)).check(matches(isDisplayed()))
        onView(withId(R.id.storeNupp)).check(matches(isDisplayed()))
        onView(withId(R.id.settingsNupp)).check(matches(isDisplayed()))
    }

    @Test
    fun clickingPlay_opensPlayActivity() {
        Intents.init()
        onView(withId(R.id.playNupp)).perform(click())
        Intents.intended(hasComponent(Join_Game::class.java.name))
        Intents.release()
    }

    @Test
    fun clickingStats_opensStatsActivity() {
        Intents.init()
        onView(withId(R.id.statsNupp)).perform(click())
        Intents.intended(hasComponent(StatisticsPage::class.java.name))
        Intents.release()
    }

    @Test
    fun clickingStore_opensStoreActivity() {
        Intents.init()
        onView(withId(R.id.storeNupp)).perform(click())
        Intents.intended(hasComponent(StorePage::class.java.name))
        Intents.release()
    }

    @Test
    fun clickingSettings_opensSettingsActivity() {
        Intents.init()
        onView(withId(R.id.settingsNupp)).perform(click())
        Intents.intended(hasComponent(GameSettingsPage::class.java.name))
        Intents.release()
    }
}