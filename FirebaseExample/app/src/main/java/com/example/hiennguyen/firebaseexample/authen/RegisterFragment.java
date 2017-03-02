package com.example.hiennguyen.firebaseexample.authen;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
import android.widget.Toast;

import com.example.hiennguyen.firebaseexample.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.google.android.gms.internal.zzt.TAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class RegisterFragment extends Fragment {
    @BindView(R.id.ed_input_email)
    EditText mInputEmail;

    @BindView(R.id.ed_input_password)
    EditText mInputPass;

    @BindView(R.id.btn_sign_up)
    Button mBtnSignUp;

    private Unbinder mUnbind;
    private FirebaseAuth mFirebaseAuth;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mUnbind = ButterKnife.bind(this, view);
        mFirebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    @OnClick(R.id.btn_sign_up)
    public void onClick() {
        String email = mInputEmail.getText().toString().trim();
        String password = mInputPass.getText().toString().trim();

        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getContext(), AuthActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onComplete: " + task.getException().getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbind.unbind();
    }
}
