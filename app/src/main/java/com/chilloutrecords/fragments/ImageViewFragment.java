package com.chilloutrecords.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chilloutrecords.R;
import com.chilloutrecords.utils.StaticMethods;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.InputStream;
import java.net.URL;

import static com.chilloutrecords.utils.StaticVariables.STR_IMAGE_URL;

public class ImageViewFragment extends Fragment {
    private static final String TAG_LOG = "IMAGE VIEW";
    private View root_view;

    public ImageViewFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            root_view = inflater.inflate(R.layout.frag_image_view, container, false);

            SubsamplingScaleImageView image = root_view.findViewById(R.id.frag_image);
            image.setMaxScale(20);

            String IMAGE_NOT_LOADED = "0";
            if (image.getTag().toString().equals(IMAGE_NOT_LOADED)) {
                new DownLoadImageTask(image).execute(STR_IMAGE_URL);
                String IMAGE_LOADED = "1";
                image.setTag(IMAGE_LOADED);
            }

        } else {
            container.removeView(root_view);
        }

        return root_view;
    }

    @SuppressLint("StaticFieldLeak")
    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        SubsamplingScaleImageView image_view;

        DownLoadImageTask(SubsamplingScaleImageView imageView) {
            this.image_view = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                StaticMethods.logg(TAG_LOG, e.toString());
                image_view.setBackgroundResource(R.drawable.ic_info);
            }
            return logo;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                image_view.setImage(ImageSource.bitmap(result));
            } catch (Exception e) {
                StaticMethods.logg(TAG_LOG, e.toString());
                image_view.setBackgroundResource(R.drawable.ic_info);
                e.printStackTrace();
            }
        }
    }

}