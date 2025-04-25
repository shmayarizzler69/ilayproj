package com.example.myapplication.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * Helper class to manage notification permissions for Android 13 and above.
 */
public class NPH {

    /**
     * Checks if the app has the POST_NOTIFICATIONS permission.
     *
     * @param context The context to check the permission for.
     * @return True if the permission is granted, false otherwise.
     */
    public static boolean hasNP(@NonNull Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Permissions are not required for Android 12 or below.
    }

    /**
     * Requests the POST_NOTIFICATIONS permission if running on Android 13 or above.
     *
     * @param launcher The ActivityResultLauncher used to request permissions.
     */
    public static void requestNP(@NonNull ActivityResultLauncher<String> launcher) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
