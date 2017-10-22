package asimmughal.chilloutrecords.start_up;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.activities.HomeActivity;
import asimmughal.chilloutrecords.services.LoginService;
import asimmughal.chilloutrecords.utils.Helpers;
import asimmughal.chilloutrecords.utils.SharedPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText ETusername;
    private EditText ETpassword;

    private EditText register_first_name;
    private EditText register_last_name;
    private EditText register_email;
    private EditText register_password;
    private EditText register_password_confirm;
    private CheckBox register_checkbox;

    Helpers helper;

    LinearLayout login_form;
    LinearLayout startup_form;
    LinearLayout registration_form;

    CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;

    public static final String
            STARTUP = "startup",
            REGISTER = "register",
            LOGIN = "login",
            DEFAULT_FACEBOOK_EMAIL = "FACEBOOKID@facebook.com",
            DEFAULT_EMAIL = "api@eatout.co.ke",
            DEFAULT_PASSWORD = "0B1D8B1C5F4CA5841D7903D50194D3E24488D7366A4EEFF258B53F47694209FA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SharedPrefs.getToken().equals("")) {
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

            Bundle extras = getIntent().getExtras();
            String logged_out = "";
            if (extras != null) {
                logged_out = extras.getString("logged_out");
                if (logged_out != null) {
                    if (logged_out.equals("true")) {
                        signOut();
                    }
                }
            }

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            findAllViews();

            refreshForms(STARTUP);

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
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Profile profile = Profile.getCurrentProfile();
                            Helpers.LogThis("FB USER ID: " + user.getUid());
                            SharedPrefs.setUserFacebookID(profile.getId());
                            SharedPrefs.setUserFullName(user.getDisplayName());
                            SharedPrefs.setUserEmail(user.getEmail());
                            if (user.getPhotoUrl() != null)
                                SharedPrefs.setUserPic(user.getPhotoUrl().toString());

                            Helpers.LogThis("FB EMAIL: " + SharedPrefs.getUserEmail());
                            Helpers.LogThis("FB PROFILE ID: " + SharedPrefs.getUserFacebookID());

                            asyncGetFacebookUser();

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
        SharedPrefs.setToken("");
    }

    private void findAllViews() {
        ETusername = (EditText) findViewById(R.id.username);
        ETpassword = (EditText) findViewById(R.id.password);

        register_first_name = (EditText) findViewById(R.id.register_first_name);
        register_last_name = (EditText) findViewById(R.id.register_last_name);
        register_email = (EditText) findViewById(R.id.register_email);
        register_password = (EditText) findViewById(R.id.register_password);
        register_password_confirm = (EditText) findViewById(R.id.register_password_confirm);

        register_checkbox = (CheckBox) findViewById(R.id.register_checkbox);

        helper.setDefaultEditTextSelectionMode(ETusername);
        helper.setDefaultEditTextSelectionMode(ETpassword);
        helper.setDefaultEditTextSelectionMode(register_first_name);
        helper.setDefaultEditTextSelectionMode(register_last_name);
        helper.setDefaultEditTextSelectionMode(register_email);
        helper.setDefaultEditTextSelectionMode(register_password);
        helper.setDefaultEditTextSelectionMode(register_password_confirm);

        startup_form = (LinearLayout) findViewById(R.id.startup_form);
        login_form = (LinearLayout) findViewById(R.id.login_form);
        registration_form = (LinearLayout) findViewById(R.id.registration_form);
    }

    @Override
    public void onBackPressed() {
        if (login_form.getVisibility() == View.VISIBLE || registration_form.getVisibility() == View.VISIBLE) {
            refreshForms(STARTUP);
        } else {
            this.finish();
        }
    }

    public void LoginBTN(View view) {
        if (ETusername.getText().toString().length() < 1) {
            ETusername.setError(getString(R.string.error_field_required));
        } else if (ETpassword.getText().toString().length() < 4) {
            ETpassword.setError(getString(R.string.error_password_short));
        } else {
            asyncLogin(ETusername.getText().toString(), ETpassword.getText().toString());
        }
    }

    private void refreshForms(String refresh) {
        switch (refresh) {
            case STARTUP:
                startup_form.setVisibility(View.VISIBLE);
                login_form.setVisibility(View.GONE);
                registration_form.setVisibility(View.GONE);
                break;
            case LOGIN:
                startup_form.setVisibility(View.GONE);
                login_form.setVisibility(View.VISIBLE);
                registration_form.setVisibility(View.GONE);
                break;
            case REGISTER:
                startup_form.setVisibility(View.GONE);
                login_form.setVisibility(View.GONE);
                registration_form.setVisibility(View.VISIBLE);
                break;
        }
    }

    // ONCLICK VIEWS ===============================================================================

    public void login(View view) {
        refreshForms(LOGIN);
    }

    public void SignUp(View view) {
        refreshForms(REGISTER);
    }

    public void skipLogin(View view) {
        SharedPrefs.setToken(SharedPrefs.getTemporaryToken());
        SharedPrefs.setWelcomeTitle("Welcome");
        SharedPrefs.setWelcomeTitleDescription("Please login to access more features");
        helper.ToastMessage(LoginActivity.this, "Welcome to EatOut");
        finish();
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));


    }

    public void back(View view) {
        refreshForms(STARTUP);
    }

    public void registerBTN(View view) {
        if (register_first_name.getText().toString().length() < 1) {
            helper.myDialog(LoginActivity.this, "Registration Failure", "Field Required: First Name");
        } else if (register_last_name.getText().toString().length() < 1) {
            helper.myDialog(LoginActivity.this, "Registration Failure", "Field Required: Last Name");
        } else if (register_email.getText().toString().length() < 1) {
            helper.myDialog(LoginActivity.this, "Registration Failure", "Field Required: Email");
        } else if (!helper.validateEmail(register_email.getText().toString())) {
            helper.myDialog(LoginActivity.this, "Registration Failure", "Field Required: Invalid Email");
        } else if (register_password.getText().toString().length() < 8) {
            helper.myDialog(LoginActivity.this, "Registration Failure", "Field Required: Password must be 8 characters long");
        } else if (register_password_confirm.getText().toString().length() < 8) {
            helper.myDialog(LoginActivity.this, "Registration Failure", "Field Required: Confirm Password must be 8 characters long");
        } else if (!register_password.getText().toString().trim().equals(register_password_confirm.getText().toString().trim())) {
            helper.myDialog(LoginActivity.this, "Registration Failure", "Field Required: Confirm Password and Password do not match");
        } else if (!register_checkbox.isChecked()) {
            helper.myDialog(LoginActivity.this, "Registration Failure", "Field Required: Agree to terms and conditions");
        } else {
            asyncRegistration(
                    register_first_name.getText().toString(),
                    register_last_name.getText().toString(),
                    register_email.getText().toString(),
                    register_password.getText().toString(),
                    "",
                    "",
                    ""
            );
        }
    }

    public void termsAndConditions(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://eatout.co.ke/terms"));
        startActivity(intent);
    }

    public void ResetPassword(View view) {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_reset_password);
        dialog.setCancelable(false);

        final TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        final TextView txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);
        final EditText userEmail = (EditText) dialog.findViewById(R.id.userEmail);
        helper.setDefaultEditTextSelectionMode(userEmail);

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEmail.getText().toString().length() < 0) {
                    helper.ToastMessage(LoginActivity.this, "No email set");
                } else if (!helper.validateEmail(userEmail.getText().toString())) {
                    helper.ToastMessage(LoginActivity.this, "Email is not valid");
                } else {
                    dialog.cancel();
                    asyncResetPassword(userEmail.getText().toString());
                }
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();

    }

    // LOGIN FUNCTIONS =============================================================================
    private void asyncLogin(
            final String user_email,
            final String user_Password) {

        ETusername.setError(null);
        ETpassword.setError(null);

        helper.setProgressDialogMessage(getString(R.string.progress_loading_login));
        helper.progressDialog(true);

        LoginService loginService = LoginService.retrofit.create
                (LoginService.class);

        final Call<JsonObject> call = loginService.login(user_email, user_Password);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    Helpers.LogThis(response.body().toString());
                    String request_msg = main.getString("success");
                    if (request_msg.equals("true")) {
                        Helpers.LogThis(main.getString("token"));
                        SharedPrefs.setToken(main.getString("token"));
                        JSONObject user = main.getJSONObject("user");

                        SharedPrefs.setWelcomeTitle("");
                        SharedPrefs.setWelcomeTitleDescription("");

                        SharedPrefs.setUserFirstName(user.getString("first_name"));
                        SharedPrefs.setUserLastName(user.getString("last_name"));
                        SharedPrefs.setUSerID(user.getString("id"));
                        SharedPrefs.setUserEmail(user.getString("email"));
                        SharedPrefs.setUserPic(user.getString("thumbnail"));
                        SharedPrefs.setUserPhone(user.getString("phone"));
                        SharedPrefs.setUserDOB(user.getString("date_of_birth"));
                        SharedPrefs.setCountryCode(user.getString("country_code"));

                        setLoginSuccess(true);

                        finish();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                    } else {
                        setLoginSuccess(false);
                        helper.ToastMessage(LoginActivity.this, "Invalid login credentials, Please try again");
                    }
                } catch (JSONException e) {
                    helper.ToastMessage(LoginActivity.this, e.toString());
                    e.printStackTrace();
                    setLoginSuccess(false);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                helper.progressDialog(false);
                if (helper.validateInternetNotPresent()) {
                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.login_form),
                            getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
                    snackBar.show();
                } else {
                    helper.ToastMessage(LoginActivity.this, getString(R.string.error_500));
                }
                setLoginSuccess(false);

            }
        });

    }


    // REGISTRATION ================================================================================
    private void asyncRegistration(
            final String first_name,
            final String last_name,
            final String email,
            final String password,
            final String country_code,
            final String phone,
            final String fb_id
    ) {
        helper.setProgressDialogMessage(getString(R.string.progress_loading_register));
        helper.progressDialog(true);

        LoginService loginService = LoginService.retrofit.create
                (LoginService.class);

        Helpers.LogThis(first_name + " -- " +
                last_name + " -- " +
                email + " -- " +
                password + " -- " +
                country_code + " -- " +
                phone + " -- " +
                fb_id
        );
        final Call<JsonObject> call = loginService.register(
                first_name,
                last_name,
                email,
                password,
                country_code,
                phone,
                fb_id
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    Helpers.LogThis(response.body().toString());
                    String request_msg = main.getString("success");
                    if (request_msg.equals("true")) {

                        JSONObject user = main.getJSONObject("user");

                        SharedPrefs.setUserFirstName(user.getString("first_name"));
                        SharedPrefs.setUserLastName(user.getString("last_name"));

                        if (!user.getString("email").equals(DEFAULT_FACEBOOK_EMAIL))
                            SharedPrefs.setUserEmail(user.getString("email"));

                        SharedPrefs.setUserPic(user.getString("thumbnail"));

                        refreshForms(STARTUP);
                        helper.setProgressDialogMessage(getString(R.string.progress_loading_nice) + SharedPrefs.getUserFullName() + getString(R.string.progress_loading_account_setup));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setLoginSuccess(true);
                                helper.progressDialog(false);
                                asyncLogin(SharedPrefs.getUserEmail(), password);
                            }
                        }, 7500);


                    } else {
                        setLoginSuccess(false);
                        helper.progressDialog(false);
                        signOut();
                        helper.ToastMessage(LoginActivity.this, "Error, Email has already been taken");
                    }
                } catch (JSONException e) {
                    setLoginSuccess(false);
                    helper.progressDialog(false);
                    signOut();
                    helper.ToastMessage(LoginActivity.this, e.toString());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                helper.progressDialog(false);
                signOut();
                if (helper.validateInternetNotPresent()) {
                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.login_form),
                            getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
                    snackBar.show();
                } else {
                    helper.ToastMessage(LoginActivity.this, getString(R.string.error_500));
                }
                setLoginSuccess(false);
            }
        });


    }


    // FACEBOOK LOGIN ==============================================================================
    private void asyncGetFacebookUser() {

        helper.setProgressDialogMessage(getString(R.string.progress_loading_details_fetch));
        helper.progressDialog(true);

        LoginService loginService = LoginService.retrofit.create
                (LoginService.class);

        final Call<JsonObject> call = loginService.getFBUser(
                SharedPrefs.getUserFacebookID(),
                SharedPrefs.getUserEmail()
        );
//        final Call<JsonObject> call = loginService.getFBUser("793970695");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    Helpers.LogThis(response.body().toString());
                    String request_msg = main.getString("success");
                    if (request_msg.equals("true")) {

                        JSONObject user = main.getJSONObject("user");

                        SharedPrefs.setWelcomeTitle("");
                        SharedPrefs.setWelcomeTitleDescription("");

                        SharedPrefs.setToken(user.getString("token"));
                        SharedPrefs.setUserFirstName(user.getString("first_name"));
                        SharedPrefs.setUserLastName(user.getString("last_name"));
                        SharedPrefs.setUSerID(user.getString("id"));
                        SharedPrefs.setUserEmail(user.getString("email"));
                        SharedPrefs.setUserPic(user.getString("thumbnail"));
                        SharedPrefs.setUserPhone(user.getString("phone"));
                        SharedPrefs.setUserDOB(user.getString("date_of_birth"));
                        SharedPrefs.setCountryCode(user.getString("country_code"));

                        helper.ToastMessage(LoginActivity.this, "Successfully logged in with facebook");
                        setLoginSuccess(true);
                        finish();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                    } else {
//                        dialogSetUpPassword();
                        asyncRegistration(
                                SharedPrefs.getUserFirstName(),
                                SharedPrefs.getUserLastName(),
                                SharedPrefs.getUserEmail(),
                                helper.generateRandomString(),
                                SharedPrefs.getCountryCode(),
                                "",
                                SharedPrefs.getUserFacebookID()
                        );
                    }
                } catch (Exception e) {
                    signOut();
                    helper.ToastMessage(LoginActivity.this, e.toString());
                    e.printStackTrace();
                    setLoginSuccess(false);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                helper.progressDialog(false);
                signOut();
                if (helper.validateInternetNotPresent()) {
                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.login_form),
                            getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
                    snackBar.show();
                } else {
                    helper.ToastMessage(LoginActivity.this, getString(R.string.error_500));
                }
                setLoginSuccess(false);
            }
        });

    }


    // RESET PASSWORD ==============================================================================
    private void asyncResetPassword(final String email) {

        helper.setProgressDialogMessage(getString(R.string.progress_loading_password_reset));
        helper.progressDialog(true);

        LoginService loginService = LoginService.retrofit.create
                (LoginService.class);

        final Call<JsonObject> call = loginService.reset_password(email);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    Helpers.LogThis(response.body().toString());
                    String request_msg = main.getString("success");
                    if (request_msg.equals("true")) {
                        helper.myDialog(LoginActivity.this, "Success", "Please check your email for a password reset link");
                    } else {
                        helper.myDialog(LoginActivity.this, "Error", "This email is not registered");
                    }
                } catch (Exception e) {
                    signOut();
                    helper.ToastMessage(LoginActivity.this, e.toString());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                helper.progressDialog(false);
                signOut();
                if (helper.validateInternetNotPresent()) {
                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.login_form),
                            getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
                    snackBar.show();
                } else {
                    helper.ToastMessage(LoginActivity.this, getString(R.string.error_500));
                }
            }
        });


    }

    public void setLoginSuccess(boolean loginSuccess) {

    }
}

