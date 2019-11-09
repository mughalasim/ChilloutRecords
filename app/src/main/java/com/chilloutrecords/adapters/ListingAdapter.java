package com.chilloutrecords.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chilloutrecords.models.ListingModel;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.material.card.MaterialCardView;
import com.makeramen.roundedimageview.RoundedImageView;

import com.chilloutrecords.R;

import java.util.ArrayList;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

    private Context
            context;
    private ArrayList<ListingModel>
            models;
    private String
            no_results = "";

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView
                txt_no_results,
                txt;
        private RoundedImageView
                img;
        private MaterialCardView
                cv_list_item;

        ViewHolder(View v) {
            super(v);
            txt = v.findViewById(R.id.txt);
            img = v.findViewById(R.id.img);
            cv_list_item = v.findViewById(R.id.cv_list_item);
            txt_no_results = v.findViewById(R.id.txt_no_results);
        }
    }

    public ListingAdapter(Context context, ArrayList<ListingModel> models, String no_results) {
        this.context = context;
        this.models = models;
        this.no_results = no_results;
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

        if (model == null) {
            holder.txt_no_results.setVisibility(View.VISIBLE);
            holder.cv_list_item.setVisibility(View.GONE);
            holder.txt_no_results.setText(no_results);
        } else {
            holder.txt_no_results.setVisibility(View.GONE);
            holder.cv_list_item.setVisibility(View.VISIBLE);

            Glide.with(context).load(model.img).into(holder.img);
            holder.txt.setText(model.txt);
        }

        StaticMethods.animate_recycler_view(holder.itemView);

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

}