package li.allen.cs160.assassins;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.content.Intent;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.LinkedHashSet;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.FindCallback;
import com.parse.GetCallback;

import com.qualcomm.toq.smartwatch.api.v1.deckofcards.Constants;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.Card;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.ListCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.NotificationTextCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.SimpleTextCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.DeckOfCardsManager;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteDeckOfCards;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteDeckOfCardsException;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteResourceStore;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteToqNotification;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.resource.CardImage;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.resource.DeckOfCardsLauncherIcon;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.util.ParcelableUtil;


public class MainActivity extends Activity {

    FragmentManager fragmentManager;
    boolean individual; boolean team; boolean day; boolean threeDays; boolean week;

    //Toq Stuff
    private final static String PREFS_FILE= "prefs_file";
    private final static String DECK_OF_CARDS_KEY= "deck_of_cards_key";
    private final static String DECK_OF_CARDS_VERSION_KEY= "deck_of_cards_version_key";

    private DeckOfCardsManager mDeckOfCardsManager;
    private RemoteDeckOfCards mRemoteDeckOfCards;
    private RemoteResourceStore mRemoteResourceStore;
    private CardImage[] mCardImages;
    private ToqBroadcastReceiver toqReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toq stuff
        mDeckOfCardsManager = DeckOfCardsManager.getInstance(getApplicationContext());
        toqReceiver = new ToqBroadcastReceiver();
        init();

        //Android Phone
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Get intent extra to see if activity was launched from notification
        Intent i = getIntent();
        String extra = i.getStringExtra("fragment");

        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ParseUser currentUser = ParseUser.getCurrentUser();

        //Sends user to status page to deal with kill confirm/deny
        if (extra != null && extra.equalsIgnoreCase("status")){
            StatusFragment status = new StatusFragment();
            fragmentTransaction.replace(R.id.container, status, "status");
            fragmentTransaction.commit();
        }
        //Sends user to player lobby if logged in
        else if (currentUser != null) {
            HomeFragment home = new HomeFragment();
            fragmentTransaction.replace(R.id.container, home, "home");
            fragmentTransaction.commit();

        }
        //Sends user to welcome page to sign up or log in
        else {
            //Welcome Fragment
            WelcomeFragment welcome = new WelcomeFragment();
            fragmentTransaction.replace(R.id.container, welcome, "welcome");
            fragmentTransaction.commit();
        }
    }

    //Toq stuff
    // Create some cards with example content
    private RemoteDeckOfCards createDeckOfCards(){

        ListCard listCard= new ListCard();
        SimpleTextCard simpleTextCard= new SimpleTextCard("card0");
        listCard.add(simpleTextCard);

        simpleTextCard = new SimpleTextCard("card1");
        listCard.add(simpleTextCard);
        simpleTextCard = new SimpleTextCard("card2");
        listCard.add(simpleTextCard);
        simpleTextCard = new SimpleTextCard("card3");
        listCard.add(simpleTextCard);
        simpleTextCard = new SimpleTextCard("card4");
        listCard.add(simpleTextCard);
        simpleTextCard = new SimpleTextCard("card5");
        listCard.add(simpleTextCard);
        simpleTextCard = new SimpleTextCard("card6");
        listCard.add(simpleTextCard);
        return new RemoteDeckOfCards(this, listCard);
    }

    // Read an image from assets and return as a bitmap
    private Bitmap getBitmap(String fileName) throws Exception{

        try{
            InputStream is= getAssets().open(fileName);
            return BitmapFactory.decodeStream(is);
        }
        catch (Exception e){
            throw new Exception("An error occurred getting the bitmap: " + fileName, e);
        }
    }

//    private void setupUI() {
//
//
//        findViewById(R.id.install_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                install();
//            }
//        });
//
//        findViewById(R.id.uninstall_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uninstall();
//            }
//        });
//
//        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ToqActivity.this,MainActivity.class);
//                ToqActivity.this.startActivity(intent);
//            }
//        });
//
//
//    }

    private void sendNotification() {
        String[] message0 = new String[2];
        String[] people = new String[6];
        people[1] = "Art Goldberg";
        people[2] = "Jack Weinberg";
        people[3] = "Jackie Goldberg";
        people[4] = "Joan Baez";
        people[5] = "Mario Savio";
        people[0] = "Michael Rossman";
        Random rand = new Random();

        message0[0] = "Please draw:";
        message0[1] = people[rand.nextInt(6)];
        // Create a NotificationTextCard

        NotificationTextCard notificationCard = new NotificationTextCard(System.currentTimeMillis(),
                "Free Speech Movement!", message0);

        // Draw divider between lines of text
        notificationCard.setShowDivider(true);
        // Vibrate to alert user when showing the notification
        notificationCard.setVibeAlert(true);
        // Create a notification with the NotificationTextCard we made
        RemoteToqNotification notification = new RemoteToqNotification(this, notificationCard);

        try {
            // Send the notification
            mDeckOfCardsManager.sendNotification(notification);
            Toast.makeText(this, "Sent Notification", Toast.LENGTH_SHORT).show();
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to send Notification", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Installs applet to Toq watch if app is not yet installed
     */
    private void install() {
        boolean isInstalled = true;
        updateDeckOfCardsFromUI();

        try {
            isInstalled = mDeckOfCardsManager.isInstalled();
        }
        catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: Can't determine if app is installed", Toast.LENGTH_SHORT).show();
        }

        if (!isInstalled) {
            try {
                mDeckOfCardsManager.installDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
            } catch (RemoteDeckOfCardsException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: Cannot install application", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "App is already installed!", Toast.LENGTH_SHORT).show();
        }


        try{
            storeDeckOfCards();
        }
        catch (Exception e){
            e.printStackTrace();
        }



    }

    private void uninstall() {
        boolean isInstalled = true;

        try {
            isInstalled = mDeckOfCardsManager.isInstalled();
        }
        catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: Can't determine if app is installed", Toast.LENGTH_SHORT).show();
        }

        if (isInstalled) {
            try{
                mDeckOfCardsManager.uninstallDeckOfCards();
            }
            catch (RemoteDeckOfCardsException e){
                Toast.makeText(this, getString(R.string.error_uninstalling_deck_of_cards), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.already_uninstalled), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Adds a deck of cards to the applet
     */
    private void addSimpleImageCard(Bitmap bitmap) {
        ListCard listCard= mRemoteDeckOfCards.getListCard();

        // Create a SimpleTextCard with 1 + the current number of SimpleTextCards
        SimpleTextCard simpleTextCard= (SimpleTextCard)listCard.childAtIndex(6);
        simpleTextCard.setHeaderText("FSM Art");
//        mCardImages[6] = new CardImage("card.image.7", bitmap);
//        mRemoteResourceStore.addResource(mCardImages[6]);
//        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[6]);

        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider((true));

        try {
            mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to Create SimpleTextCard", Toast.LENGTH_SHORT).show();
        }
    }

    private RemoteDeckOfCards getStoredDeckOfCards() throws Exception{

        if (!isValidDeckOfCards()){
            Log.w(Constants.TAG, "Stored deck of cards not valid for this version of the demo, recreating...");
            return null;
        }

        SharedPreferences prefs= getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        String deckOfCardsStr= prefs.getString(DECK_OF_CARDS_KEY, null);

        if (deckOfCardsStr == null){
            return null;
        }
        else{
            return ParcelableUtil.unmarshall(deckOfCardsStr, RemoteDeckOfCards.CREATOR);
        }

    }

    // Check if the stored deck of cards is valid for this version of the demo
    private boolean isValidDeckOfCards(){

        SharedPreferences prefs= getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        // Return 0 if DECK_OF_CARDS_VERSION_KEY isn't found
        int deckOfCardsVersion= prefs.getInt(DECK_OF_CARDS_VERSION_KEY, 0);

        return deckOfCardsVersion >= Constants.VERSION_CODE;
    }



    private void removeDeckOfCards() {
        ListCard listCard = mRemoteDeckOfCards.getListCard();
        if (listCard.size() == 0) {
            return;
        }

        listCard.remove(0);

        try {
            mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards);
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to delete Card from ListCard", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Uses SharedPreferences to store the deck of cards
     * This is mainly used to
     */
    private void storeDeckOfCards() throws Exception{
        // Retrieve and hold the contents of PREFS_FILE, or create one when you retrieve an editor (SharedPreferences.edit())
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        // Create new editor with preferences above
        SharedPreferences.Editor editor = prefs.edit();
        // Store an encoded string of the deck of cards with key DECK_OF_CARDS_KEY
        editor.putString(DECK_OF_CARDS_KEY, ParcelableUtil.marshall(mRemoteDeckOfCards));
        // Store the version code with key DECK_OF_CARDS_VERSION_KEY
        editor.putInt(DECK_OF_CARDS_VERSION_KEY, Constants.VERSION_CODE);
        // Commit these changes
        editor.commit();
    }

    //    Initialise
    private void init(){

        // Create the resource store for icons and images
        mRemoteResourceStore= new RemoteResourceStore();


//        mCardImages = new CardImage[7];
//        try{
//            mCardImages[0]= new CardImage("card.image.1", getBitmap("art_goldberg_toq.png"));
//            mCardImages[1]= new CardImage("card.image.2", getBitmap("jack_weinberg_toq.png"));
//            mCardImages[2]= new CardImage("card.image.3", getBitmap("jackie_goldberg_toq.png"));
//            mCardImages[3]= new CardImage("card.image.4", getBitmap("joan_baez_toq.png"));
//            mCardImages[4]= new CardImage("card.image.5", getBitmap("mario_savio_toq.png"));
//            mCardImages[5]= new CardImage("card.image.6", getBitmap("michael_rossman_toq.png"));
//
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            System.out.println("Can't get picture icon");
//            return;
//        }

        // Try to retrieve a stored deck of cards
        try {
            // If there is no stored deck of cards or it is unusable, then create new and store
            if ((mRemoteDeckOfCards = getStoredDeckOfCards()) == null){
                mRemoteDeckOfCards = createDeckOfCards();
                storeDeckOfCards();
            }
        }
        catch (Throwable th){
            th.printStackTrace();
            mRemoteDeckOfCards = null; // Reset to force recreate
        }

        // Make sure in usable state
        if (mRemoteDeckOfCards == null){
            mRemoteDeckOfCards = createDeckOfCards();
        }

//        // Re-populate the resource store with any card images being used by any of the cards
//        for (Iterator<Card> it= mRemoteDeckOfCards.getListCard().iterator(); it.hasNext();){
//
//            String cardImageId= ((SimpleTextCard)it.next()).getCardImageId();
//
//            if ((cardImageId != null) && !mRemoteResourceStore.containsId(cardImageId)){
//
//                if (cardImageId.equals("card.image.1")){
//                    mRemoteResourceStore.addResource(mCardImages[0]);
//                }
//                if (cardImageId.equals("card.image.2")){
//                    mRemoteResourceStore.addResource(mCardImages[1]);
//                }
//                if (cardImageId.equals("card.image.3")){
//                    mRemoteResourceStore.addResource(mCardImages[2]);
//                }
//                if (cardImageId.equals("card.image.4")){
//                    mRemoteResourceStore.addResource(mCardImages[3]);
//                }
//                if (cardImageId.equals("card.image.5")){
//                    mRemoteResourceStore.addResource(mCardImages[4]);
//                }
//                if (cardImageId.equals("card.image.6")){
//                    mRemoteResourceStore.addResource(mCardImages[5]);
//                }
//
//            }
//        }
    }

    private void updateDeckOfCardsFromUI() {
        if (mRemoteDeckOfCards == null) {
            mRemoteDeckOfCards = createDeckOfCards();
        }
        ListCard listCard= mRemoteDeckOfCards.getListCard();
        int currSize = listCard.size();
        // Card #1
        SimpleTextCard simpleTextCard1= (SimpleTextCard)listCard.childAtIndex(0);
        simpleTextCard1.setHeaderText("Art Goldberg");
        simpleTextCard1.setTitleText("Draw: Now");
//        simpleTextCard1.setCardImage(mRemoteResourceStore, mCardImages[0]);
        simpleTextCard1.setReceivingEvents(true);
        simpleTextCard1.setShowDivider(true);

        SimpleTextCard simpleTextCard2= (SimpleTextCard)listCard.childAtIndex(1);
        simpleTextCard2.setHeaderText("Jack Weinberg");
        simpleTextCard2.setTitleText("Draw: FSM");
//        simpleTextCard2.setCardImage(mRemoteResourceStore, mCardImages[1]);
        simpleTextCard2.setReceivingEvents(true);
        simpleTextCard2.setShowDivider(true);

        SimpleTextCard simpleTextCard3= (SimpleTextCard)listCard.childAtIndex(2);
        simpleTextCard3.setHeaderText("Jackie Goldberg");
        simpleTextCard3.setTitleText("Draw: SLATE");
//        simpleTextCard3.setCardImage(mRemoteResourceStore, mCardImages[2]);
        simpleTextCard3.setReceivingEvents(true);
        simpleTextCard3.setShowDivider(true);

        SimpleTextCard simpleTextCard4= (SimpleTextCard)listCard.childAtIndex(3);
        simpleTextCard4.setHeaderText("Joan Baez");
        simpleTextCard4.setTitleText("Draw: A megaphone");
//        simpleTextCard4.setCardImage(mRemoteResourceStore, mCardImages[3]);
        simpleTextCard4.setReceivingEvents(true);
        simpleTextCard4.setShowDivider(true);

        SimpleTextCard simpleTextCard5= (SimpleTextCard)listCard.childAtIndex(4);
        simpleTextCard5.setHeaderText("Mario Savio");
        simpleTextCard5.setTitleText("Draw: Your view of Free Speech");
//        simpleTextCard5.setCardImage(mRemoteResourceStore, mCardImages[4]);
        simpleTextCard5.setReceivingEvents(true);
        simpleTextCard5.setShowDivider(true);

        SimpleTextCard simpleTextCard6= (SimpleTextCard)listCard.childAtIndex(5);
        simpleTextCard6.setHeaderText("Michael Rossman");
        simpleTextCard6.setTitleText("Draw: 'Free Speech'");
//        simpleTextCard6.setCardImage(mRemoteResourceStore, mCardImages[5]);
        simpleTextCard6.setReceivingEvents(true);
        simpleTextCard6.setShowDivider(true);


    }

    /**
     * @see android.app.Activity#onStart()
     */
    protected void onStart(){
        super.onStart();


        // If not connected, try to connect
        if (!mDeckOfCardsManager.isConnected()){
            try{
                mDeckOfCardsManager.connect();
            }
            catch (RemoteDeckOfCardsException e){
                e.printStackTrace();
            }
        }
        mDeckOfCardsManager.addDeckOfCardsEventListener(new DeckOfCardsEventListener() {
            @Override
            public void onCardOpen(String s) {

            }

            @Override
            public void onCardVisible(String s) {

            }

            @Override
            public void onCardInvisible(String s) {

            }

            @Override
            public void onCardClosed(String s) {

            }

            @Override
            public void onMenuOptionSelected(String s, String s2) {

            }

            @Override
            public void onMenuOptionSelected(String s, String s2, String s3) {

            }
        });
    }

    //Install Toq Application
    public void installToq(View view) {
        install();
    }

    //Android Phone Stuff
    //Deny death after receiving push notification
    public void denyDeath(View view) {
        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        queryUser.whereEqualTo("killPending", currentUser.getUsername());
        try {
            ArrayList<ParseUser> killerList = (ArrayList<ParseUser>) queryUser.find();
            ParseUser killer = killerList.get(0);
            killer.put("killPending", "" );
            killer.save();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            StatusFragment status = new StatusFragment();
            fragmentTransaction.replace(R.id.container, status, "status");
            fragmentTransaction.addToBackStack("status");
            fragmentTransaction.commit();

        }
        catch (ParseException e) {}
    }

    //Confirms death after receiving push notification
    public void confirmDeath(View view) {
        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> queryGame = ParseQuery.getQuery("Game");
        queryGame.whereEqualTo("gameName", currentUser.getString("game"));
        ParseObject currGame = null;
        try {
            ArrayList<ParseObject> games = (ArrayList<ParseObject>) queryGame.find();
            currGame = games.get(0);
        }
        catch (ParseException e) {}

        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        queryUser.whereEqualTo("killPending", currentUser.getUsername());
        try {
            ArrayList<ParseUser> killerList = (ArrayList<ParseUser>) queryUser.find();
            ParseUser killer = killerList.get(0);
            int killerKills = killer.getInt("kills");
            killer.put("kills", killerKills+1);
            killer.put("killPending", "");
            killer.saveInBackground();

            currentUser.put("available", true);
            currentUser.put("game", "");
            currentUser.saveInBackground();

            ArrayList<String> players = (ArrayList<String>) currGame.get("playerList");
            Log.d("confirmDeath", players.toString());
            players.remove(currentUser.getUsername());
            currGame.put("playerList", players);
            currGame.saveInBackground();

        }
        catch (ParseException e) {}

    }

    //Kill function sends notification and if accepted then alters playerList
    //NEED TO CHANGE SOMETHING IN THIS AFTER TESTING PHASE IS OVER
    public void killTarget(View view) {
        ParseUser currentUser = ParseUser.getCurrentUser();

        TextView targetNameText = (TextView) findViewById(R.id.gameStatus_target);

        currentUser.put("killPending", targetNameText.getText().toString());
        currentUser.saveInBackground();

        ParseQuery pushQuery = ParseInstallation.getQuery();
        //REMEMBER TO CHANGE THE TARGET TO TARGETNAMETEXT!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        pushQuery.whereEqualTo("user", "MasonIII");

        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setMessage("You have been assassinated!");
        push.sendInBackground();

    }

    //Goes to game user is currently in if any
    public void goToGame(View view) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        try {
            currentUser.fetch();
        }
        catch (ParseException e) {

        }
        // Disables status button (does nothing)
        String currentGame = currentUser.getString("game");

        if (currentGame == null || currentGame.equalsIgnoreCase("")) {
            Log.d("goToGame->currentGame", "No Game");
        }
        else {

//            Log.d("goToGame->currentGame", currentGame.toString());
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            StatusFragment status = new StatusFragment();
            fragmentTransaction.replace(R.id.container, status, "status");
            fragmentTransaction.addToBackStack("status");
            fragmentTransaction.commit();
        }
    }

    //Join game from games where user has been invited
    public void joinGame(View view) {

        final ListView listView = (ListView) findViewById(R.id.gameList);

        int id = listView.getCheckedItemPosition();
        final String gameName = (String) listView.getItemAtPosition(id);

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", currentUser.getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    ParseUser user = users.get(0);
                    if ((Boolean) user.get("available")) {
                        user.put("available", false);
                        user.put("game", gameName);
                        user.put("kills", 0);
                        try {
                            user.save();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            StatusFragment status = new StatusFragment();
                            fragmentTransaction.replace(R.id.container, status, "status");
                            fragmentTransaction.addToBackStack("status");
                            fragmentTransaction.commit();
                        }
                        catch (ParseException e2) {}


                    }
                } else {
                    // Something went wrong.
                }
            }
        });
    }

    //Creates game as a parseObject
    public void create(View view) {

        EditText gameName = (EditText) findViewById(R.id.gameName);
        String sGameName = gameName.getText().toString();

        final ParseUser currentUser = ParseUser.getCurrentUser();
        final String creator = currentUser.getUsername();

        final ParseObject game = new ParseObject("Game");
        game.put("gameName", sGameName);
        if (individual) {
            game.put("gameMode", "individual");
        }
        else {
            game.put("gameMode", "team");
        }
        if (day) {
            game.put("reshuffle", "day");
        }
        else if (threeDays) {
            game.put("reshuffle", "threeDays");
        }
        else {
            game.put("reshuffle", "week");
        }
        game.put("advanced", "off");
        game.put("creator", creator);

        ListView listView = (ListView) findViewById(R.id.playersList);



        SparseBooleanArray checked = listView.getCheckedItemPositions();
        final ArrayList<String> users = new ArrayList<String>();
        int count = 0;
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            if (checked.get(i)) {
                // Do something
                count ++;
            }
        }
        final int countFinal = count;
        if (countFinal > 0) {
            for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                if (checked.get(i)) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", listView.getItemAtPosition(i));
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                users.add(objects.get(0).getUsername());
                                if (users.size() == countFinal) {

                                    users.add(creator);

                                    Set setItems = new LinkedHashSet(users);
                                    users.clear();
                                    users.addAll(setItems);

                                    long seed = System.nanoTime();
                                    Collections.shuffle(users, new Random(seed));
                                    game.put("playerList", users);
                                    game.saveInBackground();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    HomeFragment home = new HomeFragment();
                                    fragmentTransaction.replace(R.id.container, home, "home");
                                    fragmentTransaction.commit();

                                }
                            } else {
                                // Something went wrong.
                            }
                        }
                    });
                }
            }
        }
        else {
            game.saveInBackground();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            HomeFragment home = new HomeFragment();
            fragmentTransaction.replace(R.id.container, home, "home");
            fragmentTransaction.commit();
        }


    }

    //Logs user out and returns to welcome Fragment
    public void logout(View view) {
        ParseUser.logOut();
        WelcomeFragment welcome = new WelcomeFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, welcome, "welcome");
        fragmentTransaction.commit();

    }

    //Calls gameCreationFragment
    public void createGame(View view) {
        GameCreationFragment createGame = new GameCreationFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, createGame, "createGame");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //Sets individual true and team false
    public void individualOn(View view) {
        individual = true;
        team = false;
    }

    //Sets team true and individual false
    public void teamOn(View view) {
        team = true;
        individual = false;
    }

    //Sets reshuffle time to 1 day
    public void dayOn(View view) {
        day = true;
        threeDays = false;
        week = false;
        ((RadioButton) findViewById(R.id.threeDays)).setChecked(false);
        ((RadioButton) findViewById(R.id.week)).setChecked(false);
        ((RadioButton) findViewById(R.id.never)).setChecked(false);
    }

    //Sets reshuffle time to three days
    public void threeDaysOn(View view) {
        threeDays = true;
        day = false;
        week = false;
        ((RadioButton) findViewById(R.id.day)).setChecked(false);
        ((RadioButton) findViewById(R.id.week)).setChecked(false);
        ((RadioButton) findViewById(R.id.never)).setChecked(false);
    }

    //Sets reshuffle time to 1 week
    public void weekOn(View view) {
        week = true;
        day = false;
        threeDays = false;
        ((RadioButton) findViewById(R.id.threeDays)).setChecked(false);
        ((RadioButton) findViewById(R.id.day)).setChecked(false);
        ((RadioButton) findViewById(R.id.never)).setChecked(false);
    }

    //Sets reshuffle time to never
    public void never(View view) {
        week = false;
        day = false;
        threeDays = false;
        ((RadioButton) findViewById(R.id.threeDays)).setChecked(false);
        ((RadioButton) findViewById(R.id.week)).setChecked(false);
        ((RadioButton) findViewById(R.id.day)).setChecked(false);
    }

    //Calls LoginFragment login method
    public void signUp(View view) {
        LoginFragment fragment = (LoginFragment) fragmentManager.findFragmentByTag("login");
        fragment.signUp(view);
    }

    //Replaces screen to log in screen
    public void loginFragment(View view) {
        SignInFragment login = new SignInFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, login, "signIn");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //Calls signInFragment login method
    public void login(View view) {
        SignInFragment fragment = (SignInFragment) fragmentManager.findFragmentByTag("signIn");
        fragment.login(view);
    }

    //Redirects to sign up fragment
    public void toSignUp(View view) {
        // SignUp Fragment
        LoginFragment login = new LoginFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, login, "login");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
