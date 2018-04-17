package ar.com.madrefoca.alumnospagos.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Attendee;
import ar.com.madrefoca.alumnospagos.model.Event;
import ar.com.madrefoca.alumnospagos.utils.ExportToExcelUtil;
import ar.com.madrefoca.alumnospagos.utils.JsonUtil;
import ar.com.madrefoca.alumnospagos.utils.PermissionUtil;
import ar.com.madrefoca.alumnospagos.utils.UtilImportContacts;
import ar.com.madrefoca.alumnospagos.utils.UtilImportDataFromDrive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class SettingsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    @Nullable
    @BindView(R.id.exportDbButton)
    Button exportDbButton;

    @Nullable
    @BindView(R.id.importContactsButton)
    Button importContactsButton;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Nullable
    @BindView(R.id.contact_filter_word)
    EditText contactFilterWord;

    private DatabaseHelper databaseHelper = null;

    private static final String TAG = "Settings activity";
    protected static final int REQUEST_CODE_SIGN_IN = 0;
    protected static final int REQUEST_CODE_OPEN_ITEM = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final String FROM_PHONE = "phone";
    private static final String FROM_GOOGLE_DRIVE = "drive";
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS};
    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_CONTACTS = 3;

    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private Dao<Attendee, Integer> attendeeDao;
    private Dao<Event, Integer> eventsDao;
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;
    private AlertDialog.Builder filterContactsDialog;
    private View view;
    private String workingTable;
    private String fileName;

    @Override
    protected void onStart() {
        super.onStart();
        signIn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this, this);

        databaseHelper = OpenHelperManager.getHelper(this.getApplicationContext(),DatabaseHelper.class);

        try {
            attendeeDao = databaseHelper.getAttendeeDao();
            eventsDao = databaseHelper.getEventsDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.initDialog();
    }

    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal. For apps where sign-in is optional, handle
                    // appropriately
                    Log.e(TAG, "Sign-in failed.");
                    Toast.makeText(getApplicationContext(),
                            "Sign-in failed.",
                            Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient();
                } else {
                    Log.e(TAG, "Sign-in failed.");
                    Toast.makeText(getApplicationContext(),
                            "Sign-in failed.",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case REQUEST_CODE_OPEN_ITEM:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    mOpenItemTaskSource.setResult(driveId);
                } else {
                    Log.e(TAG, "Unable to open file.");
                    Log.e(TAG, "Sign-in failed.");
                    Toast.makeText(getApplicationContext(),
                            "Sign-in failed.",
                            Toast.LENGTH_LONG).show();
                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Starts the sign-in process and initializes the Drive client.
     */
    protected void signIn() {
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
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }

    /**
     * Continues the sign-in process, initializing the Drive clients with the current
     * user's account.
     */
    private void initializeDriveClient() {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
    }

    protected void onDriveClientReady() {
        pickJsonFile()
                .addOnSuccessListener(this,
                        new OnSuccessListener<DriveId>() {
                            @Override
                            public void onSuccess(DriveId driveId) {
                                retrieveContents(driveId.asDriveFile());
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "No file selected", e);
                        showMessage(getString(R.string.file_not_selected));
                        finish();
                    }
                });
    }

    private void retrieveContents(DriveFile file) {
        // [START open_file]
        Task<DriveContents> openFileTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);
        // [END open_file]
        // [START read_contents]
        openFileTask
                .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                        DriveContents contents = task.getResult();
                        // Process contents...
                        // [START_EXCLUDE]
                        // [START read_as_string]
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(contents.getInputStream()))) {
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line);
                            }

                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.setProgress(0);

                            //UtilImportContacts utilImportContacts = new UtilImportContacts(getApplicationContext(), progressBar, builder.toString());
                            //utilImportContacts.execute(FROM_GOOGLE_DRIVE);

                            UtilImportDataFromDrive importDataFromDrive = new UtilImportDataFromDrive(getApplicationContext(), progressBar, builder.toString());
                            importDataFromDrive.execute();

                            showMessage(getString(R.string.content_loaded));
                            //mFileContents.setText(builder.toString());
                            Log.i("file loaded ---->", builder.toString());
                        }
                        // [END read_as_string]
                        // [END_EXCLUDE]
                        // [START discard_contents]
                        Task<Void> discardTask = getDriveResourceClient().discardContents(contents);
                        // [END discard_contents]
                        return discardTask;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        // [START_EXCLUDE]
                        Log.e(TAG, "Unable to read contents", e);
                        showMessage(getString(R.string.read_failed));
                        finish();
                        // [END_EXCLUDE]
                    }
                });
        // [END read_contents]
    }

    /**
     * Prompts the user to select a text file using OpenFileActivity.
     *
     * @return Task that resolves with the selected item's ID.
     */
    protected Task<DriveId> pickJsonFile() {
        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "application/json"))
                        .setActivityTitle(getString(R.string.select_file))
                        .build();
        return pickItem(openOptions);
    }

    /**
     * Prompts the user to select a folder using OpenFileActivity.
     *
     * @param openOptions Filter that should be applied to the selection
     * @return Task that resolves with the selected item's ID.
     */
    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
        mOpenItemTaskSource = new TaskCompletionSource<>();
        getDriveClient()
                .newOpenFileActivityIntentSender(openOptions)
                .continueWith(new Continuation<IntentSender, Void>() {
                    @Override
                    public Void then(@NonNull Task<IntentSender> task) throws Exception {
                        startIntentSenderForResult(
                                task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
                        return null;
                    }
                });
        return mOpenItemTaskSource.getTask();
    }

    /** Create a new file and save it to Drive. */
    private void saveFileToDrive(String type) {
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
                file = prepareJsonToSave(workingTable);
                mimeType = "application/json";
                fileTitle = fileName + ".json";
                fileContent = file.getBytes();
                break;
            case "xls":
                ExportToExcelUtil exportToExcelUtil = new ExportToExcelUtil(getApplicationContext(), databaseHelper);
                fileContent = exportToExcelUtil.generateExcelFile();
                mimeType = "application/vnd.ms-excel";
                fileTitle = "pagosEnExcel.xls";
                break;
        }

        Log.i(TAG, "Json content from " + workingTable + ": " + file);
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

    private String prepareJsonToSave(String tableToExport) {
        String jsonString = "";
        switch (tableToExport) {
            case "attendees":
                try {
                    List<Attendee> attendees = attendeeDao.queryForAll();
                    jsonString = JsonUtil.attendeesToJSon(attendees);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "events":
                try {
                    List<Event> events = eventsDao.queryForAll();
                    jsonString = JsonUtil.eventsToJSon(events);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "all":
                jsonString = JsonUtil.allTablesToJSon(databaseHelper);
                break;
            default:
                jsonString = "";
                break;
        }
        return jsonString;
    }

    private void initDialog()  {
        filterContactsDialog = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_contacts_filter, null);
        contactFilterWord = (EditText)view.findViewById(R.id.contact_filter_word);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        filterContactsDialog.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.filter_import_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(0);

                        UtilImportContacts utilImportContacts = new UtilImportContacts(getApplicationContext(), progressBar);
                        utilImportContacts.setFilterWord(contactFilterWord.getText().toString());
                        utilImportContacts.execute(FROM_PHONE);
                    }
                });

    }

    /**
     * Requests the Contacts permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestContactsPermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT, REQUEST_CONTACTS);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CONTACTS) {
            Log.i(TAG, "Received response for contact permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                filterContactsDialog.show();
            } else {
                Log.i(TAG, "Contacts permissions were NOT granted.");
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    @Optional
    @OnClick(R.id.exportDbButton)
    public void onClickExportDatabase() {
        //save database
        //path: /data/data/ar.com.madrefoca.alumnospagos/databases/AlumnosTango.db
        this.initializeDriveClient();

        //create folder in internal storage if does not exist.
        //createJsonFolder();
        workingTable = "attendees";
        fileName = getString(R.string.file_name_attendees);
        saveFileToDrive("json");

        Log.d("Database path: ",databaseHelper.getReadableDatabase().getPath());
    }

    @Optional
    @OnClick(R.id.saveAllInDriveButton)
    public void onClickSaveAllInDriveButton() {
        this.initializeDriveClient();
        workingTable = "all";
        fileName = getString(R.string.file_name_all);
        saveFileToDrive("json");
    }

    @Optional
    @OnClick(R.id.exportToExcel)
    public void onClickExportToExcelButton() {
        this.initializeDriveClient();
        workingTable = "all";
        fileName = getString(R.string.file_name_all);
        saveFileToDrive("xls");
    }

    @Optional
    @OnClick(R.id.importContactsButton)
    public void onClickImportContactsButton() {
        this.removeView();

        Log.i(TAG, "Show contacts button pressed. Checking permissions.");

        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            requestContactsPermissions();

        } else {

            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG, "Contact permissions have already been granted.");
            filterContactsDialog.show();
        }
    }

    @Optional
    @OnClick(R.id.importDbButton)
    public void onClickImportDbButton() {
        onDriveClientReady();
    }

    /**
     * Shows a toast message.
     */
    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    protected DriveClient getDriveClient() {
        return mDriveClient;
    }

    protected DriveResourceClient getDriveResourceClient() {
        return mDriveResourceClient;
    }
}
