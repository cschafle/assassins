package li.allen.cs160.assassins;



import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.ParseException;

import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class LoginFragment extends Fragment {

    View layout;
    Activity parentActivity;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_login, container, false);
        return layout;
    }

    public void signUp(View view) {
        ParseUser user = new ParseUser();

        EditText usernameEditText = (EditText) layout.findViewById(R.id.username);
        String sUsername = usernameEditText.getText().toString();
        if (sUsername.matches("")) {
            Toast.makeText(this.getActivity(), "You did not enter a username", Toast.LENGTH_SHORT).show();
//            return;
        }

        EditText passwordEditText = (EditText) layout.findViewById(R.id.password);
        String sPassword = passwordEditText.getText().toString();
        if (sPassword.matches("")) {
            Toast.makeText(this.getActivity(), "You did not enter a password", Toast.LENGTH_SHORT).show();
//            return;
        }

        EditText emailEditText = (EditText) layout.findViewById(R.id.email);
        String sEmail = emailEditText.getText().toString();
        if (sEmail.matches("")) {
            Toast.makeText(this.getActivity(), "You did not enter an email", Toast.LENGTH_SHORT).show();
//            return;
        }

        EditText firstNameEditText = (EditText) layout.findViewById(R.id.firstName);
        String sFirstName = firstNameEditText.getText().toString();
        if (sFirstName.matches("")) {
            Toast.makeText(this.getActivity(), "You did not enter a first name", Toast.LENGTH_SHORT).show();
//            return;
        }

        EditText lastNameEditText = (EditText) layout.findViewById(R.id.lastName);
        String sLastName = lastNameEditText.getText().toString();
        if (sLastName.matches("")) {
            Toast.makeText(this.getActivity(), "You did not enter a last name", Toast.LENGTH_SHORT).show();
//            return;
        }

//        Toast.makeText(this.getActivity(), sUsername + ", " + sEmail + ", " + sFirstName + " " + sLastName, Toast.LENGTH_SHORT).show();


        user.setUsername(sUsername);
        user.setPassword(sPassword);
        user.setEmail(sEmail);

// other fields can be set just like with ParseObject
        user.put("First",sFirstName);
        user.put("Last", sLastName);
        user.put("available", true);
        user.put("kills", 0);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "Account Created", Toast.LENGTH_SHORT).show();

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    HomeFragment home = new HomeFragment();
                    fragmentTransaction.replace(R.id.container, home, "home");
                    fragmentTransaction.commit();

                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.d("Login", "signUp()", e);
                }
            }
        });
    }


}
