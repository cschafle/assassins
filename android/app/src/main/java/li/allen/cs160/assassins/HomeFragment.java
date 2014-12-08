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


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class HomeFragment extends Fragment {

    View layout;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_home, container, false);

        ParseUser currentUser = ParseUser.getCurrentUser();
        TextView home = (TextView) layout.findViewById(R.id.home);
        home.setText(currentUser.getUsername());


        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("available", "true");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.d("users", "number: " + scoreList.size() + " users");
                } else {
                    Log.d("users", "Error: " + e.getMessage());
                }
            }
        });



        return layout;
    }





}
