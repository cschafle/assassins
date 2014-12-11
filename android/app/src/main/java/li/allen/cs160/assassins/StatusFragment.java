package li.allen.cs160.assassins;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 *
 */
public class StatusFragment extends Fragment {

    View layout;
    String layoutMode = "Regular";
    private Handler handler;
    FragmentManager fragmentManager;

    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        handler = new Handler();
        handler.postDelayed(runnable, 1000);

        fragmentManager = this.getFragmentManager();

        final ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> queryGame = ParseQuery.getQuery("Game");
        queryGame.whereEqualTo("gameName", currentUser.getString("game"));
        ParseObject currGame = null;
        try {
            ArrayList<ParseObject> games = (ArrayList<ParseObject>) queryGame.find();
            currGame = games.get(0);
        }
        catch (ParseException e) {}
        Log.d("StatusFragment currGame", currGame.getString("gameName"));
        ArrayList<String> killsPending = (ArrayList<String>) currGame.get("killsPending");
        int userIndex;
        if (killsPending != null) {
            userIndex=killsPending.indexOf(currentUser.getUsername());
            if (userIndex > -1) {
                layoutMode = "Response";
            }
        }
        ArrayList<String> players = (ArrayList<String>) currGame.get("playerList");
        if (players.size() == 1) {
            layoutMode = "Win";
        }
//
//        ParseQuery<ParseUser> queryKilled = ParseUser.getQuery();
//        queryKilled.whereEqualTo("killPending", currentUser.getUsername());
//        try {
//            ArrayList<ParseUser> users = (ArrayList<ParseUser>) queryKilled.find();
//            if (users.size() != 0) {
//                layoutMode = "Response";
//            }
//        }
//        catch (ParseException e) {}

        if (layoutMode.equalsIgnoreCase("Response")) {
            layout = inflater.inflate(R.layout.fragment_status2, container, false);
            handler.removeCallbacks(runnable);

            return layout;
        }
        else if (layoutMode.equalsIgnoreCase("Win")) {
            layout = inflater.inflate(R.layout.fragment_status_win, container, false);
            handler.removeCallbacks(runnable);


            return layout;
        }
        else {
            // Inflate the layout for this fragment
            layout = inflater.inflate(R.layout.fragment_status, container, false);

            // Gets current user and sets Textview to user
            TextView home = (TextView) layout.findViewById(R.id.gameStatus_username);
            home.setText(currentUser.getUsername());

            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("user", currentUser.getUsername());
            installation.saveInBackground();

            TextView gameNameText = (TextView) layout.findViewById(R.id.gameStatus_gameName);
            gameNameText.setText(currentUser.getString("game"));

            final TextView targetNameText = (TextView) layout.findViewById(R.id.gameStatus_target);
            targetNameText.setGravity(Gravity.CENTER);


            final ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
            Log.d("gameName Query", currentUser.getString("game"));
            query.whereEqualTo("gameName", currentUser.getString("game"));
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        // object will be your game score
                        ParseObject game;
                        game = objects.get(0);
                        ArrayList<String> players = (ArrayList<String>) game.get("playerList");
                        int index = players.indexOf(currentUser.getUsername());
                        int targetIndex;
                        if (players.size() == (index+1)) {
                            targetIndex = 0;
                        }
                        else {
                            targetIndex = index + 1;
                        }
                        String targetName = players.get(targetIndex);
                        targetNameText.setText(targetName);
                    } else {
                        // something went wrong
                    }
                }
            });

            return layout;


        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            final ParseUser currentUser = ParseUser.getCurrentUser();

            ParseQuery<ParseObject> queryGame = ParseQuery.getQuery("Game");
            queryGame.whereEqualTo("gameName", currentUser.getString("game"));
            ParseObject currGame = null;
            try {
                ArrayList<ParseObject> games = (ArrayList<ParseObject>) queryGame.find();
                try {
                    currGame = games.get(0);
                    ArrayList<String> killsPending = (ArrayList<String>) currGame.get("killsPending");
                    int userIndex;
                    if (killsPending != null) {
                        userIndex=killsPending.indexOf(currentUser.getUsername());
                        if (userIndex > -1) {
                            StatusFragment status = new StatusFragment();
                            FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
                            fragTransaction.replace(R.id.container,status, "status" );
                            fragTransaction.commit();
                        }
                    }
                    ArrayList<String> players = (ArrayList<String>) currGame.get("playerList");
                    if (players.size() == 1) {
                        StatusFragment status = new StatusFragment();
                        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
                        fragTransaction.replace(R.id.container,status, "status" );
                        fragTransaction.commit();
                    }
                }
                catch (IndexOutOfBoundsException e) {}
            }
            catch (ParseException e) {}


      /* and here comes the "trick" */
            handler.postDelayed(this, 5000);
        }
    };


}
