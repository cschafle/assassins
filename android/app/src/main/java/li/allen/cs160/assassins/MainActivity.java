package li.allen.cs160.assassins;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.LinkedHashSet;

import com.parse.ParseUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.FindCallback;



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

    //Join game
    public void joinGame(View view) {

        final ListView listView = (ListView) findViewById(R.id.gameList);

        int id = listView.getCheckedItemPosition();
        final String gameName = listView.getItemAtPosition(id).toString();

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", currentUser.getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    ParseUser user = users.get(0);
                    if ((Boolean) user.get("available")) {
                        user.put("available", false);
                        user.put("game", gameName);
                        user.saveInBackground();
                    }
                } else {
                    // Something went wrong.
                }
            }
        });
    }

    //Creates game as a parseObject
    public void create(View view) {

        EditText gameName = (EditText) findViewById(R.id.gameName);
        String sGameName = gameName.getText().toString();

        final ParseUser currentUser = ParseUser.getCurrentUser();
        final String creator = currentUser.getUsername();

        final ParseObject game = new ParseObject("Game");
        game.put("gameName", sGameName);
        if (individual) {
            game.put("gameMode", "individual");
        }
        else {
            game.put("gameMode", "team");
        }
        if (day) {
            game.put("reshuffle", "day");
        }
        else if (threeDays) {
            game.put("reshuffle", "threeDays");
        }
        else {
            game.put("reshuffle", "week");
        }
        game.put("advanced", "off");
        game.put("creator", creator);

        ListView listView = (ListView) findViewById(R.id.playersList);



        SparseBooleanArray checked = listView.getCheckedItemPositions();
        final ArrayList<String> users = new ArrayList<String>();
        int count = 0;
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            if (checked.get(i)) {
                // Do something
                count ++;
            }
        }
        final int countFinal = count;
        if (countFinal > 0) {
            for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                if (checked.get(i)) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", listView.getItemAtPosition(i));
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                users.add(objects.get(0).getUsername());
                                if (users.size() == countFinal) {

                                    users.add(creator);

                                    Set setItems = new LinkedHashSet(users);
                                    users.clear();
                                    users.addAll(setItems);

                                    long seed = System.nanoTime();
                                    Collections.shuffle(users, new Random(seed));
                                    game.put("playerList", users);
                                    game.saveInBackground();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    HomeFragment home = new HomeFragment();
                                    fragmentTransaction.replace(R.id.container, home, "home");
                                    fragmentTransaction.commit();

                                }
                            } else {
                                // Something went wrong.
                            }
                        }
                    });
                }
            }
        }
        else {
            game.saveInBackground();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            HomeFragment home = new HomeFragment();
            fragmentTransaction.replace(R.id.container, home, "home");
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

    //Calls gameCreationFragment
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
        ((RadioButton) findViewById(R.id.threeDays)).setChecked(false);
        ((RadioButton) findViewById(R.id.week)).setChecked(false);
        ((RadioButton) findViewById(R.id.never)).setChecked(false);
    }

    //Sets reshuffle time to three days
    public void threeDaysOn(View view) {
        threeDays = true;
        day = false;
        week = false;
        ((RadioButton) findViewById(R.id.day)).setChecked(false);
        ((RadioButton) findViewById(R.id.week)).setChecked(false);
        ((RadioButton) findViewById(R.id.never)).setChecked(false);
    }

    //Sets reshuffle time to 1 week
    public void weekOn(View view) {
        week = true;
        day = false;
        threeDays = false;
        ((RadioButton) findViewById(R.id.threeDays)).setChecked(false);
        ((RadioButton) findViewById(R.id.day)).setChecked(false);
        ((RadioButton) findViewById(R.id.never)).setChecked(false);
    }

    //Sets reshuffle time to never
    public void never(View view) {
        week = false;
        day = false;
        threeDays = false;
        ((RadioButton) findViewById(R.id.threeDays)).setChecked(false);
        ((RadioButton) findViewById(R.id.week)).setChecked(false);
        ((RadioButton) findViewById(R.id.day)).setChecked(false);
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
