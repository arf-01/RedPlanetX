package com.example.redplanetx;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {

    private Context context;
    private List<info> infoList;

    public InfoAdapter(Context context, List<info> infoList) {
        this.context = context;
        this.infoList = infoList;
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        info Info = infoList.get(position);
        holder.solTextView.setText("Sol: " + Info.getSol());
        holder.earthDateTextView.setText("Earth Date: " + Info.getEarthDate());
        holder.numPhotosTextView.setText("Number of Photos: " + Info.getNumPhotos());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PhotoDetailsActivity.class);
            intent.putExtra("sol", Info.getSol());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public static class InfoViewHolder extends RecyclerView.ViewHolder {
        TextView solTextView, earthDateTextView, numPhotosTextView;

        public InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            solTextView = itemView.findViewById(R.id.solTextView);
            earthDateTextView = itemView.findViewById(R.id.earthDateTextView);
            numPhotosTextView = itemView.findViewById(R.id.numPhotosTextView);
        }
    }
}
