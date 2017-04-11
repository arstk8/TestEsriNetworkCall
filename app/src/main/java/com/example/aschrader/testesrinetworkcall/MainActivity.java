package com.example.aschrader.testesrinetworkcall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private boolean connected = true;

    private final NetworkConnectionChecker networkConnectionChecker = new NetworkConnectionChecker();
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectionChecker, filter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeNetworkCall("http://services.arcgisonline.com/arcgis/rest/services/World_Street_Map/MapServer?f=json");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        makeNetworkCall("http://services.arcgisonline.com/arcgis/rest/services/World_Street_Map/MapServer?f=json");
        for (int i = 0; i < 40; i++) {
            makeNetworkCall("http://services.arcgisonline.com/arcgis/rest/services/World_Street_Map/MapServer/tile/3/3/1");
        }
    }

    private void makeNetworkCall(final String url) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                OkHttpClient httpClient = okHttpClient.newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .writeTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .method("GET", null)
                        .build();
                try (Response response = httpClient.newCall(request).execute()) {
                    ResponseBody responseBody = response.body();
                    responseBody.string();
                    Log.e("TAG", "Successful");
                } catch (IOException e) {
                    Log.e("TAG", "Unsuccessful", e);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private final class NetworkConnectionChecker extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                boolean oldState = connected;
                connected = networkInfo != null && networkInfo.isConnected();

                if (oldState != connected && connected) {
                    makeNetworkCall("http://services.arcgisonline.com/arcgis/rest/services/World_Street_Map/MapServer?f=json");
                }
            }
        }
    }
}
