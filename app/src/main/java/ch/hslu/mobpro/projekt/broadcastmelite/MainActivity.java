package ch.hslu.mobpro.projekt.broadcastmelite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;


/**
 * BroadcastMe Main Activity.
 */
public class MainActivity extends AppCompatActivity {

    private final String myFileExtensions = ".txt";
    DownloadTask performBackgroundTask;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private CharSequence title;
    private String[] menuPoints;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private long timeStamp;
    private int requestIntervall;
    private String myMessagesPath;
    private String myBroadcastsPath;
    private EditText etKeyString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);

        initPreferences();

        if (!initFileStrucutre()) {
            Log.e("initFileStructure: ", "Could not create FileStructure");
        }

        myBroadcastsPath = getFilesDir() + "/mybroadcasts/";
        myMessagesPath = getFilesDir() + "/mymessages/";

        performBackgroundTask = new DownloadTask(this);

        title = drawerTitle = getTitle();
        menuPoints = getResources().getStringArray(R.array.menuPoints_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        MainMenu.LoadModel(this);

        String[] ids = new String[MainMenu.Items.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = Integer.toString(i + 1);
        }

        // set up the drawer's list view with items using a custom adapter and click listener
        MenuAdapter adapter = new MenuAdapter(this, ids);
        drawerList.setAdapter(adapter);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                null,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(title);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    //Initialisiert die Filestruktur wenn nicht vorhanden.
    private boolean initFileStrucutre() {
        File myMessagesPath = new File(getFilesDir() + "/mymessages/");
        File myBroadcastsPath = new File(getFilesDir() + "/mybroadcasts/");

        boolean myMessagesPathAvailable;
        boolean myBroadcastsPathAvailable;

        if (!myMessagesPath.exists()) {
            myMessagesPathAvailable = myMessagesPath.mkdirs();
        } else {
            myMessagesPathAvailable = true;
        }

        if (!myBroadcastsPath.exists()) {
            myBroadcastsPathAvailable = myBroadcastsPath.mkdirs();
        } else {
            myBroadcastsPathAvailable = true;
        }
        return (myMessagesPathAvailable && myBroadcastsPathAvailable);
    }


    //Initilisiert die Preferences.
    private void initPreferences() {

        preference = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preference.edit();

        this.timeStamp = preference.getLong("TimeStamp", Calendar.getInstance().getTimeInMillis() / 1000L);
        this.requestIntervall = preference.getInt("RequestIntervall", 0);

        editor.putLong("TimeStamp", Calendar.getInstance().getTimeInMillis());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call supportInvalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_settings).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectItem(int position) {

        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new MyMessagesFragment();
                break;
            case 1:
                fragment = new MyBroadcastFragment();
                break;
            case 2:
                fragment = new SettingsFragment();
                break;


            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();

            // update selected item and title, then close the drawer
            drawerList.setItemChecked(position, true);
            setTitle(menuPoints[position]);
            drawerLayout.closeDrawer(drawerList);

        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getSupportActionBar().setTitle(this.title);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * ActionListener für den Subscribe Broadcast Button. (MyMessages Fragment)
     *
     * @param v View des Buttons
     */
    public void onSubscribeClicked(View v) {
        EditText etKeyString = (EditText) findViewById(R.id.etKeyString);
        String key = etKeyString.getText().toString();
        String title = "<New Topic>";
        Topics newTopic = new Topics(key, title);
        this.saveTopicsInFiles(newTopic, myMessagesPath);
        selectItem(0);
    }

    /**
     * ActionListener für den NewBroadcast Button. (MyBroadcasts Fragment)
     *
     * @param v View des Buttons.
     */
    public void onNewBroadcastClicked(View v) {
        //Generate TopicKey
        String key = Long.toString(new Random().nextLong(), 32);
        String title = "<New Broadcast>";
        Topics newBroadcast = new Topics(key, title);
        this.saveTopicsInFiles(newBroadcast, myBroadcastsPath);
        selectItem(1);
    }

    /**
     * Speichert ein Topics-Objekt persistent im mybroadcast Pfad.
     *
     * @param broadcast Topics-Objekt zum speichern.
     */
    private void saveTopicsInFiles(Topics broadcast, String path) {
        Gson gson = new Gson();
        String json = gson.toJson(broadcast);

        String text = json;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        String filename = broadcast.getIdentifier() + myFileExtensions;
        File outfile = new File(file, filename);

        FileWriter fileWriter;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(outfile);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(text);
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException ex) {
            Log.e("Persistenz", "Error beim schreiben");
            System.out.println(ex.toString());
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException ioex) {
                Log.e("saveTopicsInFiles", "Could not close BufferedWriter");
            }
        }
    }

    // The click listener for ListView in the navigation drawer
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
