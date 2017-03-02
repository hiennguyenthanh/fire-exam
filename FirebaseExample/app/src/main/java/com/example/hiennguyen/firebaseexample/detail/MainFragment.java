package com.example.hiennguyen.firebaseexample.detail;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hiennguyen.firebaseexample.model.User;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.example.hiennguyen.firebaseexample.R;
import com.example.hiennguyen.firebaseexample.adapter.FirebaseAdapter;
import com.example.hiennguyen.firebaseexample.adapter.OnGetImageStorage;
import com.example.hiennguyen.firebaseexample.authen.AuthActivity;
import com.example.hiennguyen.firebaseexample.model.FoodDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.btn_logout)
    Button mBtnLogout;

    @BindView(R.id.btn_invite)
    Button mBtnInvite;

    @BindView(R.id.img_avatar)
    CircleImageView mImageAvatar;

    @BindView(R.id.txt_username)
    TextView mTxtUserName;

    private List<String> mData;
    private List<FoodDetail> mFoodDetails;

    private Unbinder mUnbind;
    private DatabaseReference mDatabase;
    private FirebaseAdapter mAdapter;
    private FirebaseAuth auth;
    private ProgressDialog progressBar;
    private StorageReference mStorageRef;
    private FirebaseStorage mFirebaseStorage;
    private OnGetImageStorage onListener;

    public void setOnListener(OnGetImageStorage onListener) {
        this.onListener = onListener;
    }

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mUnbind = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        mFoodDetails = new ArrayList<>();
        progressBar = new ProgressDialog(getContext());
        progressBar.setMessage("Please wail...");
        progressBar.show();

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        updateDataChange();
        updateInfoUser();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = mFirebaseStorage.getReference();

        initSwipe();

        return view;
    }

    @OnClick({R.id.btn_logout, R.id.btn_invite})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_logout:
                auth.signOut();

                auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user == null && getActivity() != null) {
                            Intent intent = new Intent(getContext(), AuthActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                });
                break;
            case R.id.btn_invite:
                onInviteClicked();
                break;
        }

    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder("Invite using app")
                .setMessage("Please using this app!!!!!")
//                .setDeepLink(Uri.parse("https://ewyc6.app.goo.gl/eNh4"))
//                .setCallToActionText("INVITATION_CALL_TO_ACTION")
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    private final int REQUEST_INVITE = 124;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode + "result_ok ="+RESULT_OK);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {

                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                StringBuilder sb = new StringBuilder();
                sb.append("Sent ").append(Integer.toString(ids.length)).append(" invitations: ");
                for (String id : ids) sb.append("[").append(id).append("]");
                Toast.makeText(getContext(),"Invited!!!",Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(getContext(),"Sorry, unable to send invite.",Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Add Data!");
                alertDialog.setMessage("Add an item");

                final EditText input = new EditText(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(layoutParams);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String item = input.getText().toString();
                        if (item.equals(""))
                            return;

                        uploadFileLocal();

                        onListener = new OnGetImageStorage() {
                            @Override
                            public void onGetImage(Uri uri) {
                                FoodDetail foodDetail = new FoodDetail("test@test.com", false, item, uri.toString());
                                mDatabase.child("groceryItems").child(item).setValue(foodDetail);
                            }
                        };

                        setOnListener(onListener);
                    }
                });

                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateDataChange() {
        mDatabase.child("groceryItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFoodDetails.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FoodDetail foods = snapshot.getValue(FoodDetail.class);
                    Log.e(TAG, "onDataChange: " + foods.getAddedByUser());
                    mFoodDetails.add(foods);
                }
                mAdapter = new FirebaseAdapter(mFoodDetails);
                mAdapter.notifyDataSetChanged();

                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(mAdapter);
                Log.e(TAG, "onCreateView: " + mFoodDetails.size());
                progressBar.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateInfoUser() {
        FirebaseUser userFirebase = auth.getCurrentUser();
        mDatabase.child("users").child(userFirebase.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    Log.e(TAG, "onDataChange1: " + user.getUserName());
                    mTxtUserName.setText(user.getUserName());

                    if (user.getProfileUrl() == null) {
                        return;
                    }
                    Glide.with(getContext()).load(user.getProfileUrl()).into(mImageAvatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                final FoodDetail foodDetails = mFoodDetails.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    Toast.makeText(getContext(), "position: " + position, Toast.LENGTH_SHORT).show();

                    mDatabase.child("groceryItems").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                FoodDetail foods = snapshot.getValue(FoodDetail.class);

                                if (foodDetails.getName().equals(foods.getName())) {
                                    Log.e(TAG, "onDataChange: " + foods.getAddedByUser());
                                    snapshot.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }


    public void uploadFileLocal() {
            Uri uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.ando);
            StorageReference storageReference = mStorageRef.child("images/" + uri.getLastPathSegment());

            storageReference.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri uri = taskSnapshot.getDownloadUrl();
                            Log.e(TAG, "onSuccess: " + uri);
                            if (onListener != null) {
                                onListener.onGetImage(uri);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: " + e.getMessage());
                        }
                    });
    }

    private List<String> getData() {
        mData = new ArrayList<>();
        mData.add("A");
        mData.add("B");
        mData.add("C");
        mData.add("D");
        mData.add("E");

        Log.e(TAG, "getData: " + mData);
        return mData;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbind.unbind();
    }
}
