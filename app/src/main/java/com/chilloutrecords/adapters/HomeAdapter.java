package com.chilloutrecords.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.interfaces.HomeInterface;
import com.chilloutrecords.interfaces.UrlInterface;
import com.chilloutrecords.models.HomeModel;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.StaticMethods;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context
            context;
    private HomeInterface listener;
    private ArrayList<HomeModel>
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

    public HomeAdapter(Context context, ArrayList<HomeModel> models, HomeInterface listener) {
        this.context = context;
        this.models = models;
        this.listener = listener;
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

        final HomeModel model = models.get(position);

        Database.getFileUrl(BuildConfig.STORAGE_IMAGES, model.img, BuildConfig.DEFAULT_HOME, new UrlInterface() {
            @Override
            public void completed (Boolean success, String url) {
                if (context != null && success)
                    Glide.with(context).load(url).into(holder.img);
            }
        });

        holder.txt.setText(model.txt);

        StaticMethods.animate_recycler_view(holder.itemView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.clicked (model.page_title, model.url);
            }
        });

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