package com.example.user.assassins;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MyActivity extends Activity implements View.OnClickListener{

    FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    public boolean myActivity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        Button joinGame = (Button) findViewById(R.id.joinGame);
        Button hostGame = (Button) findViewById(R.id.hostGame);
        //set onClickListener to listen for join game
        joinGame.setOnClickListener(this);
        hostGame.setOnClickListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        //the if myActivity prevents myActivity buttons from being clicked inside fragments
        if (myActivity) {
            Fragment fragment = null;
            if (view == findViewById(R.id.joinGame)) {
                fragment = new PlayerHomeFrag();
            }
            if (view == findViewById(R.id.hostGame)) {
                fragment = new HostGame();
            }
            if (fragment != null) {
                myActivity = false;
                fragmentTransaction.replace(R.id.action_settings, fragment);
                fragmentTransaction.commit();
            } else {
                // error in creating fragment
                Log.e("MainActivity", "Error in creating fragment");
            }
        }

    }

}
