package asimmughal.chilloutrecords.main_pages.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.activities.ArtistDetailsActivity;
import asimmughal.chilloutrecords.main_pages.activities.VideoPlayerActivity;
import asimmughal.chilloutrecords.main_pages.models.GeneralModel;
import asimmughal.chilloutrecords.utils.Helpers;

import static asimmughal.chilloutrecords.main_pages.activities.HomeActivity.ARTISTS;
import static asimmughal.chilloutrecords.main_pages.activities.HomeActivity.VIDEOS;

public class generalAdapter extends RecyclerView.Adapter<generalAdapter.ViewHolder> {
    private Context context;
    private ArrayList<GeneralModel> mDataset;
    private Helpers helper;
    private String adapter_type = "";

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView artist_name;
        private TextView artist_stage_name;
        private TextView artist_year_since;
        private TextView artist_info;
        private RoundedImageView artist_ppic;

        private TextView video_name;
        private TextView video_desc;
        private TextView video_url;
        private RoundedImageView video_ppic;


        private RelativeLayout no_list_item, list_item_artists, list_item_videos;

        ViewHolder(View v) {
            super(v);
            artist_name = v.findViewById(R.id.artist_name);
            artist_stage_name = v.findViewById(R.id.artist_stage_name);
            artist_year_since = v.findViewById(R.id.artist_year_since);
            artist_info = v.findViewById(R.id.artist_info);
            artist_ppic = v.findViewById(R.id.artist_ppic);

            video_name = v.findViewById(R.id.video_name);
            video_desc = v.findViewById(R.id.video_desc);
            video_url = v.findViewById(R.id.video_url);
            video_ppic = v.findViewById(R.id.video_ppic);

            no_list_item = v.findViewById(R.id.no_list_items);
            list_item_artists = v.findViewById(R.id.list_item_artists);
            list_item_videos = v.findViewById(R.id.list_item_videos);

        }


    }

    public generalAdapter(Context context, ArrayList<GeneralModel> myDataset, String adapter_type) {
        this.context = context;
        helper = new Helpers(context);
        this.mDataset = myDataset;
        this.adapter_type = adapter_type;

    }

    @Override
    public generalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_general, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final GeneralModel generalModel = mDataset.get(position);
        if (generalModel == null) {
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item_artists.setVisibility(View.GONE);
            holder.list_item_videos.setVisibility(View.GONE);

        } else if (adapter_type.equals(ARTISTS)) {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item_artists.setVisibility(View.VISIBLE);
            holder.list_item_videos.setVisibility(View.GONE);

            holder.artist_name.setText(generalModel.artist_name);
            holder.artist_stage_name.setText(generalModel.artist_stage_name);
            holder.artist_info.setText(generalModel.artist_info);
            holder.artist_year_since.setText(generalModel.artist_year_since);
            Glide.with(context).load(generalModel.artist_ppic).into(holder.artist_ppic);

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!helper.validateInternetNotPresent() && generalModel.artist_id.length() > 0) {
                        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(),
                                ArtistDetailsActivity.class)
                                .putExtra("id", generalModel.artist_id)
                                .putExtra("name", generalModel.artist_name)
                                .putExtra("ppic", generalModel.artist_ppic)
                        );

                    } else {
                        helper.myDialog(context, "Alert", context.getString(R.string.error_connection));
                    }
                }
            });

        } else if (adapter_type.equals(VIDEOS)) {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item_artists.setVisibility(View.GONE);
            holder.list_item_videos.setVisibility(View.VISIBLE);

            holder.video_name.setText(generalModel.video_name);
            holder.video_desc.setText(generalModel.video_desc);
            holder.video_url.setText(generalModel.video_url);
            Glide.with(context).load(generalModel.video_ppic).into(holder.video_ppic);

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!helper.validateInternetNotPresent() && generalModel.video_url.length() > 0) {
                        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(),
                                VideoPlayerActivity.class)
                                .putExtra("VIDEO_URL", generalModel.video_url)
                        );

                    } else {
                        helper.myDialog(context, "Alert", context.getString(R.string.error_connection));
                    }
                }
            });
        }

        Helpers.animate_recyclerview(holder.itemView);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}