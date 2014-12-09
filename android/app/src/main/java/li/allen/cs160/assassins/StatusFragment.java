package li.allen.cs160.assassins;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String layoutMode = "Regular";

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

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
            Log.d("gameName Query", currentUser.getString("game"));
            query.whereEqualTo("gameName", currentUser.getString("game"));
            ArrayList<ParseObject> gameResult;
            try {
                gameResult = (ArrayList<ParseObject>) query.find();
                ParseObject game = gameResult.get(0);
                Log.d("gameResult", gameResult.toString());
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
            }
            catch (ParseException e) {
                Log.d("gameResult Error", e.toString());
            }

            return layout;
        }

    }


}
