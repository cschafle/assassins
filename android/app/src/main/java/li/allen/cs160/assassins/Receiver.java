package li.allen.cs160.assassins;

import com.parse.ParsePushBroadcastReceiver;
import android.util.Log;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ryanma on 12/9/14.
 */
public class Receiver extends ParsePushBroadcastReceiver {

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("fragment", "status");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}