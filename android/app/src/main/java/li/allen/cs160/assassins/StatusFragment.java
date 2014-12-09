package li.allen.cs160.assassins;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.util.Log;
import java.util.ArrayList;

import com.parse.ParseUser;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.GetCallback;
import com.parse.ParseException;


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

        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_status, container, false);

        final ParseUser currentUser = ParseUser.getCurrentUser();
        TextView home = (TextView) layout.findViewById(R.id.gameStatus_username);
        home.setText(currentUser.getUsername());

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
