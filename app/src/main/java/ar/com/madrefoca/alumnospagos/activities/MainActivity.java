package ar.com.madrefoca.alumnospagos.activities;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Attendee;
import ar.com.madrefoca.alumnospagos.model.Event;
import ar.com.madrefoca.alumnospagos.utils.ExportToExcelUtil;
import ar.com.madrefoca.alumnospagos.utils.JsonUtil;
import ar.com.madrefoca.alumnospagos.utils.ManageFragmentsNavigation;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_OPEN_ITEM = 1;
    private static final int REQUEST_CODE_CREATOR = 2;

    private DatabaseHelper databaseHelper = null;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;

    // flag to load home fragment when user presses back key
    private Handler mHandler;

    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (savedInstanceState == null) {
            setUpNavigationView();
            ManageFragmentsNavigation.navItemTag = ManageFragmentsNavigation.TAG_HOME;
            loadHomeFragment();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        databaseHelper = OpenHelperManager.getHelper(this.getApplicationContext(),DatabaseHelper.class);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(ManageFragmentsNavigation.navItemTag) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = ManageFragmentsNavigation.getHomeFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.navItemTag);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }



    private void setToolbarTitle() {
        getSupportActionBar().setTitle(ManageFragmentsNavigation.navItemTag);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(ManageFragmentsNavigation.navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        ManageFragmentsNavigation.setCurrentTag(ManageFragmentsNavigation.TAG_HOME);
                        break;
                    case R.id.nav_attendees:
                        ManageFragmentsNavigation.setCurrentTag(ManageFragmentsNavigation.TAG_ATTENDEES);
                        break;
                    case R.id.nav_places:
                        ManageFragmentsNavigation.setCurrentTag(ManageFragmentsNavigation.TAG_PLACES);
                        break;
                    case R.id.nav_payments:
                        ManageFragmentsNavigation.setCurrentTag(ManageFragmentsNavigation.TAG_PAYMENTS);
                        break;
                    case R.id.nav_coupons:
                        ManageFragmentsNavigation.setCurrentTag(ManageFragmentsNavigation.TAG_COUPONS);
                        break;
                    case R.id.nav_notification:
                        ManageFragmentsNavigation.setCurrentTag(ManageFragmentsNavigation.TAG_NOTIFICATIONS);
                        break;
                    case R.id.nav_settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_aboutUs:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        // TODO: 02/09/17 completar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: 02/09/17 completar
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    /**
     * Starts the sign-in process and initializes the Drive client.
     */
    @SuppressLint("RestrictedApi")
    public void signIn() {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient();
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .build();
            @SuppressLint("RestrictedApi")
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }

    /**
     * Continues the sign-in process, initializing the Drive clients with the current
     * user's account.
     */
    private void initializeDriveClient() {
        @SuppressLint("RestrictedApi")
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
    }

    /** Create a new file and save it to Drive. */
    public void saveFileToDrive(String type) {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");

        switch (type) {
            case "json":
                mDriveResourceClient
                        .createContents()
                        .continueWithTask(
                                new Continuation<DriveContents, Task<Void>>() {
                                    @Override
                                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                                        return createFileIntentSender(task.getResult(), "json");
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Failed to create new contents.", e);
                                    }
                                });
                break;
            case "xls":
                mDriveResourceClient
                        .createContents()
                        .continueWithTask(
                                new Continuation<DriveContents, Task<Void>>() {
                                    @Override
                                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                                        return createFileIntentSender(task.getResult(), "xls");
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Failed to create new contents.", e);
                                    }
                                });
                break;
        }


    }


    /**
     * Creates an {@link IntentSender} to start a dialog activity with configured {@link
     * CreateFileActivityOptions} for user to create a new photo in Drive.
     */
    private Task<Void> createFileIntentSender(DriveContents driveContents, String type) {
        // Get an output stream for the contents.
        OutputStream outputStream = driveContents.getOutputStream();
        String file = "";
        String mimeType = "";
        String fileTitle = "";
        byte[] fileContent = null;
        switch (type) {
            case "json":
                file = prepareJsonToSave();
                mimeType = "application/json";
                fileTitle = "fullBackup.json";
                fileContent = file.getBytes();
                break;
            case "xls":
                ExportToExcelUtil exportToExcelUtil = new ExportToExcelUtil(getApplicationContext(), databaseHelper);
                fileContent = exportToExcelUtil.generateExcelFile();
                mimeType = "application/vnd.ms-excel";
                fileTitle = "pagosEnExcel.xls";
                break;
        }

        Log.i(TAG, "Json content: " + file);
        try {
            outputStream.write(fileContent);
        } catch (IOException e) {
            Log.e(TAG, "Unable to write file contents.", e);
        }

        // Create the initial metadata - MIME type and title.
        // Note that the user will be able to change the title later.
        MetadataChangeSet metadataChangeSet =
                new MetadataChangeSet.Builder()
                        .setMimeType(mimeType)
                        .setTitle(fileTitle)
                        .build();
        // Set up options to configure and display the create file activity.
        CreateFileActivityOptions createFileActivityOptions =
                new CreateFileActivityOptions.Builder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(driveContents)
                        .build();

        return mDriveClient
                .newCreateFileActivityIntentSender(createFileActivityOptions)
                .continueWith(
                        new Continuation<IntentSender, Void>() {
                            @Override
                            public Void then(@NonNull Task<IntentSender> task) throws Exception {
                                startIntentSenderForResult(task.getResult(), REQUEST_CODE_CREATOR,
                                        null, 0, 0, 0);
                                return null;
                            }
                        });
    }

    private String prepareJsonToSave() {
        return JsonUtil.allTablesToJSon(databaseHelper);
    }
}
