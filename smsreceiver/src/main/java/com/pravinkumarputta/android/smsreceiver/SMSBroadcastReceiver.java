package com.pravinkumarputta.android.smsreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    private static OTPReceiveListener otpReceiver;

    public static void initOTPListener(OTPReceiveListener receiver) {
        otpReceiver = receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                return;
            }
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            if (status == null) {
                return;
            }
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String otp = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    Log.d("OTP_Message", otp);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server for SMS authenticity.
                    // But here we are just passing it to MainActivity
                    if (otpReceiver != null) {
//                        otp = otp.replace("<#> Your ExampleApp code is: ", "").split("\n").dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        otpReceiver.onSMSReceived(otp.replaceFirst("<#>", "").replace(SMSReceiver.getHashKey(context), "").trim());
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    if (otpReceiver != null) {
                        otpReceiver.onSMSReceiverTimeOut();
                    }
                    break;
            }
        }
    }

    public interface OTPReceiveListener {
        void onSMSReceiverStarted();

        void onSMSReceiverFailed(Exception exception);

        void onSMSReceived(String message);

        void onSMSReceiverTimeOut();
    }
}
