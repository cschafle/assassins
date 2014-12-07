package com.example.user.assassins;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by User on 11/25/2014.
 */
public class PlayerHomeFrag extends Fragment implements View.OnClickListener {

    Button confirmKill;
    Context context;
    String target;
    Boolean condition = true;
    TextView waiting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.player_home_frag, container, false);
        confirmKill = (Button) rootView.findViewById(R.id.report_kill);
        confirmKill.setOnClickListener(this);
        waiting = (TextView) rootView.findViewById(R.id.waiting);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        //Allen's mock up showed the button disappearing..
        //view.setVisibility(View.INVISIBLE);  this option the button can still be clicked
        //this gets rid of the button
        view.setVisibility(View.GONE);
        waiting.setVisibility(View.VISIBLE);

        target = "Eric";
        CharSequence options[] = new CharSequence[] {"Report Kill", "False Alarm"};
        context = view.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Report Kill")
                .setMessage("We'll ask"+ target + "to confirm that you've killed him");

        if (condition) {
            builder.setMessage("We'll ask "+ target + " to confirm that you've killed him. Although, we're not sure you can do that, since " + target
            + " cannot be killed during lecture");
        }

        final FrameLayout frameView = new FrameLayout(context);
        builder.setView(frameView);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // record wether or not they "killed them" send data out

            }
        });

        final AlertDialog alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.report_kill, frameView);
        alertDialog.show();
    }
}
