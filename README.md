# SMS Receiver with SMS Retriever API
This is simple library for receiving sms in android with new SMS Retriever API.

[![](https://jitpack.io/v/pravinkumarputta/smsreceiver.svg)](https://jitpack.io/#pravinkumarputta/smsreceiver)


# Usage
### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
```
### Step 2. Add the dependency
```
dependencies {
	implementation 'com.github.pravinkumarputta:smsreceiver:0.1.0'
}
```
### Step 3. Instantiate SMSReceiver
```
SMSBroadcastReceiver.OTPReceiveListener smsReceiverCallback = new SMSBroadcastReceiver.OTPReceiveListener() {
	@Override
	public void onSMSReceiverStarted() {

	}

	@Override
	public void onSMSReceiverFailed(Exception exception) {

	}

	@Override
	public void onSMSReceived(String message) {

	}

	@Override
	public void onSMSReceiverTimeOut() {

	}
};

SMSReceiver smsReceiver = SMSReceiver(activity, smsReceiverCallback)

### Step 3. Call startSmsListener() method to start receiving
```
smsReceiver.startSmsListener() // It stops receiving after one message received
```
