package com.example.redplanetx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
            finish(); // Close activity if title is missing
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        photos = new ArrayList<>();
        adapter = new InfoAdapter(this, photos);
        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.progressBar); // Add a progress bar in the layout
        progressBar.setVisibility(View.VISIBLE); // Show the progress bar

        fetchMarsPhotos(title); // Pass title to fetchMarsPhotos
    }

    private void fetchMarsPhotos(String title) {
        String url = "https://api.nasa.gov/mars-photos/api/v1/manifests/" + title + "?api_key=B2hgw9SAQTLZuseGzrt25cwLnyjaTrTcyBm1TUfY";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE); // Hide the progress bar

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
                                        photos.add(Info);
                                    }

                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(MainActivity2.this, "No photos found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity2.this, "Invalid response format", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity2.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE); // Hide the progress bar in case of error
                        Log.e("Volley", "Error: " + error.getMessage());
                        Toast.makeText(MainActivity2.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonObjectRequest);
    }
}
