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
import com.chilloutrecords.interfaces.UrlInterface;
import com.chilloutrecords.interfaces.VideoListingInterface;
import com.chilloutrecords.models.VideoModel;
import com.chilloutrecords.utils.Database;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_VIDEOS;

public class VideoListingAdapter extends RecyclerView.Adapter<VideoListingAdapter.ViewHolder> {

    private Context
            context;
    private VideoListingInterface listener;
    private ArrayList<VideoModel>
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

    public VideoListingAdapter(Context context, ArrayList<VideoModel> models, VideoListingInterface listener) {
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

        final VideoModel model = models.get(position);

        Database.getFileUrl(BuildConfig.STORAGE_IMAGES, model.art, BuildConfig.DEFAULT_VIDEO_ART, new UrlInterface() {
            @Override
            public void completed (Boolean success, String url) {
                if (context != null && success)
                    Glide.with(context).load(url).into(holder.img);
            }
        });

        holder.txt.setText(model.name);

//        StaticMethods.animate_recycler_view(holder.itemView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.clicked (model, PAGE_TITLE_VIDEOS + " / " + model.name);
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