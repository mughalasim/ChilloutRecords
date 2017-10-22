package asimmughal.chilloutrecords.main_pages.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.activities.HomePageCollectionActivity;
import asimmughal.chilloutrecords.main_pages.models.RestaurantModel;
import asimmughal.chilloutrecords.utils.Helpers;

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.ViewHolder> {
    private ArrayList<RestaurantModel> mDataset;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtId;
        private TextView txtTitle;
        private TextView txtArticleTitle;
        private TextView txtDescription;

        private ImageView image;
        private RelativeLayout no_list_item, list_item;

        public ViewHolder(View v) {
            super(v);
            txtId = (TextView) v.findViewById(R.id.id);
            txtTitle = (TextView) v.findViewById(R.id.title);
            txtArticleTitle = (TextView) v.findViewById(R.id.article_title);
            txtDescription = (TextView) v.findViewById(R.id.description);


            no_list_item = (RelativeLayout) v.findViewById(R.id.no_list_items);
            list_item = (RelativeLayout) v.findViewById(R.id.list_items);
            image = (ImageView) v.findViewById(R.id.image);
        }


    }

    public HomePageAdapter(ArrayList<RestaurantModel> myDataset) {
        this.mDataset = myDataset;
    }

    @Override
    public HomePageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_collection, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final RestaurantModel restaurantModel = mDataset.get(position);
        if (restaurantModel.id == null) {
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item.setVisibility(View.GONE);

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            holder.txtId.setText(restaurantModel.id);
            holder.txtTitle.setText(restaurantModel.restaurant_name);
            holder.txtArticleTitle.setText(restaurantModel.area_name);
            holder.txtDescription.setText(restaurantModel.cuisine_name);

            Glide.with(holder.itemView.getContext())
                    .load(restaurantModel.image)
                    .into(holder.image);

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.txtId.getText().toString().length() > 1) {
                        String id = holder.txtId.getText().toString();
                        String name = holder.txtTitle.getText().toString();
                        String cuisine = holder.txtDescription.getText().toString();
                        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(),
                                HomePageCollectionActivity.class)
                                .putExtra("collection_id", id)
                                .putExtra("collection_name", name)
                                .putExtra("collection_desc", cuisine)
                        );

                    }else{
                        Helpers helper = new Helpers(holder.itemView.getContext());
                        helper.ToastMessage(holder.itemView.getContext(), "Coming Soon");
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