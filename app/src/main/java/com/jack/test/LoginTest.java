package com.jack.test;

import android.content.Intent;

import com.jack.domoscrum.Login;
import com.jack.domoscrum.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;

import static org.junit.Assert.assertThat;


/**
 * Created by Jhon_Albert on 27/01/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
public class LoginTest   {

    @Test
    public void clickingLogin_shouldStartLoginActivity() {
        Login activity = Robolectric.setupActivity(Login.class);
        activity.findViewById(R.id.light).performClick();
        Intent expectedIntent = new Intent(activity, Login.class);
        //assertThat(shadowOf(activity).getNextStartedActivity()).isEqualTo(expectedIntent);
    }
}
