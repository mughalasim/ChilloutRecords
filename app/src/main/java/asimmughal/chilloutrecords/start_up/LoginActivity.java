package asimmughal.chilloutrecords.start_up;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.activities.ArtistActivity;
import asimmughal.chilloutrecords.main_pages.activities.HomeActivity;
import asimmughal.chilloutrecords.utils.Helpers;
import asimmughal.chilloutrecords.utils.SharedPrefs;

public class LoginActivity extends AppCompatActivity {

    Helpers helper;
    CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AccessToken.getCurrentAccessToken()!=null) {
            finish();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

        } else {
            helper = new Helpers(LoginActivity.this);

            setContentView(R.layout.activity_login);

            mAuth = FirebaseAuth.getInstance();
            mCallbackManager = CallbackManager.Factory.create();
            LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions("email", "public_profile");
            loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    helper.ToastMessage(LoginActivity.this, "Login Cancelled");
                }

                @Override
                public void onError(FacebookException error) {
                    helper.ToastMessage(LoginActivity.this, "Error " + error);
                }
            });


            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        helper.setProgressDialogMessage(getString(R.string.progress_loading_fb_login));
        helper.progressDialog(true);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Helpers.LogThis("FB " + e.toString());
                        helper.progressDialog(false);
                        helper.ToastMessage(LoginActivity.this, "Failed to login with Facebook");
                        signOut();
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Profile profile = Profile.getCurrentProfile();
                            Helpers.LogThis("FB USER ID: " + user.getUid());
                            SharedPrefs.setUserFacebookID(profile.getId());
                            SharedPrefs.setUserFullName(user.getDisplayName());
                            SharedPrefs.setUserEmail(user.getEmail());
                            if (user.getPhotoUrl() != null)
                                SharedPrefs.setUserPic(user.getPhotoUrl().toString());

                            helper.ToastMessage(LoginActivity.this, "Successfully logged in with facebook");
                            finish();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                        } else {
                            helper.progressDialog(false);
                            helper.ToastMessage(LoginActivity.this, "Failed to login with Facebook");
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }

}

