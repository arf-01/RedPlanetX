package com.example.redplanetx;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InfoAdapter adapter;
    private List<info> photos;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Retrieve the data from the Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");

        if (title == null || title.isEmpty()) {
            Toast.makeText(this, "Title is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        photos = new ArrayList<>();
        adapter = new InfoAdapter(this, photos);
        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        // Execute AsyncTask to fetch data
        new FetchMarsPhotosTask().execute(title);
    }

    private class FetchMarsPhotosTask extends AsyncTask<String, Void, List<info>> {

        @Override
        protected List<info> doInBackground(String... params) {
            String title = params[0];
            String url = "https://api.nasa.gov/mars-photos/api/v1/manifests/" + title + "?api_key=B2hgw9SAQTLZuseGzrt25cwLnyjaTrTcyBm1TUfY";

            final List<info> fetchedPhotos = new ArrayList<>();
            final Object lock = new Object(); // Lock object to synchronize

            RequestQueue queue = Volley.newRequestQueue(MainActivity2.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject photoManifest = response.optJSONObject("photo_manifest");
                                if (photoManifest != null) {
                                    JSONArray photosArray = photoManifest.optJSONArray("photos");
                                    if (photosArray != null) {
                                        for (int i = 0; i < photosArray.length(); i++) {
                                            JSONObject photoObject = photosArray.getJSONObject(i);
                                            int sol = photoObject.optInt("sol", 0);
                                            String earthDate = photoObject.optString("earth_date", "N/A");
                                            int numPhotos = photoObject.optInt("total_photos", 0);

                                            info Info = new info(sol, earthDate, numPhotos);
                                            fetchedPhotos.add(Info);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                Log.e("FetchMarsPhotosTask", "JSON Exception", e);
                            }
                            synchronized (lock) {
                                lock.notify(); // Notify that data has been fetched
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley", "Error: " + error.getMessage());
                    synchronized (lock) {
                        lock.notify(); // Notify that there was an error
                    }
                }
            });

            // Set timeout, retry policy, and cache
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000, // 30 seconds timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            jsonObjectRequest.setShouldCache(true);

            queue.add(jsonObjectRequest);

            // Wait until the data fetching is done
            synchronized (lock) {
                try {
                    lock.wait(); // Wait for the notification
                } catch (InterruptedException e) {
                    Log.e("FetchMarsPhotosTask", "Wait interrupted", e);
                }
            }

            return fetchedPhotos;
        }

        @Override
        protected void onPostExecute(List<info> result) {
            progressBar.setVisibility(View.GONE);
            if (result != null && !result.isEmpty()) {
                photos.clear(); // Clear existing data
                photos.addAll(result); // Add fetched data
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity2.this, "Data Loaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity2.this, "No data available", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity2.this, "Failed to load data", Toast.LENGTH_SHORT).show();
        }
    }
}
