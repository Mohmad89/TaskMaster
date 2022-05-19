package com.example.myapplication;

import android.content.Context;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.myapplication.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
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
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.myapplication", appContext.getPackageName());
    }


    // Test AllTask Button
    @Test
    public void allTask(){
        onView(withId(R.id.all_task)).perform(click());
        onView(withId(R.id.all_task_activity)).check(matches(isDisplayed()));
    }

    // Test Setting Button
    @Test
    public void settingPage() {

        String username = "mohammad";
        openActionBarOverflowOrOptionsMenu(mainActivity.getActivity());
        onView(withText("Setting")).perform(click());
//        onView(withId(R.id.setting_activity)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_username)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.create_username)).perform(click());
        onView(withId(R.id.text_username)).check(matches(withText(username)));
    }


    // This task to add new Task and move to the taskDetails Page to check if the title for task is right or not
    @Test
    public void addTask () {

        String task = "Task1";
        String desc = "FrontEnd Development";
        onView(withId(R.id.add_task)).perform(click());
        onView(withId(R.id.add_task_activity)).check(matches(isDisplayed()));
        onView(withId(R.id.task_name)).perform(typeText(task), closeSoftKeyboard());
        onView(withId(R.id.task_description)).perform(typeText(desc), closeSoftKeyboard());
        onView(withId(R.id.submit_task)).perform(click());
        pressBack();
        onView(withId(R.id.main_activity)).check(matches(isDisplayed()));
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.text_task_name)).check(matches(withText(task)));

    }

}


