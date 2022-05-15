package com.example.myapplication;

import android.content.Context;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MyAppEspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

//    @Test
//    public void recyclerTest () {
//
//
//        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//        onView(withId(R.id.text_task_name)).check(matches (withText("Task1")));
//    }

//    @Test
//    public void settingsPageTest(){
//        onView(withId(R.id.action_setting)).perform(click());
//        onView(withId(R.id.setting_activity)).check(matches(isDisplayed()));
//        onView(withId(R.id.task_title))
//                .perform(typeText("mohm"), closeSoftKeyboard());
//        onView(withId(R.id.save)).perform(click());
//        pressBack();
//        onView(withId(R.id.userTasks)).check(matches(withText("mohm")));
//    }


    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.myapplication", appContext.getPackageName());
    }

    @Test
    public void allTask(){
        onView(withId(R.id.all_task)).perform(click());
        onView(withId(R.id.all_task_activity)).check(matches(isDisplayed()));
    }
}


