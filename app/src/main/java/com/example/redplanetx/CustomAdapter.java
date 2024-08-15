package com.example.redplanetx;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    private String[] titles;
    private int[] images;
    private String[] descriptions;

    public CustomAdapter(Context context, String[] titles, int[] images, String[] descriptions) {
        this.context = context;
        this.titles = titles;
        this.images = images;
        this.descriptions = descriptions;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        TextView title = convertView.findViewById(R.id.item_title);
        ImageView image = convertView.findViewById(R.id.item_image);
        TextView description = convertView.findViewById(R.id.item_text);

        title.setText(titles[position]);
        image.setImageResource(images[position]);
        description.setText(descriptions[position]);

        convertView.setOnClickListener(view -> {
            // Create an intent to open DetailActivity
            Intent intent = new Intent(context, MainActivity2.class);

            // Pass data to the new activity
            intent.putExtra("title", titles[position]);
           // intent.putExtra("image", images[position]);
           // intent.putExtra("description", descriptions[position]);

            // Start the new activity
            context.startActivity(intent);
        });



        return convertView;
    }
}
