package asimmughal.chilloutrecords.start_up;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.activities.HomeActivity;
import asimmughal.chilloutrecords.utils.Helpers;
import asimmughal.chilloutrecords.utils.SharedPrefs;

public class LoginActivity extends AppCompatActivity {

    Helpers helper;
    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private final int RC_SIGN_IN = 234;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (account != null) {

            SharedPrefs.setUSerID(account.getId());
            SharedPrefs.setUserEmail(account.getEmail());
            SharedPrefs.setUserFirstName(account.getDisplayName());
            SharedPrefs.setUserLastName(account.getFamilyName());
            SharedPrefs.setUserPic(account.getPhotoUrl().getPath());

            finish();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

        } else {
            helper = new Helpers(LoginActivity.this);

            setContentView(R.layout.activity_login);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            SignInButton sign_in_google = findViewById(R.id.sign_in_google);
            sign_in_google.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(helper.validateGooglePlayServices(LoginActivity.this)){
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    }
                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    // BASIC FUNCTIONS =============================================================================

    private void signOut() {
        mAuth.signOut();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            startUp();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Helpers.LogThis("signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void startUp() {
        helper.ToastMessage(LoginActivity.this, "Successfully logged in");
        finish();
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }

    public void signUp(View view) {

    }

    public void logIn(View view) {

    }
}

