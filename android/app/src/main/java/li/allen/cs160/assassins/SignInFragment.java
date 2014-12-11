package li.allen.cs160.assassins;



import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SignInFragment extends Fragment {

    View layout;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_edit_login, container, false);
        return layout;
    }

    public void login(View view){

        EditText usernameEditText = (EditText) layout.findViewById(R.id.username2);
        String sUsername = usernameEditText.getText().toString();
        if (sUsername.matches("")) {
            Toast.makeText(this.getActivity(), "You did not enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText passwordEditText = (EditText) layout.findViewById(R.id.password2);
        String sPassword = passwordEditText.getText().toString();
        if (sPassword.matches("")) {
            Toast.makeText(this.getActivity(), "You did not enter a password", Toast.LENGTH_SHORT).show();
            return;
        }


        ParseUser.logInInBackground(sUsername, sPassword, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
            if (user != null) {
                // Hooray! The user is logged in.
                Toast.makeText(getActivity(), "Login Accepted", Toast.LENGTH_SHORT).show();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                HomeFragment home = new HomeFragment();
                fragmentTransaction.replace(R.id.container, home, "home");
                fragmentTransaction.commit();
            } else {
                // Signup failed. Look at the ParseException to see what happened.
                Log.d("SignIn", "login()", e);
            }
            }
        });
    }



}
