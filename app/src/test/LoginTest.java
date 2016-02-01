package test.java.;


import android.test.suitebuilder.annotation.LargeTest;

import com.jack.domoscrum.Login;
import com.jack.domoscrum.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    private static final String STRING_TO_BE_TYPED = "Peter";

    @Rule
    public ActivityTestRule<Login> mActivityRule = new ActivityTestRule<>(
            Login.class);

    @Test
    public void acceder() {
        onView(withId(R.id.name)).perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard()); //line 1

        onView(withText("Aceptar")).perform(click()); //line 2

        //String expectedText = "Hello, " + STRING_TO_BE_TYPED + "!";
        //onView(withId(R.id.textView)).check(matches(withText(expectedText))); //line 3

    }
}