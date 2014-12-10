package li.allen.cs160.assassins;



import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.FragmentTransaction;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.FindCallback;



/**
 * A simple {@link Fragment} subclass.
 *
 */
public class StatusFragment extends Fragment {

    View layout;
    String layoutMode = "Regular";
    private Handler handler;

    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        handler = new Handler();
        handler.postDelayed(runnable, 1000);

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
                            FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                            fragTransaction.replace(R.id.container,status, "status" );
                            fragTransaction.commit();
                        }
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
