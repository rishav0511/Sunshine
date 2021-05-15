package com.example.sunshine.sync;

import android.content.Context;
import android.content.Intent;

public class SunshineSyncUtils {
    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(final Context context) {
        Intent intentToSyncImmediately = new Intent(context,SunshineSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
