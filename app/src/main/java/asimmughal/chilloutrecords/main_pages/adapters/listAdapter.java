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
import asimmughal.chilloutrecords.main_pages.activities.HomeActivity;
import asimmughal.chilloutrecords.main_pages.activities.RestaurantDetailsActivity;
import asimmughal.chilloutrecords.main_pages.models.RestaurantModel;
import asimmughal.chilloutrecords.utils.Helpers;

import static asimmughal.chilloutrecords.utils.Helpers.ADAPTER_DEFAULT;
import static asimmughal.chilloutrecords.utils.Helpers.ADAPTER_DISTANCE;

public class listAdapter extends RecyclerView.Adapter<listAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> mDataset;
    private Helpers helper;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private RelativeLayout no_list_item, list_item;

        public ViewHolder(View v) {
            super(v);
            txtName = (TextView) v.findViewById(R.id.name);
            no_list_item = (RelativeLayout) v.findViewById(R.id.no_list_items);
            list_item = (RelativeLayout) v.findViewById(R.id.list_items);

        }


    }

    public listAdapter(Context context, ArrayList<String> myDataset) {
        this.context = context;
        this.mDataset = myDataset;
        helper = new Helpers(context);

    }

    @Override
    public listAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_restaurant, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String restaurantModel = mDataset.get(position);
        if (restaurantModel == null || restaurantModel.equals("")) {
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item.setVisibility(View.GONE);

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            holder.txtName.setText(restaurantModel);

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!helper.validateInternetNotPresent() && holder.txtName.getText().toString().length() > 0) {
                        String name = holder.txtName.getText().toString();
//                        helper.ToastMessage(context, name);

                        HomeActivity.setNewDatabaseRef(name);

//                        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(),
//                                RestaurantDetailsActivity.class)
//                                .putExtra("restaurant_name", name)
//                        );

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