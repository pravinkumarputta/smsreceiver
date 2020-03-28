package com.pravinkumarputta.android.smsreceiverdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.pravinkumarputta.android.smsreceiver.SMSBroadcastReceiver
import com.pravinkumarputta.android.smsreceiver.SMSReceiver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SMSBroadcastReceiver.OTPReceiveListener {
    private var smsReceiver: SMSReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        smsReceiver = SMSReceiver(this, this)
        tvOtpHashCode.text = SMSReceiver.getHashKey(this)
        tvOtpSampleMessage.text =
            "<#> One time password is 12345678\n${SMSReceiver.getHashKey(this)}"

        btSubmit.setOnClickListener {
            btSubmit.isEnabled = false
            smsReceiver!!.startSmsListener()
        }

        btRequestPhoneNumber.setOnClickListener {
            SMSReceiver.requestForPhoneNumber(this)
        }
    }

    override fun onSMSReceiverStarted() {
        btSubmit.isEnabled = true
        tvOtpResponse.text = "Waiting for the OTP"
    }

    override fun onSMSReceiverFailed(exception: Exception?) {
        btSubmit.isEnabled = true
        tvOtpResponse.text = "Failed to Start SMS Retriever"
    }

    override fun onSMSReceived(message: String?) {
        btSubmit.isEnabled = true
        tvOtpResponse.text = message
    }

    override fun onSMSReceiverTimeOut() {
        btSubmit.isEnabled = true
        tvOtpResponse.text = "Otp receiver is expired"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val phoneNumber = SMSReceiver.getPhoneNumberFromResult(requestCode, resultCode, data)
        if (phoneNumber != null) {
            tvPhoneNumber.text = phoneNumber
        }
    }
}
