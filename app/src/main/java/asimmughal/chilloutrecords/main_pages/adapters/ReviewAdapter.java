package asimmughal.chilloutrecords.main_pages.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.models.ReviewModel;
import asimmughal.chilloutrecords.utils.Helpers;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private ArrayList<ReviewModel> mDataset;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView user_display_name;
        private TextView review_text;

        private ImageView user_thumbnail;
        private RelativeLayout no_list_item, list_item;
        private RatingBar rating;

        public ViewHolder(View v) {
            super(v);

            user_display_name = (TextView) v.findViewById(R.id.user_display_name);
            review_text = (TextView) v.findViewById(R.id.review_text);

            no_list_item = (RelativeLayout) v.findViewById(R.id.no_list_items);
            list_item = (RelativeLayout) v.findViewById(R.id.list_items);
            user_thumbnail = (ImageView) v.findViewById(R.id.user_thumbnail);
            rating = (RatingBar) v.findViewById(R.id.rating);
        }


    }

    public ReviewAdapter(ArrayList<ReviewModel> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_reviews, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ReviewModel reviewModel = mDataset.get(position);
        if (reviewModel.review_text.equals("")) {
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item.setVisibility(View.GONE);

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            holder.user_display_name.setText(reviewModel.user_display_name);
            holder.review_text.setText(reviewModel.review_text);


            if (reviewModel.rating.equals("")) {
                holder.rating.setRating(1);
            } else {
                holder.rating.setRating(Float.valueOf(reviewModel.rating) / 2);
            }

            Glide.with(holder.itemView.getContext()).load(reviewModel.user_thumbnail).into(holder.user_thumbnail);

        }

        Helpers.animate_recyclerview(holder.itemView);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}