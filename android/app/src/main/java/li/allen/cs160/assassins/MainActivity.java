package li.allen.cs160.assassins;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.LogInCallback;
import com.parse.ParseException;

public class MainActivity extends Activity {

    FragmentManager fragmentManager;
    boolean individual; boolean team; boolean day; boolean threeDays; boolean week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            HomeFragment home = new HomeFragment();
            fragmentTransaction.replace(R.id.container, home, "home");
            fragmentTransaction.commit();

        } else {
            //Welcome Fragment
            WelcomeFragment welcome = new WelcomeFragment();
            fragmentTransaction.replace(R.id.container, welcome, "welcome");
            fragmentTransaction.commit();
        }



    }

    //Logs user out and returns to welcome Fragment
    public void logout(View view) {
        ParseUser.logOut();
        WelcomeFragment welcome = new WelcomeFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, welcome, "welcome");
        fragmentTransaction.commit();

    }

    public void createGame(View view) {
        GameCreationFragment createGame = new GameCreationFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, createGame, "createGame");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //Sets individual true and team false
    public void individualOn(View view) {
        individual = true;
        team = false;
    }

    //Sets team true and individual false
    public void teamOn(View view) {
        team = true;
        individual = false;
    }

    //Sets reshuffle time to 1 day
    public void dayOn(View view) {
        day = true;
        threeDays = false;
        week = false;
    }

    //Sets reshuffle time to three days
    public void threeDaysOn(View view) {
        threeDays = true;
        day = false;
        week = false;
    }

    //Sets reshuffle time to 1 week
    public void weekOn(View view) {
        week = true;
        day = false;
        threeDays = false;
    }


    //Calls LoginFragment login method
    public void signUp(View view) {
        LoginFragment fragment = (LoginFragment) fragmentManager.findFragmentByTag("login");
        fragment.signUp(view);
    }

    //Replaces screen to log in screen
    public void loginFragment(View view) {
        SignInFragment login = new SignInFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, login, "signIn");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //Calls signInFragment login method
    public void login(View view) {
        SignInFragment fragment = (SignInFragment) fragmentManager.findFragmentByTag("signIn");
        fragment.login(view);
    }

    //Redirects to sign up fragment
    public void toSignUp(View view) {
        // SignUp Fragment
        LoginFragment login = new LoginFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, login, "login");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
