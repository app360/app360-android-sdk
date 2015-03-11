<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
 

- [Introduction](#introduction)
- [Getting started with Demo project](#getting-started-with-demo-project)
- [Add App360 SDK to your project](#add-app360-sdk-to-your-project)
- [Integrate your application with App360 SDK](#integrate-your-application-with-app360-sdk)
  - [Documentation](#documentation)
  - [Application Id & secret](#application-id-&-secret)
  - [AndroidManifest](#androidmanifest)
  - [Assets](#assets)
  - [Other SDKs](#other-sdks)
- [App-scoped ID](#app-scoped-id)
  - [SDK Initialization](#sdk-initialization)
  - [Session initialization](#session-initialization)
    - [Note on channeling](#note-on-channeling)
  - [Linking app-scoped ID with Facebook/Google](#linking-app-scoped-id-with-facebookgoogle)
- [Payment](#payment)
  - [Payment flow](#payment-flow)
  - [Using payment form UI](#using-payment-form-ui)
  - [Using request classes](#using-request-classes)
  - [Checking transaction status](#checking-transaction-status)
- [Release Notes](#release-notes)
  - [Version 1.0.0](#version-100)
- [Known Issues](#known-issues)
- [FAQ](#faq)
- [Support](#support)
  - [For a technical issue](#for-a-technical-issue)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

#Introduction

App360SDK provides easiest way to integrate user management and payment methods (including sms, phone card and e-banking) into your application.

The App360 SDK supports Android version 4.0 and above.

App360 SDK 's Integration Flow

![Integration Flow](http://i.imgur.com/PXdCmb3.png)

# Getting started with Demo project

Firstly, clone or download this repository to your machine.

- `git clone https://github.com/app360/app360-android-sdk.git`
- Or, download from https://github.com/app360/app360-android-sdk/releases

The demo project is compatible with latest versions of both Android Studio and Eclipse. To import the project into Android Studio, close the current project, choose _Open an existing Android Studio project_ then choose the repository you've just downloaded/cloned.

![Android Studio import](http://i.imgur.com/yLBQ2lP.png)

To import the project into Eclipse, choose _File > Import..._, then inside the _Import_ dialog, choose _General > Existing projects into workspace_, then in _Select root directory_, browse to the repository. **Select all** projects that Eclipse detected, then click _Finish_. There're a lot of library projects, but they're necessary to support the demo (including Google Play Services for Google login, Facebook SDK for Facebook login, Android support libraries and some UI libraries).

![Eclipse import 1](http://i.imgur.com/ryT1Bqk.png)
![Eclipse import 2](http://i.imgur.com/LvXZzG4.png)

Open LoginActivity inside demo project, find `App360.initialize("appId", "appSecret", ...)` line and replace the placeholders with your application credentials.

Run the project. The app demonstrates capability of App360 SDK, including app-scoped ID and payment.

# Add App360 SDK to your project

To import the SDK into your Eclipse project, repeat the same steps for importing demo project, except that in the last step, just select `app360sdk` project. Right-click on your project, choose _Properties_, inside _Android > Library_, add `app360sdk` as a library project dependency.

To import the SDK into Android Studio, follows these steps:

1. Choose _File > Import Module..._, then browse to `app360sdk` directory inside the repository. Click _Finish_ and wait until the import is completed.
   ![AS import 1](http://i.imgur.com/62OtAPu.png)
2. Open _File > Project Structure..._, select your application module, select _Dependencies_ tab, press the plus (+) symbol in the top-right corner, select _Module dependency_, then select `app360sdk`.
   ![AS import 2](http://i.imgur.com/7GX9wjD.png)

#Integrate your application with App360 SDK

## Documentation
- Getting Started Guide: this README
- Online Javadoc: https://docs.app360.vn/javadoc/
- Javadoc archive: `app360sdk/app360sdk-javadoc.jar` inside this repository

##Application Id & secret

To using any function of App360SDK, you need to configure application id and secret. SDK uses this pair of key to authorize your app (game) with SDK's server. To retrieve application ID and client secret, register your application on https://developers.app360.vn/; your application ID and secret is available in application details page, _Information_ tab.

![App credentials](http://i.imgur.com/4xZ8fYc.png)

## AndroidManifest

App360 SDK needs the following permissions:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<!-- Remove if SMS payment is not needed -->
<uses-permission android:name="android.permission.SEND_SMS" />
```

## Assets

In order to support channeling, the application's `assets` directory should contain a properties file named `m360.properties`. The file should contain two keys `channel` and `sub_channel`. `channel` is distribution channel such as `mwork`, `appota` while `sub_channel` is arbitrary string defined by the distribution channel itself.

## Other SDKs

App360 SDK supports login via Facebook/Google account, but in order to maximize flexibility, acquiring user's access token from Facebook/Google is the responsibility of the application. The SDK only accepts the acquired  access tokens, therefore the application developers should consult respective manuals from Facebook and Google, which could be found at:
- https://developers.facebook.com/docs/android/getting-started
- https://developers.facebook.com/docs/android/login-with-facebook/v2.2
- http://developer.android.com/google/auth/http-auth.html

# App-scoped ID

App-scoped ID is App360's identification service for end-user of applications that integrate with our SDK. App-scoped IDs generated by an app is only valid for that app (hence _app-scoped_). Different applications cannot access each other's user and data.

App-scoped ID SDK provides the following functions:
- Sign-up/Login anonymously
- Sign-up/Login via Facebook/Google
- Link anonymous app-scoped ID with Facebook/Google account

**Note**: almost all App360 APIs require the existence of an app-scoped ID.

## SDK Initialization

The SDK could be initialized via `App360.initialize()`. Initialization should be done on main activity's `onCreate`.

```java
/**
  * Initialize the SDK
  *
  * @param appId     application ID (retrieve from developer dashboard)
  * @param appSecret application secret (retrieve from developer dashboard)
  * @param ctx       the application {@link android.content.Context} that will be re-used in
  *                  various SDK's function (so you won't have to specified it again and again)
  * @param listener  listener for initialization events
  */
public static void initialize(String appId, String appSecret, Context ctx, InitListener listener) {
```

`appId` và `appSecret` could be acquired from [App360 Developers](https://developers.app360.vn). `ctx` is the application context, e.g. `Activity.getApplicationContext()`. `listener` is an instance of `InitListener` interface, defined as

```java
public interface InitListener {
    public void onSuccess();

    public void onFailure(Exception e);
}
```

During initialization, the SDK will try to find and re-opened any cached app-scoped ID session. If the application hasn't opened any session, `SessionManager.getCurrentSession()` will return null and the application should open a new session.

```java
M360SDK.initialize("appID", "appSecret", getApplicationContext(), new InitListener() {
    @Override
    public void onSuccess() {
        SessionService.Session session = SessionManager.getCurrentSession();
        if (session == null) { // no cached valid session
			SessionManager.createSession(null, new SessionCallback());
        } else {
            Log.d(TAG, "Current session: " + session);
        }
    }

    @Override
    public void onFailure(Exception e) {
            Log.e(TAG, "Initialization error", e);
    }
});
```

## Session initialization

App-scoped ID is logged in via creating a new session for such ID. There are two ways to open a session (both via `SessionManager` class):

- Anonymously
```java
    public static void createSession(String scopedId, SessionCallback callback);
```
- With Facebook/Google access token
```java
public static void createSession(String service, String token, SessionCallback callback);
```

When session is created anonymously and `scopedId` exists, a new session will be created for that app-scoped ID. If `scopedId` doesn't exist, a new app-scoped ID with the same `scopedId` will be created first. If `scopedId` is `null`, it will be generated randomly. 

Note that there's no authentication mechanism (password, token, etc) for anonymous login. This login type should be used when the application has already implemented its own authentication mechanism (in this case, `scopedId` should be set to app's username, for example); or when the application doesn't require the user to login immediately. Anonymously logged-in user could later be linked with Facebook/Google account for better security and portability (e.g. login the same app-scoped ID on different devices).

When session is created via Facebook/Google access token, the SDK will verify the token with Facebook/Google servers then try to find an existing app-scoped ID that is already linked with the corresponding social account. If there's no such ID, a new one will be generated randomly then link with the account and its session is returned. Otherwise, a new session is generated for the existing app-scoped ID. In this way, the authentication mechanism is provided by Facebook/Google.

The application receives initialized session via `SessionCallback` interface:

```java
public static interface SessionCallback {
    void onSuccess();

    void onFailure(Exception e);
}
```

Inside `onSuccess()` callback, the application could access current session via `SessionManager.getCurrentSession()` and current scoped ID via `ScopedUser.getCurrentUser()`. Example:

```java
private class SessionCallback implements SessionManager.SessionCallback {
    @Override
    public void onSuccess() {
        SessionService.Session session = SessionManager.getCurrentSession();
        Log.d(TAG, "Current session: " + session);

        ScopedUser currentUser = ScopedUser.getCurrentUser();
        Log.d(TAG, "Current user: " + currentUser);
    }

    @Override
    public void onFailure(Exception e) {
        Log.e(TAG, "onFailure", e);
    }
}
```

### Note on channeling

Channeling is done per app-scoped ID. In other words, the channel of a payment equals to the channel of the app-scoped user that orders such payment. Moreover, an app-scoped user's channel is decided at _creation_ and equals to the channel specified in `m360.properties` of the build that creates it. So:

- If you want channeling be done on user level, you should synchronize your app's users with app-scoped ID (i.e. set your app's user ID as `scopedId` in `public static void createSession(String scopedId, SessionCallback callback);`
- If you want channeling be done on device level, you should set device ID as `scopedId` in `public static void createSession(String scopedId, SessionCallback callback);`

## Linking app-scoped ID with Facebook/Google

To link current app-scoped ID with Facebook/Google, call  `public void linkFacebook(String token, SaveCallback cb)` or `public void linkGoogle(String token, SaveCallback cb)`

Example:
```java
ScopedUser.getCurrentUser().linkFacebook(accessToken, new SaveCallback() {
    @Override
    public void onSuccess() {
        Log.d(TAG, "Link succeeded");
    }

    @Override
    public void onFailure(Exception e) {
        Log.d(TAG, "Link failed");
    }
});
```

# Payment


## Payment flow

In order to secure payment flow, your application might choose to integrate with our SDK on both client-side and server-side, in which case the payment flow is depict in the following diagram:

![Payment flow](http://www.websequencediagrams.com/cgi-bin/cdraw?lz=dGl0bGUgUGF5bWVudCBzZXF1ZW5jZQoKR2FtZS0-U0RLOgAUCXJlcXVlc3QgKDEpClNESy0-R2FtZTogVHJhbnNhY3Rpb24gaWQsIHN0YXR1cywgYW1vdW50ICgyKQBGB0dhbWUgc2VydmVyOiBzZW5kIHQAMAtkYXRhIGZvciBpbnNwZQBJBigzAF8LADIJACoMaWQAYQgsIHVzZXJfaWQgKDQAbAYAZQcAgT8HYWNrIChIVFRQAIEaByAyMDApICg1AB0PAIEYDWNvbmZpcm0gKDYAExMAFAs3KQ&s=rose)

1. The application (client-side) calls payment API from the SDK, optionally with a custom payload (documented below).
2. The SDK returns transaction id and other details (if available)
3. The application client sends transaction data to its server awaiting confirmation
4. SDK server calls a pre-registered endpoint of the application server to notify about transaction status when it completes
    - Note that there is no guarantee about the order of (3) and (4) (i.e. (4) may happen before (3))
5. Application server acknowledges SDK server's call by responding with HTTP status code 200.
6. Application server validates the transaction based on the information it has (transaction ID, payload, etc.)
7. Application server confirms/notifies game client about the status of the transaction

**Note**:
- To register your application's server endpoint, go to https://developers.app360.vn/; set _Payment callback endpoint_ in application details page, _Information_ tab.
- Before using any payment methods, the application must first initialize the SDK. See section _SDK Initialization_ above.

There are two methods to integrate payment functionality of App360 SDK into your application:
- Using the SDK's existing UI
- Implement your own UI and using the SDK's basic payment request classes.

## Using payment form UI

To use payment form UI, you need to add the following activities to your manifest (if manifestmerger is diabled):

```xml
<activity android:name="vn.mog.m360.sdk.payment.activities.CardActivity" />
<activity android:name="vn.mog.m360.sdk.payment.activities.BankActivity" />
<activity android:name="vn.mog.m360.sdk.payment.activities.SmsActivity" />
<activity android:name="vn.mog.m360.sdk.payment.activities.BankWebviewActivity"/>
<activity android:name="vn.mog.m360.sdk.payment.activities.PaymentFormActivity" />
```

To display the payment UI, build a `PaymentForm` via a `PaymentForm.Builder`. For example:

```java
paymentForm = new PaymentForm.Builder(this)
                .setListener(listener)
                .setPayload("payload")
                .setSMSAmounts(Const.SmsAmount.AMOUNT_15000, Const.SmsAmount.AMOUNT_500)
                .setBankAmounts(50000, 100000, 200000)
                .setCardAmounts(10000, 15000, 20000)
                .setConverter(converter)
                .setAppDescription("Chúc mừng năm mới")
                .build();
```
Please refer to javadoc and demo project for the meaning of each parameter. You don't need to set every parameter, but only the one needed for the payment methods that you application uses. For example, if you don't use SMS payment, you don't have to use `setSMSAmount`.

After building, call `paymentForm.showCardForm()`, `paymentForm.showBankForm()`, `paymentForm.showSmsForm()` and `paymentForm.showPaymentForm()` to display respective payment UI. `showPaymentForm()` will display an UI which allows users to choose between all available payment methods.

![](http://i.imgur.com/ooSBqnf.png)![Imgur](http://i.imgur.com/npicbmU.png)
![Imgur](http://i.imgur.com/62lJjpJ.png)![Imgur](http://i.imgur.com/QvJS792.png)

In order to receive callback when payment finishes, implement `PaymentFormListener` and provide the instance to `PaymentForm.Builder#setListener`.  The interface is defined as follows:

```java
public interface PaymentFormListener {
    public void onFinish(Transaction transaction);
    public void onCancel();
    public void onError(Throwable e);
}
```

Note that `onFinish` only means that the user has finished placing a payment request, but the transaction itself might not be finished yet. For example, the user might has sent an SMS but our backend has not received it yet. In order to inquire the status of the transaction, use `StatusRequest` (documented later).

## Using request classes

To make a card charging request:

```java
CardRequest cardRequest = new CardRequest.Builder()
                .setCardCode("CARD_CODE/PIN")
                .setCardSerial("CARD_SERIAL")
                .setCardVendor(CardTransaction.CardVendor.VIETTEL)
                .setPayload("payload")
                .setListener(new MyRequestListener())
                .build();
cardRequest.execute();
```

To make an SMS charging request:
```java
SmsRequest smsRequest = new SmsRequest.Builder()
                .setAmounts(Const.SmsAmount.AMOUNT_1000, Const.SmsAmount.AMOUNT_15000)
                .setPayload("payload")
                .setListener(new MyRequestListener())
                .build();
smsRequest.execute();
```

To make a bank charging request:
```java
BankRequest bankRequest = new BankRequest.Builder()
                .setAmount(25000)
                .setPayload("payload")
                .setListener(new MyRequestListener())
                .build();
bankRequest.execute();
```

## Checking transaction status

To check the status of a transaction, use `StatusRequest` as follows:

```java
StatusRequest statusRequest = new StatusRequest.Builder()
                .setListener(statusListener)
                .setTransactionId(transId)
                .build();
statusRequest.execute();
```

#Release Notes

##Version 1.0.0

**Release date**: 02 Feb 2015

- Support user management via app-scoped ID
 - Support charging via phone card, SMS and e-banking
 - Support checking transaction status

#Known Issues

There's no known issues for now

#FAQ

**What is a application id and client key?**

They are a pair of key, used to authorize your app (game) with SDK's server.

**How can i get my application id and client key?**

1. Goto https://developers.app360.vn
2. Login if you already have an account or register a new one
3. Open your application in App360 dashboard, select Information tab
4. All key you need will be there

#Support
Please contact [us](mailto:support@app360.vn) for general inquiries.

##For a technical issue
In case you have a technical issue, you can reach [our technical support team](mailto:support@app360.vn).
Please provide the following information when you reach out, it'll allow us to help you much more quickly.

 - **The library version** you're using. You can get the precise number by
   printing the result of the `App360SDK.getVersion();` method.
 - **The platform** used to produce the problem (device model or simulator),
   device model, and the android version.
 - **The steps** to reproduce the problem.
 - If possible, **some pieces of code**, or even a project.

> For more information, please go to https://developers.app360.vn.
