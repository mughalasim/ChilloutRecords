package com.chilloutrecords.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.activities.ParentActivity;
import com.chilloutrecords.interfaces.GeneralInterface;
import com.chilloutrecords.interfaces.UrlInterface;
import com.chilloutrecords.models.NavigationModel;
import com.chilloutrecords.models.UserModel;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Objects;

import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_PROFILE_EDIT;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_SHARE;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_USER;
import static com.chilloutrecords.utils.StaticVariables.USER_MODEL;

public class ProfileFragment extends Fragment {
    private View root_view;

    private String STR_ID = "";
    private boolean is_me;

    private String IMG_PROFILE_URL = "";
    private TextView
            txt_name,
            txt_stage_name,
            txt_info,
            txt_profile_visits,
            txt_member_since;
    private MaterialButton
            btn_singles,
            btn_collections;

    private RoundedImageView
            img_profile;

    private DatabaseReference
            reference;



    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_profile, container, false);

                reference = FIREBASE_DB.getReference(BuildConfig.DB_REF_USERS);

                // FIND ALL VIEWS
                txt_name = root_view.findViewById(R.id.txt_name);
                txt_stage_name = root_view.findViewById(R.id.txt_stage_name);
                txt_info = root_view.findViewById(R.id.txt_info);
                txt_profile_visits = root_view.findViewById(R.id.txt_profile_visits);
                txt_member_since = root_view.findViewById(R.id.txt_member_since);

                img_profile = root_view.findViewById(R.id.img_profile);
                final FloatingActionButton btn_edit_picture = root_view.findViewById(R.id.btn_edit_picture);
                FloatingActionButton btn_edit_profile = root_view.findViewById(R.id.btn_edit_profile);

                btn_singles = root_view.findViewById(R.id.btn_singles);
                btn_collections = root_view.findViewById(R.id.btn_collections);

                btn_singles.setVisibility(View.GONE);
                btn_collections.setVisibility(View.GONE);

                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    STR_ID = bundle.getString(EXTRA_STRING);
                    btn_edit_picture.hide();
                    btn_edit_profile.hide();
                    is_me = false;
                } else {
                    STR_ID = FIREBASE_USER.getUid();
                    btn_edit_picture.show();
                    btn_edit_profile.show();
                    is_me = true;
                }

                root_view.findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new ShareFragment(), PAGE_TITLE_SHARE, "", null, true));
                    }
                });
                btn_edit_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new RegisterFragment(), PAGE_TITLE_PROFILE_EDIT, "", null, true));
                    }
                });


                img_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new ImageViewFragment(), "", IMG_PROFILE_URL, null, true));
                    }
                });


                btn_edit_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
                        dialog.setContentView(R.layout.dialog_picture_update);
                        dialog.setCancelable(true);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        dialog.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                USER_MODEL.p_pic = BuildConfig.DEFAULT_PROFILE_ART;
                                Database.setUser(USER_MODEL, new GeneralInterface() {
                                    @Override
                                    public void success() {
                                        updateArt(USER_MODEL.p_pic);
                                    }

                                    @Override
                                    public void failed() {
                                        StaticMethods.showToast(getString(R.string.error_update_failed));
                                    }
                                });
                                dialog.cancel();
                            }
                        });

                        dialog.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                                ((ParentActivity) Objects.requireNonNull(getActivity())).checkImagePermissions();
                            }
                        });

                        dialog.show();
                    }
                });


            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            container.removeView(root_view);
        }
        return root_view;
    }

    @Override
    public void onResume() {
        loadProfile();
        super.onResume();
    }




    // CLASS METHODS ===============================================================================
    private void loadProfile() {
        reference.child(STR_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserModel model = dataSnapshot.getValue(UserModel.class);
                if (model != null) {
                    if (is_me) {
                        USER_MODEL = model;
                    }

                    updateArt(model.p_pic);

                    // General info
                    txt_name.setText(model.name);
                    txt_stage_name.setText(getString(R.string.txt_stage_name).concat(model.stage_name));
                    txt_info.setText(model.info);
                    // stats
                    txt_profile_visits.setText(getString(R.string.txt_profile_visits).concat(String.valueOf(model.profile_visits)));
                    txt_member_since.setText(getString(R.string.txt_member_since).concat(StaticMethods.getDate(model.member_since_date)));
                    // Music content
                    if (model.is_artist && model.music != null) {

                        if (model.music.singles.size() > 0) {
                            btn_singles.setVisibility(View.VISIBLE);
                            btn_singles.setText(String.valueOf(model.music.singles.size()).concat(" Single(s)"));
                            btn_singles.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((ParentActivity) Objects.requireNonNull(getActivity()))
                                            .loadFragment(
                                                    new NavigationModel(new TrackFragment(), "Singles", BuildConfig.DB_REF_SINGLES, model.music.singles,
                                                            true
                                                    ));
                                }
                            });
                        } else {
                            btn_singles.setVisibility(View.GONE);
                        }

                        if (model.music.collections.size() > 0) {
                            btn_collections.setVisibility(View.VISIBLE);
                            btn_collections.setText(String.valueOf(model.music.collections.size()).concat(" Album(s) / Mixtape(s)"));
                            btn_collections.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((ParentActivity) Objects.requireNonNull(getActivity()))
                                            .loadFragment(
                                                    new NavigationModel(new TrackFragment(), "Albums / Mixtapes", BuildConfig.DB_REF_COLLECTIONS, model.music.collections,
                                                            true
                                                    ));
                                }
                            });
                        } else {
                            btn_collections.setVisibility(View.GONE);
                        }
                    } else {
                        btn_singles.setVisibility(View.GONE);
                        btn_collections.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Database.handleDatabaseError(databaseError);
            }
        });
    }

    public void updateArt(String image_url) {
        Database.getFileUrl(BuildConfig.STORAGE_IMAGES, image_url, BuildConfig.DEFAULT_PROFILE_ART, new UrlInterface() {
            @Override
            public void completed(Boolean success, String url) {
                if (success) {
                    Glide.with(Objects.requireNonNull(getActivity())).load(url).into(img_profile);
                    IMG_PROFILE_URL = url;
                }
            }
        });
    }
}
