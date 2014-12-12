package li.allen.cs160.assassins;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class HomeFragment extends Fragment {

    View layout;
    Activity main;
    private Handler handler;
    int gameCount = -1;


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
        home.setText("Welcome " + currentUser.getUsername());

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
                String[] gameStrings;
                if (gameStringArray.size() == 0) {
                    LinearLayout gameList = (LinearLayout) layout.findViewById(R.id.gameList);
                    gameList.removeAllViews();

                    LinearLayout.LayoutParams textParams = (LinearLayout.LayoutParams) layout.findViewById(R.id.gameName).getLayoutParams();
                    TextView textView = new TextView(layout.getContext());
                    textView.setLayoutParams(textParams);
                    textView.setText("No Games Available");
//                    gameStrings = new String[1];
//                    gameCount = 0;
//                    gameStringArray.add("No games available");
//                    gameStrings = gameStringArray.toArray(gameStrings);
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(main,
//                            android.R.layout.simple_list_item_1, gameStrings);
//                    ListView listView = (ListView) layout.findViewById(R.id.gameList);
//                    listView.setAdapter(adapter);
//                    listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                }
                else if (gameCount != gameStringArray.size()) {
                    gameCount = gameStringArray.size();
                    gameStrings = new String[gameStringArray.size()];
                    gameStrings = gameStringArray.toArray(gameStrings);

                    LinearLayout gameList = (LinearLayout) layout.findViewById(R.id.gameList);
                    //gameList.removeAllViews();

                    LinearLayout.LayoutParams containerParams = (LinearLayout.LayoutParams) layout.findViewById(R.id.gameItem).getLayoutParams();
                    LinearLayout.LayoutParams textParams = (LinearLayout.LayoutParams) layout.findViewById(R.id.gameName).getLayoutParams();
                    LinearLayout.LayoutParams buttonParams = (LinearLayout.LayoutParams) layout.findViewById(R.id.gameButton).getLayoutParams();

                    int i;
                    for (i=0; i<gameCount; i++) {
                        //Log.d("Iteration" + i, "Iteration" + i);
                        if (i == 0) {
                            TextView textView = (TextView) layout.findViewById(R.id.gameName);
                            textView.setText(gameStrings[i]);

                            Button buttonView = (Button) layout.findViewById(R.id.gameButton);
                            buttonView.setTag(gameStrings[i]);
                            continue;
                        }

                        LinearLayout container = new LinearLayout(layout.getContext());
                        container.setLayoutParams(containerParams);

                        TextView textView = new TextView(layout.getContext());
                        textView.setLayoutParams(textParams);
                        textView.setText(gameStrings[i]);
                        container.addView(textView);

                        Button buttonView = new Button(layout.getContext());
                        buttonView.setLayoutParams(buttonParams);
                        buttonView.setText("Join");
                        buttonView.setTextColor(Color.WHITE);
                        buttonView.setBackgroundColor(Color.parseColor("#3F51B5"));
                        buttonView.setTag(gameStrings[i]);

                        buttonView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                
                            }
                        });


                        container.addView(buttonView);

                        gameList.addView(container);
                    }



//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(main,
//                            android.R.layout.simple_list_item_single_choice, gameStrings);
//                    ListView listView = (ListView) layout.findViewById(R.id.gameList);
//                    listView.setAdapter(adapter);
//                    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                }
            } else {
                Log.d("score", "Error: " + e.getMessage());
            }
            }
        });
      /* and here comes the "trick" */
            handler.postDelayed(this, 2000);
        }
    };







}
