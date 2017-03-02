package com.example.hiennguyen.firebaseexample.authen;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import android.widget.Toast;

import com.example.hiennguyen.firebaseexample.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForgotPassFragment extends Fragment {
    @BindView(R.id.ed_input_email)
    EditText mInputEmail;

    @BindView(R.id.btn_reset_password)
    Button btnResetPass;

    @BindView(R.id.btn_back)
    Button btnBack;

    private FirebaseAuth auth;
    private Unbinder mUnbind;
    private ProgressDialog progressDialog;

    public ForgotPassFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_pass, container, false);
        mUnbind = ButterKnife.bind(this, view);
        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getContext());

        return view;
    }

    @OnClick({R.id.btn_reset_password, R.id.btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reset_password:
                String email = mInputEmail.getText().toString();
                progressDialog.setMessage("Please wail...");
                progressDialog.show();
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Please following link that we have send to you to reset password", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed reset password", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
                break;
            case R.id.btn_back:
                Intent intent = new Intent(getContext(), AuthActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbind.unbind();
    }
}
