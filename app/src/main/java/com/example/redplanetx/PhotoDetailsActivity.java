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

public class PhotoDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private List<Photo> photoList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_details);

        recyclerView = findViewById(R.id.recyclerViewPhotos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        photoList = new ArrayList<>();
        adapter = new PhotoAdapter(this, photoList);
        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();

        int sol = intent.getIntExtra("sol", -1);

        if (sol == -1) {
            Toast.makeText(this, "Invalid Sol ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchPhotos(sol, new FetchPhotosCallback() {
            @Override
            public void onSuccess(List<Photo> fetchedPhotos) {
                progressBar.setVisibility(View.GONE);
                if (fetchedPhotos.isEmpty()) {
                    Toast.makeText(PhotoDetailsActivity.this, "No photos found for this Sol", Toast.LENGTH_SHORT).show();
                } else {
                    photoList.clear();
                    photoList.addAll(fetchedPhotos);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PhotoDetailsActivity.this, "Failed to load photos: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPhotos(int sol, final FetchPhotosCallback callback) {
        String roverName =getIntent().getStringExtra("rover_name");;
        String url = "https://api.nasa.gov/mars-photos/api/v1/rovers/" + roverName + "/photos?sol=" + sol + "&api_key=B2hgw9SAQTLZuseGzrt25cwLnyjaTrTcyBm1TUfY";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            List<Photo> fetchedPhotos = new ArrayList<>();
                            JSONArray photosArray = response.optJSONArray("photos");
                            if (photosArray != null && photosArray.length() > 0) {
                                for (int i = 0; i < photosArray.length(); i++) {
                                    JSONObject photoObject = photosArray.getJSONObject(i);
                                    String imgSrc = photoObject.optString("img_src", "N/A");
                                    String earthDate = photoObject.optString("earth_date", "N/A");

                                    if (!imgSrc.equals("N/A")) {
                                        Photo photo = new Photo(imgSrc, earthDate);
                                        fetchedPhotos.add(photo);
                                    }
                                }
                                callback.onSuccess(fetchedPhotos);
                            } else {
                                callback.onFailure("No photos found");
                            }
                        } catch (JSONException e) {
                            callback.onFailure("JSON parsing error: " + e.getMessage());
                            Log.e("FetchPhotosTask", "JSON Exception", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure("Network error: " + error.getMessage());
                Log.e("Volley", "Error: " + error.getMessage());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsonObjectRequest.setShouldCache(false);

        queue.add(jsonObjectRequest);
    }

    interface FetchPhotosCallback {
        void onSuccess(List<Photo> fetchedPhotos);
        void onFailure(String errorMessage);
    }
}
