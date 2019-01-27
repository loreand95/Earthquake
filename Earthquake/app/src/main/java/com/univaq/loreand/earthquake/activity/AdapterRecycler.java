package com.univaq.loreand.earthquake.activity;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.univaq.loreand.earthquake.R;
import com.univaq.loreand.earthquake.model.Earthquake;

import java.util.List;



public class AdapterRecycler extends RecyclerView.Adapter<AdapterRecycler.ViewHolder> {

    private List<Earthquake> data;

    public AdapterRecycler(List<Earthquake> data){
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Earthquake earthquake = data.get(i);
        viewHolder.title.setText(Double.toString(earthquake.getMagnitude()));
        viewHolder.subtitle.setText(earthquake.getRegionName());
        viewHolder.time.setText(earthquake.getTime().toLocalTime().toString());


    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    // Use ViewHolder Pattern
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView subtitle;
        TextView time;

        ViewHolder(@NonNull View view) {
            super(view);

            title = view.findViewById(R.id.title);
            subtitle = view.findViewById(R.id.subtitle);
            time = view.findViewById(R.id.time);

            // Define the click event on item
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Open another Activity and pass to it the right city

                    //TODO

                    Earthquake earthquake = data.get(getAdapterPosition());
                    Intent intent = new Intent(v.getContext(), MapsActivity.class);

                    intent.putExtra("region",earthquake.getRegionName());
                    intent.putExtra("magnitude",earthquake.getMagnitude());
                    intent.putExtra("latitude", earthquake.getLat());
                    intent.putExtra("longitude", earthquake.getLon());
                    intent.putExtra("time",earthquake.getTime().toLocalTime().toString());

                    Log.d("ORA",earthquake.getTime().toString());
                    v.getContext().startActivity(intent);

                }
            });
        }
    }
}
