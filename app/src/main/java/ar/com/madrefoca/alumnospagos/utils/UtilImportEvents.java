package ar.com.madrefoca.alumnospagos.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by fernando on 04/02/18.
 */

public class UtilImportEvents extends AsyncTask<String, Integer, String> {

    private Context context;
    private ProgressBar progressBar;
    private String jsonEvents;

    public UtilImportEvents(Context context, ProgressBar progressBar, String jsonEvents) {
        this.context = context;
        this.progressBar = progressBar;
        this.jsonEvents = jsonEvents;
    }

    @Override
    protected String doInBackground(String... origin) {
        //this.importAllEvents(origin[0]);

        return "Import all events task completed.";
    }

    @Override
    protected void onPostExecute(String result) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPreExecute() {
        //progressBar.setMax(this.countContacts());
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]);
    }
}
