package com.example.hiennguyen.firebaseexample.authen;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hiennguyen.firebaseexample.detail.MainActivity;
import com.example.hiennguyen.firebaseexample.R;
import com.example.hiennguyen.firebaseexample.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.google.android.gms.internal.zzt.TAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class AuthFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.ed_input_email)
    EditText mInputEmail;

    @BindView(R.id.ed_input_password)
    EditText mInputPassword;

    @BindView(R.id.btn_login)
    Button mBtnLogin;

    @BindView(R.id.btn_registration)
    Button mBtnRegister;

    @BindView(R.id.btn_fotgot_password)
    TextView mBtnForgotPass;

    @BindView(R.id.sign_in_button)
    SignInButton mBtnGoogle;

    private Unbinder mUnbind;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog progressDialog;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient mGoogleApiClient;
    private final int RC_SIGN_IN = 123;
    private DatabaseReference mDatabase;

    public AuthFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth, container, false);
        mUnbind = ButterKnife.bind(this, view);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wail...");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();


        return view;
    }

    @OnClick({R.id.btn_login, R.id.btn_fotgot_password, R.id.btn_registration, R.id.sign_in_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                String email = mInputEmail.getText().toString();
                String password = mInputPassword.getText().toString();
                progressDialog.show();

                //login with email and password
                mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            for (UserInfo userInfo : task.getResult().getUser().getProviderData()) {
                                if (userInfo.getProviderId().equals("google.com")) {
                                    Log.e(TAG, "onComplete: " + userInfo.getDisplayName() + ", " + userInfo.getUid() + ", " + userInfo.getPhotoUrl() + ", " + userInfo.getProviderId());
                                    User user = new User(userInfo.getDisplayName(), userInfo.getPhotoUrl().toString());
                                    mDatabase.child("users").child(task.getResult().getUser().getUid()).setValue(user);
                                }
                            }
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
                break;
            case R.id.btn_fotgot_password:
                Intent intent = new Intent(getContext(), ForgotPassActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.btn_registration:
                Intent intent1 = new Intent(getContext(), RegisterActivity.class);
                startActivity(intent1);
                getActivity().finish();
                break;
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                progressDialog.show();
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "erroooro: " + result.getSignInAccount().getDisplayName());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        for (UserInfo userInfo : task.getResult().getUser().getProviderData()) {
                            if (userInfo.getProviderId().equals("google.com")) {
                                Log.e(TAG, "onComplete: " + userInfo.getDisplayName() + ", " + userInfo.getUid() + ", " + userInfo.getPhotoUrl() + ", " + userInfo.getProviderId());
                                User user = new User(userInfo.getDisplayName(), userInfo.getPhotoUrl().toString());
                                mDatabase.child("users").child(task.getResult().getUser().getUid()).setValue(user);
                            }
                        }

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.dismiss();

                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbind.unbind();
    }
}
