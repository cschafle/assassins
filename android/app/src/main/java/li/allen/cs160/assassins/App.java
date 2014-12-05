package li.allen.cs160.assassins;

/**
 * Created by ryanma on 12/3/14.
 */

import android.app.Application;
import com.parse.Parse;
import com.parse.PushService;


public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "0bMsRllZl4dgRrUPcNOrlT35fNfR8zI9NmS13sqe", "Pia0mwfgSXEvXenldjW9I9124TJbCf7Z222GPFWg");
        PushService.setDefaultPushCallback(this, MainActivity.class);
    }
}