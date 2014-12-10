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

        ParseQuery<ParseUser> queryKilled = ParseUser.getQuery();
        queryKilled.whereEqualTo("killPending", currentUser.getUsername());
        try {
            ArrayList<ParseUser> users = (ArrayList<ParseUser>) queryKilled.find();
            if (users.size() != 0) {
                layoutMode = "Response";
            }
        }
        catch (ParseException e) {}

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
                        while (objects.size() == 0 ) {
                            try {
                                objects = query.find();
                            }
                            catch (ParseException e1) {}

                        }
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

            ParseQuery<ParseUser> queryKilled = ParseUser.getQuery();
            queryKilled.whereEqualTo("killPending", currentUser.getUsername());
            try {
                ArrayList<ParseUser> users = (ArrayList<ParseUser>) queryKilled.find();
                if (users.size() != 0) {
                    StatusFragment status = new StatusFragment();
                    FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                    fragTransaction.replace(R.id.container,status, "status" );
                    fragTransaction.commit();
                }
            }
            catch (ParseException e) {}
      /* and here comes the "trick" */
            handler.postDelayed(this, 10000);
        }
    };


}
