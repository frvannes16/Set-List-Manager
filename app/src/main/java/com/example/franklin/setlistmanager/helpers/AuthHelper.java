package com.example.franklin.setlistmanager.helpers;

import android.content.Context;
import android.content.Intent;

import com.example.franklin.setlistmanager.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Franklin on 10/26/2016.
 */

public class AuthHelper {

    public static boolean logout(Context context) {
        // Sign out
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        // load the login activity.
        // TODO: finish all activites on logout as per http://stackoverflow.com/a/3008684/3769032
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        return true;
    }
}
