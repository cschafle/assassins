package li.allen.cs160.assassins;



import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class GameCreationFragment extends Fragment {

    View layout;
    Activity main;

    public GameCreationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_game_creation, container, false);

        main = this.getActivity();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("available", true);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
//                  Log.d("users", "number: " + objects.size() + " users");
                    ArrayList<String> userStringArray = new ArrayList<String>();
                    for (int i = 0; i < objects.size(); i++) {
                        ParseUser user = objects.get(i);
                        String username = user.getUsername();
                        userStringArray.add(username);
                    }
                    String[] userStrings = new String[userStringArray.size()];
                    userStrings = userStringArray.toArray(userStrings);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(main,
                            android.R.layout.simple_list_item_1, userStrings);

                    ListView listView = (ListView) layout.findViewById(R.id.playersList);
                    listView.setAdapter(adapter);
                } else {
                    Log.d("users", "Error: " + e.getMessage());
                }
            }
        });

        return layout;

    }


}
