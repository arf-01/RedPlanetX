package com.example.redplanetx;

import android.content.Intent;
import android.os.Bundle;
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

        fetchMarsPhotos(title, new FetchMarsPhotosCallback() {
            @Override
            public void onSuccess(List<info> fetchedPhotos) {
                progressBar.setVisibility(View.GONE);
                photos.clear();
                photos.addAll(fetchedPhotos);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity2.this, "Failed to load data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMarsPhotos(String title, final FetchMarsPhotosCallback callback) {
        String url = "https://api.nasa.gov/mars-photos/api/v1/manifests/" + title + "?api_key=B2hgw9SAQTLZuseGzrt25cwLnyjaTrTcyBm1TUfY";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            List<info> fetchedPhotos = new ArrayList<>();
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
                                    callback.onSuccess(fetchedPhotos);
                                } else {
                                    callback.onFailure("No photos found");
                                }
                            } else {
                                callback.onFailure("No photo manifest found");
                            }
                        } catch (JSONException e) {
                            callback.onFailure("JSON parsing error");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure("Network error: " + error.getMessage());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsonObjectRequest.setShouldCache(true);

        queue.add(jsonObjectRequest);
    }

    interface FetchMarsPhotosCallback {
        void onSuccess(List<info> fetchedPhotos);
        void onFailure(String errorMessage);
    }
}
