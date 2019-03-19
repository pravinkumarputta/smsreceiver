package com.pravinkumarputta.android.smsreceiver;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class SMSReceiver {
    private Activity activity;
    private SMSBroadcastReceiver.OTPReceiveListener onSMSReceiverCallback;
    private GoogleApiClient apiClient;
    public static String hashKey = "";

    public SMSReceiver(Activity activity, SMSBroadcastReceiver.OTPReceiveListener onSMSReceiverCallback) {
        super();
        this.activity = activity;
        this.onSMSReceiverCallback = onSMSReceiverCallback;
        AppSignatureHelper appSignature =new  AppSignatureHelper(activity);
        hashKey = appSignature.getAppSignatures().get(0);

        apiClient =new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .enableAutoManage((FragmentActivity) activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        SMSReceiver.this.onSMSReceiverCallback.onSMSReceiverFailed(new Exception(connectionResult.getErrorMessage()));
                    }
                })
                .addApi(Auth.CREDENTIALS_API)
                .build();

//        startSmsListener();

        SMSBroadcastReceiver.initOTPListener(onSMSReceiverCallback);
    }

    public void startSmsListener(){
        SmsRetrieverClient client = SmsRetriever.getClient(activity /* context */);
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
