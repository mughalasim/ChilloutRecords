package asimmughal.chilloutrecords.main_pages.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.activities.RestaurantDetailsActivity;
import asimmughal.chilloutrecords.main_pages.models.RestaurantModel;
import asimmughal.chilloutrecords.utils.Helpers;

import static asimmughal.chilloutrecords.utils.Helpers.ADAPTER_DEFAULT;
import static asimmughal.chilloutrecords.utils.Helpers.ADAPTER_DISTANCE;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private Context context;
    private ArrayList<RestaurantModel> mDataset;
    private Helpers helper;
    private String ADAPTER_TYPE;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtId;
        private TextView txtName;
        private TextView txtArea;
        private TextView txtCuisine;
        private RoundedImageView image, offer_icon;
        private RelativeLayout no_list_item, list_item;
        private RatingBar rating;

        public ViewHolder(View v) {
            super(v);
            txtId = (TextView) v.findViewById(R.id.id);
            txtName = (TextView) v.findViewById(R.id.name);
//            txtArea = (TextView) v.findViewById(R.id.area);
//            txtCuisine = (TextView) v.findViewById(R.id.cuisine);

            no_list_item = (RelativeLayout) v.findViewById(R.id.no_list_items);
            list_item = (RelativeLayout) v.findViewById(R.id.list_items);
            image = (RoundedImageView) v.findViewById(R.id.image);
            rating = (RatingBar) v.findViewById(R.id.rating);

            offer_icon = (RoundedImageView) v.findViewById(R.id.offer_icon);
        }


    }

    public RestaurantAdapter(Context context, ArrayList<RestaurantModel> myDataset, String ADAPTER_TYPE) {
        this.context = context;
        this.mDataset = myDataset;
        this.ADAPTER_TYPE = ADAPTER_TYPE;
        helper = new Helpers(context);

    }

    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_restaurant, parent, false);
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
            holder.txtName.setText(restaurantModel.restaurant_name);

            if (ADAPTER_TYPE.equals(ADAPTER_DEFAULT)) {
                holder.txtArea.setText(restaurantModel.area_name);
            } else if (ADAPTER_TYPE.equals(ADAPTER_DISTANCE)) {
                holder.txtArea.setText(restaurantModel.distance);
            }

            if (!restaurantModel.cuisine_name.equals(""))
                holder.txtCuisine.setText(restaurantModel.cuisine_name.substring(0, restaurantModel.cuisine_name.length() - 2));

            if (restaurantModel.offer_icon.equals("")) {
                holder.offer_icon.setVisibility(View.INVISIBLE);
            } else {
                holder.offer_icon.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView.getContext()).load(restaurantModel.offer_icon).into(holder.offer_icon);
                holder.offer_icon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!helper.validateInternetNotPresent()) {
                            if(helper.validateIsLoggedIn() && holder.txtId.getText().toString().length() > 0){
                                String id = holder.txtId.getText().toString();
                                String name = holder.txtName.getText().toString();
                                String cuisine = holder.txtCuisine.getText().toString();
                                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(),
                                        RestaurantDetailsActivity.class)
                                        .putExtra("restaurant_id", id)
                                        .putExtra("restaurant_name", name)
                                        .putExtra("restaurant_desc", cuisine)
                                        .putExtra("offer", "true")
                                );
                            }
                        } else {
                            helper.myDialog(context, "Alert", context.getString(R.string.error_connection));
                        }
                    }
                });
            }

            if (restaurantModel.average_rating.equals("")) {
                holder.rating.setRating(1);
            } else {
                holder.rating.setRating(Float.valueOf(restaurantModel.average_rating) / 2);
            }

            Glide.with(holder.itemView.getContext()).load(restaurantModel.image).into(holder.image);

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!helper.validateInternetNotPresent() && holder.txtId.getText().toString().length() > 0) {
                        String id = holder.txtId.getText().toString();
                        String name = holder.txtName.getText().toString();
                        String cuisine = holder.txtCuisine.getText().toString();
                        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(),
                                RestaurantDetailsActivity.class)
                                .putExtra("restaurant_id", id)
                                .putExtra("restaurant_name", name)
                                .putExtra("restaurant_desc", cuisine)
                                .putExtra("offer", "false")
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

    public void removeItem(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
    }

    public void updateData(ArrayList<RestaurantModel> viewModels) {
        mDataset.clear();
        mDataset.addAll(viewModels);
        notifyDataSetChanged();
    }

}