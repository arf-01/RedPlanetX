package com.example.redplanetx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ImageView roverIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        roverIcon = findViewById(R.id.icon_rovers);

        // Sample data
        String[] titles = {"Curiosity", "Opportunity", "Spirit", "Perseverance"};
        int[] images = {R.drawable.perseverance, R.drawable.opportunity, R.drawable.spirit, R.drawable.perseverance1};
        String[] descriptions = {
                "Explore the rover used in Mars missions.",
                "A beautiful landscape on Mars.",
                "The surface of the red planet.",
                "A deep crater on Mars."
        };

        // Pass all four arguments to the CustomAdapter
        CustomAdapter adapter = new CustomAdapter(this, titles, images, descriptions);
        listView.setAdapter(adapter);

        // Handle list item clicks
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            intent.putExtra("title", titles[position]);
            startActivity(intent);
        });

        roverIcon.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
