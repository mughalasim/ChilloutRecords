package com.chilloutrecords.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.interfaces.TrackInterface;
import com.chilloutrecords.interfaces.UrlInterface;
import com.chilloutrecords.models.CollectionModel;
import com.chilloutrecords.models.TrackModel;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;
import static com.chilloutrecords.utils.StaticVariables.STR_IMAGE_URL;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private Context
            context;
    private DialogMethods dialogs;
    private TrackInterface
            listener;
    private String
            path;
    private ArrayList<String>
            models;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView
                txt_collection_name,
                txt_collection_release_year,
                txt_collection_type;
        private AppCompatImageView
                btn_expand;
        private RoundedImageView
                img_art;
        private LinearLayout
                ll_tracks;

        ViewHolder(View v) {
            super(v);

            txt_collection_name = v.findViewById(R.id.txt_collection_name);
            txt_collection_release_year = v.findViewById(R.id.txt_collection_release_year);
            txt_collection_type = v.findViewById(R.id.txt_collection_type);

            img_art = v.findViewById(R.id.img_art);
            btn_expand = v.findViewById(R.id.btn_expand);
            ll_tracks = v.findViewById(R.id.ll_tracks);
        }
    }

    public TrackAdapter(Context context, String path, ArrayList<String> models, TrackInterface listener) {
        this.context = context;
        this.models = models;
        this.path = path;
        this.listener = listener;
        dialogs = new DialogMethods(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_tracks, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final String id = models.get(position);

        FIREBASE_DB.getReference().child(path).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setView(holder, dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Database.handleDatabaseError(databaseError);
            }
        });

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

    private void setView(@NonNull final ViewHolder holder, DataSnapshot dataSnapshot) {
        if (path.equals(BuildConfig.DB_REF_COLLECTIONS)) {
            final CollectionModel model = dataSnapshot.getValue(CollectionModel.class);
            if (model != null) {
                holder.txt_collection_name.setText(model.name);
                holder.txt_collection_release_year.setText(context.getString(R.string.txt_released_on).concat(StaticMethods.getDate(model.release_date)));
                holder.txt_collection_type.setText(model.type.concat(" (").concat(String.valueOf(model.tracks.size())).concat(" tracks)"));
                Database.getFileUrl(BuildConfig.STORAGE_IMAGES, model.art, new UrlInterface() {
                    @Override
                    public void completed(Boolean success, final String url) {
                        if (success)
                            Glide.with(context).load(url).into(holder.img_art);
                        holder.img_art.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogs.setDialogImagePreview(url);
                            }
                        });
                    }
                });
                holder.btn_expand.setVisibility(View.VISIBLE);
                holder.ll_tracks.setVisibility(View.GONE);
                holder.btn_expand.setTag("0");
                holder.btn_expand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.btn_expand.getTag() == "0") {
                            holder.btn_expand.setTag("1");
                            holder.btn_expand.setImageResource(R.drawable.ic_arrow_up);
                            holder.ll_tracks.setVisibility(View.VISIBLE);
                        } else {
                            holder.btn_expand.setTag("0");
                            holder.btn_expand.setImageResource(R.drawable.ic_arrow_down);
                            holder.ll_tracks.setVisibility(View.GONE);
                        }
                    }
                });

                int track_number = model.tracks.size();
                for (int i = 0; i < track_number; i++) {
                    final TrackModel track = model.tracks.get(i);
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View child = inflater.inflate(R.layout.list_item_track, null);
                    TextView txt_track_number = child.findViewById(R.id.txt_track_number);
                    TextView txt_track_title = child.findViewById(R.id.txt_track_title);
                    TextView txt_track_play_count = child.findViewById(R.id.txt_track_play_count);
                    txt_track_title.setText(track.name);
                    txt_track_number.setText(String.valueOf(track.number));
                    txt_track_play_count.setText(String.valueOf(track.play_count));
                    holder.ll_tracks.addView(child);
                    child.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.success(
                                    track,
                                    BuildConfig.DB_REF_COLLECTIONS + "/" + model.id + "/tracks/" + track.id,
                                    BuildConfig.DB_REF_COLLECTIONS + "/" + model.id
                            );
                        }
                    });

                }
            } else {
                holder.itemView.setVisibility(View.GONE);
            }


        } else {
            final TrackModel model = dataSnapshot.getValue(TrackModel.class);
            if (model != null) {
                holder.txt_collection_name.setText(model.name);
                holder.txt_collection_release_year.setText(context.getString(R.string.txt_released_on).concat(StaticMethods.getDate(model.release_date)));
                holder.txt_collection_type.setText(String.valueOf(model.play_count).concat(" plays"));
                Database.getFileUrl(BuildConfig.STORAGE_IMAGES, model.art, new UrlInterface() {
                    @Override
                    public void completed(Boolean success, final String url) {
                        if (success)
                            Glide.with(context).load(url).into(holder.img_art);
                        holder.img_art.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogs.setDialogImagePreview(url);
                            }
                        });
                    }
                });
                holder.btn_expand.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.success(model,
                                BuildConfig.DB_REF_SINGLES + "/" + model.id,
                                BuildConfig.DB_REF_SINGLES
                        );
                    }
                });
            } else {
                holder.itemView.setVisibility(View.GONE);
            }
        }
    }


}