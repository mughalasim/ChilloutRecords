package asimmughal.chilloutrecords.main_pages.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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
import asimmughal.chilloutrecords.main_pages.models.ArtistModel;
import asimmughal.chilloutrecords.utils.Helpers;

public class artistAdapter extends RecyclerView.Adapter<artistAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ArtistModel> mDataset;
    private Helpers helper;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView stage_name;
        private TextView year_since;
        private TextView info;
        private RoundedImageView ppic;

        private RelativeLayout no_list_item, list_item;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            stage_name = (TextView) v.findViewById(R.id.stage_name);
            year_since = (TextView) v.findViewById(R.id.year_since);
            info = (TextView) v.findViewById(R.id.info);
            ppic = v.findViewById(R.id.ppic);
            no_list_item = (RelativeLayout) v.findViewById(R.id.no_list_items);
            list_item = (RelativeLayout) v.findViewById(R.id.list_items);

        }


    }

    public artistAdapter(Context context, ArrayList<ArtistModel> myDataset) {
        this.context = context;
        this.mDataset = myDataset;
        helper = new Helpers(context);

    }

    @Override
    public artistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_artists, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ArtistModel artistModel = mDataset.get(position);
        if (artistModel == null || artistModel.name.equals("")) {
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item.setVisibility(View.GONE);

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            holder.name.setText(artistModel.name);
            holder.stage_name.setText("Stage Name: "+artistModel.stage_name);
            holder.info.setText(artistModel.info);
            holder.year_since.setText(artistModel.year_since);
            Glide.with(context).load(artistModel.ppic).into(holder.ppic);

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!helper.validateInternetNotPresent() && artistModel.id.length() > 0) {
                        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(),
                                ArtistDetailsActivity.class)
                                .putExtra("id", artistModel.id)
                                .putExtra("name", artistModel.name)
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