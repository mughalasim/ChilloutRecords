package asimmughal.chilloutrecords.main_pages.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.activities.RestaurantDetailsActivity;
import asimmughal.chilloutrecords.main_pages.models.GalleryModel;
import asimmughal.chilloutrecords.utils.Helpers;

public class GalleryAdapterSmall extends RecyclerView.Adapter<GalleryAdapterSmall.ViewHolder> {
    private ArrayList<GalleryModel> mDataset;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView caption;
        private ImageView image;
        private RelativeLayout no_list_item, list_item;

        public ViewHolder(View v) {
            super(v);
            caption = (TextView) v.findViewById(R.id.caption);
            no_list_item = (RelativeLayout) v.findViewById(R.id.no_list_items);
            list_item = (RelativeLayout) v.findViewById(R.id.list_items);
            image = (ImageView) v.findViewById(R.id.image);
        }


    }

    public GalleryAdapterSmall(ArrayList<GalleryModel> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public GalleryAdapterSmall.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_gallery_small, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        GalleryModel galleryModel = mDataset.get(position);
        if (galleryModel.image.equals("")) {
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item.setVisibility(View.GONE);

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            holder.caption.setText(galleryModel.caption);

            Glide.with(holder.itemView.getContext())
                    .load(galleryModel.image)
                    .into(holder.image);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RestaurantDetailsActivity) holder.itemView.getContext()).showGalleryLargeLayout();
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