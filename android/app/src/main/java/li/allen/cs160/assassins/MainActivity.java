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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.content.Intent;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.LinkedHashSet;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.FindCallback;
import com.parse.GetCallback;



public class MainActivity extends Activity {

    FragmentManager fragmentManager;
    boolean individual; boolean team; boolean day; boolean threeDays; boolean week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent i = getIntent();
        String extra = i.getStringExtra("fragment");

        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (extra != null && extra.equalsIgnoreCase("status")){
            StatusFragment status = new StatusFragment();
            fragmentTransaction.replace(R.id.container, status, "status");
            fragmentTransaction.commit();
        }
        else if (currentUser != null) {
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

    //Deny death after receiving push notification
    public void denyDeath(View view) {
        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        queryUser.whereEqualTo("killPending", currentUser.getUsername());
        try {
            ArrayList<ParseUser> killerList = (ArrayList<ParseUser>) queryUser.find();
            ParseUser killer = killerList.get(0);
            killer.put("killPending", "" );
            killer.save();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            StatusFragment status = new StatusFragment();
            fragmentTransaction.replace(R.id.container, status, "status");
            fragmentTransaction.addToBackStack("status");
            fragmentTransaction.commit();

        }
        catch (ParseException e) {}
    }

    //Confirms death after receiving push notification
    public void confirmDeath(View view) {
        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> queryGame = ParseQuery.getQuery("Game");
        queryGame.whereEqualTo("gameName", currentUser.getString("game"));
        ParseObject currGame = null;
        try {
            ArrayList<ParseObject> games = (ArrayList<ParseObject>) queryGame.find();
            currGame = games.get(0);
        }
        catch (ParseException e) {}

        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        queryUser.whereEqualTo("killPending", currentUser.getUsername());
        try {
            ArrayList<ParseUser> killerList = (ArrayList<ParseUser>) queryUser.find();
            ParseUser killer = killerList.get(0);
            int killerKills = killer.getInt("kills");
            killer.put("kills", killerKills+1);
            killer.put("killPending", "");
            killer.saveInBackground();

            currentUser.put("available", true);
            currentUser.put("game", "");
            currentUser.saveInBackground();

            ArrayList<String> players = (ArrayList<String>) currGame.get("playerList");
            Log.d("confirmDeath", players.toString());
            players.remove(currentUser.getUsername());
            currGame.put("playerList", players);
            currGame.saveInBackground();

        }
        catch (ParseException e) {}

    }

    //Kill function sends notification and if accepted then alters playerList
    //NEED TO CHANGE SOMETHING IN THIS AFTER TESTING PHASE IS OVER
    public void killTarget(View view) {
        ParseUser currentUser = ParseUser.getCurrentUser();

        TextView targetNameText = (TextView) findViewById(R.id.gameStatus_target);

        currentUser.put("killPending", targetNameText.getText().toString());
        currentUser.saveInBackground();

        ParseQuery pushQuery = ParseInstallation.getQuery();
        //REMEMBER TO CHANGE THE TARGET TO TARGETNAMETEXT!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        pushQuery.whereEqualTo("user", "MasonIII");

        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setMessage("You have been assassinated!");
        push.sendInBackground();

    }

    //Goes to game user is currently in if any
    public void goToGame(View view) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        try {
            currentUser.fetch();
        }
        catch (ParseException e) {

        }
        // Disables status button (does nothing)
        String currentGame = currentUser.getString("game");

        if (currentGame == null || currentGame.equalsIgnoreCase("")) {
            Log.d("goToGame->currentGame", "No Game");
        }
        else {

//            Log.d("goToGame->currentGame", currentGame.toString());
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            StatusFragment status = new StatusFragment();
            fragmentTransaction.replace(R.id.container, status, "status");
            fragmentTransaction.addToBackStack("status");
            fragmentTransaction.commit();
        }
    }

    //Join game from games where user has been invited
    public void joinGame(View view) {

        final ListView listView = (ListView) findViewById(R.id.gameList);

        int id = listView.getCheckedItemPosition();
        final String gameName = (String) listView.getItemAtPosition(id);

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
                        user.put("kills", 0);
                        try {
                            user.save();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            StatusFragment status = new StatusFragment();
                            fragmentTransaction.replace(R.id.container, status, "status");
                            fragmentTransaction.addToBackStack("status");
                            fragmentTransaction.commit();
                        }
                        catch (ParseException e2) {}


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
