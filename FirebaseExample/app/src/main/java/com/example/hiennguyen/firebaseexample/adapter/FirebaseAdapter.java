package com.example.hiennguyen.firebaseexample.adapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hiennguyen.firebaseexample.model.FoodDetail;
import com.example.hiennguyen.firebaseexample.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hiennguyen on 20/02/2017
 */
public class FirebaseAdapter extends RecyclerView.Adapter<FirebaseAdapter.ViewHolder> {
    private static final String TAG = FirebaseAdapter.class.getSimpleName();

    private List<FoodDetail> mData;

    public FirebaseAdapter(List<FoodDetail> mData) {
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FoodDetail foodDetails = mData.get(position);
        holder.txtName.setText(foodDetails.getName());

        Glide.with(holder.itemView.getContext()).load(foodDetails.getImage()).into(holder.mImage);

        if (foodDetails.isCompleted()) {
            holder.mCbComplete.setChecked(true);
            holder.mCbComplete.setEnabled(false);
        }

        holder.mCbComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    holder.mCbComplete.setEnabled(false);
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("groceryItems").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                FoodDetail foods = snapshot.getValue(FoodDetail.class);

                                if (foodDetails.getName().equals(foods.getName())) {
                                    Log.e(TAG, "onDataChange: " + foods.getAddedByUser());
                                    snapshot.getRef().child("completed").setValue(true);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_name)
        TextView txtName;

        @BindView(R.id.img_image)
        ImageView mImage;

        @BindView(R.id.cb_complete)
        CheckBox mCbComplete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
