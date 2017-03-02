package com.example.hiennguyen.firebaseexample.service.fcm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.hiennguyen.firebaseexample.utilities.PreferenceUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = FirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        saveTokenToPreference(refreshedToken);
    }
    // [END refresh_token]

    private void saveTokenToPreference(String token) {
        PreferenceUtil.saveFCMToken(token);
    }
}
