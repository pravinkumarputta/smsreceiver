package com.pravinkumarputta.android.smsreceiver;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class SMSReceiver {
    private static final String TAG = "SMSReceiver";
    private static final int RESOLVE_HINT = 1234;
    private static GoogleApiClient apiClient;
    private SMSBroadcastReceiver.OTPReceiveListener onSMSReceiverCallback;
    private Context context;

    public SMSReceiver(Context context, SMSBroadcastReceiver.OTPReceiveListener onSMSReceiverCallback) {
        super();
        this.context = context;
        this.onSMSReceiverCallback = onSMSReceiverCallback;

        SMSBroadcastReceiver.initOTPListener(onSMSReceiverCallback);
    }

    public static String getHashKey(Context context) {
        AppSignatureHelper appSignature = new AppSignatureHelper(context);
        return appSignature.getAppSignatures().get(0);
    }

    public static void requestForPhoneNumber(Activity activity) {
        try {
            if (apiClient == null) {
                apiClient = new GoogleApiClient.Builder(activity)
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                Log.d(TAG, "onConnected: ");
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                Log.d(TAG, "onConnectionSuspended: ");
                            }
                        })
                        .enableAutoManage((FragmentActivity) activity, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            }
                        })
                        .addApi(Auth.CREDENTIALS_API)
                        .build();
            }

            HintRequest hintRequest = new HintRequest.Builder()
                    .setPhoneNumberIdentifierSupported(true)
                    .build();

            PendingIntent hintPickerIntent = Auth.CredentialsApi.getHintPickerIntent(apiClient, hintRequest);
            activity.startIntentSenderForResult(hintPickerIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    public static String getPhoneNumberFromResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                return credential.getId();
            }
        }
        return null;
    }

    public void startSmsListener() {
        SmsRetrieverClient client = SmsRetriever.getClient(context /* context */);
        Task task = client.startSmsRetriever();
        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                onSMSReceiverCallback.onSMSReceiverStarted();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onSMSReceiverCallback.onSMSReceiverFailed(e);
            }
        });
    }
}
