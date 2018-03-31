package ar.com.madrefoca.alumnospagos.fragments;


import android.Manifest;
import android.app.Fragment;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.utils.UtilImportContacts;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Nullable
    @BindView(R.id.exportDbButton)
    Button exportDbButton;

    @Nullable
    @BindView(R.id.importContactsButton)
    Button importContactsButton;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Integer count =1;

    private DatabaseHelper databaseHelper = null;

    View thisFragment = null;

    private static final String TAG = "alumnos tango";
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_CREATOR = 2;
    private GoogleSignInClient mGoogleSignInClient;
    private Bitmap mBitmapToSave;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisFragment = inflater.inflate(R.layout.fragment_settings, container, false);

        int hasReadContactsPermission = ActivityCompat.checkSelfPermission(thisFragment.getContext(),
                Manifest.permission.READ_CONTACTS);
        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }

        int hasWritePermission = ActivityCompat.checkSelfPermission(thisFragment.getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }

        ButterKnife.bind(this, thisFragment);

        progressBar.setMax(10);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        this.signIn();

        // Use the last signed in account here since it already have a Drive scope.
        mDriveClient = Drive.getDriveClient(thisFragment.getContext(), GoogleSignIn.getLastSignedInAccount(thisFragment.getContext()));
        // Build a drive resource client.
        mDriveResourceClient =
                Drive.getDriveResourceClient(thisFragment.getContext(), GoogleSignIn.getLastSignedInAccount(thisFragment.getContext()));

        return thisFragment;
    }

    @Optional
    @OnClick(R.id.exportDbButton)
    public void onClickExportDatabase() {
        //save database
        //path: /data/data/ar.com.madrefoca.alumnospagos/databases/AlumnosTango.db

        //create folder in internal storage if does not exist.
        createJsonFolder();

        Log.d("Database path: ",databaseHelper.getReadableDatabase().getPath());
    }

    /** Start sign in activity. */
    private void signIn() {
        Log.i(TAG, "Start sign in");
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    /** Build a Google SignIn client. */
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(thisFragment.getContext(), signInOptions);
    }

    /** Create a new file and save it to Drive. */
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        final Bitmap image = mBitmapToSave;

        mDriveResourceClient
                .createContents()
                .continueWithTask(
                        new Continuation<DriveContents, Task<Void>>() {
                            @Override
                            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                                return createFileIntentSender(task.getResult(), image);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Failed to create new contents.", e);
                            }
                        });
    }

    /**
     * Creates an {@link IntentSender} to start a dialog activity with configured {@link
     * CreateFileActivityOptions} for user to create a new photo in Drive.
     */
    private Task<Void> createFileIntentSender(DriveContents driveContents, Bitmap image) {
        Log.i(TAG, "New contents created.");
        // Get an output stream for the contents.
        OutputStream outputStream = driveContents.getOutputStream();

        try {
            outputStream.write("asdf".getBytes());
        } catch (IOException e) {
            Log.w(TAG, "Unable to write file contents.", e);
        }

        // Create the initial metadata - MIME type and title.
        // Note that the user will be able to change the title later.
        MetadataChangeSet metadataChangeSet =
                new MetadataChangeSet.Builder()
                        .setMimeType("application/json")
                        .setTitle("examplefile.json")
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
                                //startIntentSenderForResult(task.getResult(), REQUEST_CODE_CREATOR, null, 0, 0, 0);
                                return null;
                            }
                        });
    }

    private void createJsonFolder() {
        String path = Environment.getDataDirectory().getAbsolutePath().toString() +
                "/ar.com.madrefoca.alumnospagos/jsonFolder";
        File file = new File(thisFragment.getContext().getFilesDir(), "example.json");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fos;
        DataOutputStream dos;
        String s = "";
        try {
            File f = thisFragment.getContext().getFilesDir();
            s = f.getCanonicalPath();
            File file2= new File(s + "/archivo3.txt");

            if(file.exists()){
                file.delete();
            }

            if(file2.exists()){
                file2.delete();
            }
            file2.createNewFile();
            fos = new FileOutputStream(file2);
            dos = new DataOutputStream(fos);
            dos.write("asdfadf".getBytes());
            dos.writeChars("\n");
            dos.write("ddddddddddddddd".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("app path: ",s);

    }

    @Optional
    @OnClick(R.id.importContactsButton)
    public void onClickImportContactsButton() {
        count =1;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        UtilImportContacts utilImportContacts = new UtilImportContacts(thisFragment.getContext(), progressBar);

    }

}


