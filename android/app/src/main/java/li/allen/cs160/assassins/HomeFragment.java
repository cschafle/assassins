package li.allen.cs160.assassins;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.FindCallback;
import java.util.List;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.util.Log;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import android.app.Activity;
import android.widget.ListView;
import android.widget.Button;
import android.os.Handler;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class HomeFragment extends Fragment {

    View layout;
    Activity main;
    private Handler handler;
    int gameCount;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_home, container, false);

        main = this.getActivity();

        handler = new Handler();
        handler.postDelayed(runnable, 1);

        return layout;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
        /* do what you need to do */
        final ParseUser currentUser = ParseUser.getCurrentUser();
        TextView home = (TextView) layout.findViewById(R.id.home);
        home.setText(currentUser.getUsername());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.whereEqualTo("started", false);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
            if (e == null) {
                ArrayList<String> gameStringArray = new ArrayList<String>();
                for (int i = 0; i < objects.size(); i++) {
                    ParseObject game = objects.get(i);
                    ArrayList<String> users = (ArrayList<String>) game.get("playerList");
                    if (users != null && users.size() > 0) {
                        if (users.contains(currentUser.getUsername())) {
                            String gameName = game.get("gameName").toString();
                            gameStringArray.add(gameName);
                        }
                    }
                }
                if (gameStringArray.size() == 0) {
                    gameStringArray.add("No Games Available");
                }
                String[] gameStrings = new String[gameStringArray.size()];
                if (gameCount != gameStringArray.size()) {
                    gameCount = gameStringArray.size();
                    gameStrings = gameStringArray.toArray(gameStrings);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(main,
                            android.R.layout.simple_list_item_single_choice, gameStrings);
                    ListView listView = (ListView) layout.findViewById(R.id.gameList);
                    listView.setAdapter(adapter);
                }
            } else {
                Log.d("score", "Error: " + e.getMessage());
            }
            }
        });
      /* and here comes the "trick" */
            handler.postDelayed(this, 5000);
        }
    };







}
