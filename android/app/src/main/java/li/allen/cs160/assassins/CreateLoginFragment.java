package li.allen.cs160.assassins;

/**
 * Created by User on 12/10/2014.
 */

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.app.DialogFragment;

public class CreateLoginFragment extends DialogFragment implements TextView.OnEditorActionListener {

    public interface EditNameDialogListener {
        void onFinishEditDialog(String inputText);
    }

    private EditText username;
    private EditText password;

    View view;


    public CreateLoginFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_login, container);
        username = (EditText) view.findViewById(R.id.username2);
        password = (EditText) view.findViewById(R.id.username2);
        getDialog().setTitle("Please fill in your information");
        // Show soft keyboard automatically
        username.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        username.setOnEditorActionListener(this);

        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            EditNameDialogListener activity = (EditNameDialogListener) getActivity();
            activity.onFinishEditDialog(username.getText().toString());
            this.dismiss();
            return true;
        }
        return false;
    }


}

