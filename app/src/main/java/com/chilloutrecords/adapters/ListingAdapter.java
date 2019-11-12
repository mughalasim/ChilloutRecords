package com.chilloutrecords.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chilloutrecords.R;
import com.chilloutrecords.models.ListingModel;
import com.chilloutrecords.utils.StaticMethods;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

    private Context
            context;
    private ArrayList<ListingModel>
            models;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView
                txt;
        private RoundedImageView
                img;

        ViewHolder(View v) {
            super(v);
            txt = v.findViewById(R.id.txt);
            img = v.findViewById(R.id.img);
        }
    }

    public ListingAdapter(Context context, ArrayList<ListingModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_listings, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final ListingModel model = models.get(position);

        Glide.with(context).load(model.img).into(holder.img);
        holder.txt.setText(model.txt);

        StaticMethods.animate_recycler_view(holder.itemView);

    }

    @Override
    public int getItemCount() {
        if (models != null) {
            return models.size();
        } else {
            return 0;
        }
    }


}