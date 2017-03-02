package com.example.hiennguyen.firebaseexample.authen;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hiennguyen.firebaseexample.detail.MainActivity;
import com.example.hiennguyen.firebaseexample.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class AuthFragment extends Fragment {
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

    private Unbinder mUnbind;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog progressDialog;

    public AuthFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth, container, false);
        mUnbind = ButterKnife.bind(this, view);
        progressDialog = new ProgressDialog(getContext());

        mFirebaseAuth = FirebaseAuth.getInstance();

        if (mFirebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        return view;
    }

    @OnClick({R.id.btn_login, R.id.btn_fotgot_password, R.id.btn_registration})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                String email = mInputEmail.getText().toString();
                String password = mInputPassword.getText().toString();
                progressDialog.setMessage("Please wail...");
                progressDialog.show();

                mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbind.unbind();
    }
}
