**Other language**: [Vietnamese](https://github.com/app360/app360-android-sdk/blob/master/README-VI.md)

#Introduction

App360SDK provides easiest way to integrate user management and payment methods (including sms, phone card and e-banking) into your application.

The App360 SDK supports Android version 4.0 and above.

# Getting started with Demo project

Firstly, clone or download this repository to your machine.

- `git clone https://github.com/app360/app360-android-sdk.git`
- Or, download from https://github.com/app360/app360-android-sdk/releases

The demo project is compatible with latest versions of both Android Studio and Eclipse. 

## Android Studio

To import the project into Android Studio, close the current project, choose _Open an existing Android Studio project_ then choose the repository you've just downloaded/cloned.

![Android Studio import](http://i.imgur.com/yLBQ2lP.png)

## Eclipse

To import the project into Eclipse, choose _File > Import..._, then inside the _Import_ dialog, choose _General > Existing projects into workspace_, then in _Select root directory_, browse to the repository. **Select all** projects that Eclipse detected, then click _Finish_. There're a lot of library projects, but they're necessary to support the demo (including Google Play Services for Google login, Facebook SDK for Facebook login, Android support libraries and some UI libraries).

![Eclipse import 1](http://i.imgur.com/ryT1Bqk.png)
![Eclipse import 2](http://i.imgur.com/LvXZzG4.png)

Open LoginActivity inside demo project, find `App360.initialize("appId", "appSecret", ...)` line and replace the placeholders with your application credentials.

Run the project. The app demonstrates capability of App360 SDK, including app-scoped ID and payment.

# x step to integrate with App360

## 1. Create an account

The first thing you need to do to get started with App360 is [sign up for a free account](https://developers.app360.vn/). After create account, you can access App360 dashboard to create or manage your apps.

## 2. Create your app

To integrate with App360SDK you need to create new application. Each application has a pair of key (application id and application secret key) that will be used to authorize with SDK’s server.

## 3. Download SDK

There are multiple ways to include the App360 SDK into an Android project. We can use Gradle or Maven (coming soon), or manually include the jar and its dependencies

> ### Download App360SDK
>
> Download the Android SDK from the App360 github repo. After downloading the JAR, place it on your application’s classpath. Typically, this is in your libs folder. Depending on your IDE, you may need to explicitly add the library to your project as a dependency.

## 4. Add Android Permissions

The App360 library requires the some permissions to operate. Your app will not work unless you add this permission to your AndroidManifest.xml file:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<!-- Remove if SMS payment is not needed or you don't use SDK Payment UI -->
<uses-permission android:name="android.permission.SEND_SMS" />
```

## 5. Add App360 SDK to your project

### Eclipse

To import the SDK into your Eclipse project, repeat the same steps for importing demo project, except that in the last step, just select `app360sdk` project. Right-click on your project, choose _Properties_, inside _Android > Library_, add `app360sdk` as a library project dependency.

### Android Studio

To import the SDK into Android Studio, follows these steps:

1. Choose _File > Import Module..._, then browse to `app360sdk` directory inside the repository. Click _Finish_ and wait until the import is completed.

    ![AS import 1](http://i.imgur.com/62OtAPu.png)
2. Open _File > Project Structure..._, select your application module, select _Dependencies_ tab, press the plus (+) symbol in the top-right corner, select _Module dependency_, then select `app360sdk`.
   
    ![AS import 2](http://i.imgur.com/7GX9wjD.png)

## 6. Add App360 config file

In order to support channeling, the application's `assets` directory should contain a properties file named `app360.properties`. The file should contain two keys `channel` and `sub_channel`. `channel` is distribution channel such as `mwork`, `appota` while `sub_channel` is arbitrary string defined by the distribution channel itself.

## 7. Setup App360 on Android

The App360 library must be initialized once with an Android context. This must happen before any App360 app reference is created or used. You can add the setup code to your Android Application’s or Activity’s onCreate method.

```java
App360SDK.initialize("appID", "appSecret", getApplicationContext(), new InitListener() {
    @Override
    public void onSuccess() {
        SessionService.Session session = SessionManager.getCurrentSession();
        if (session == null) { // no cached valid session
            SessionManager.createSession("your-user-id", new SessionCallback());
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

You can get appID and appSecret in the code example above from App360 dashboard. Login your account, choose the app you are working on and you will see the keys you need in Information tab

![app credential](http://i.imgur.com/Bp1ymT0.jpg)

### Note on channeling

Channeling is done per app-scoped ID. In other words, the channel of a payment equals to the channel of the app-scoped user that orders such payment. Moreover, an app-scoped user's channel is decided at _creation_ and equals to the channel specified in `app360.properties` of the build that creates it. So:

- If you want channeling be done on user level, you should synchronize your app's users with app-scoped ID (i.e. set your app's user ID as `scopedId` in 
    ```java
        public static void createSession(String scopedId, SessionCallback callback);
    ```
- If you want channeling be done on device level, you should set device ID as `scopedId` in 
    ```java
        public static void createSession(String scopedId, SessionCallback callback);
    ```

# What's next?

- Checkout [our document](http://docs.app360.vn/) for more infomation of App360SDK
- Integrate with [Payment API](http://docs.app360.vn/?page_id=271)
- If you got any trouble, checkout the [FAG page](http://docs.app360.vn/?page_id=228) or send a support request

#Release Notes

## Version 1.4.0

** Release date **: 19 Jun 2015

- New payment methods: SMS Plus (9029), Zing/Gate/Vcoin/Bit
- Support utm_source, utm_medium, utm_campaign, utm_content, utm_term in app360.properties file
- Support channeling (channel, sub_channel) via Google Play Store
- Add Unity wrapper
- Move UI resource to different project/module (app360sdkpaymentui)
- Slight change in API (deprecate several methods)
- Support getting update URL

## Version 1.0.0

** Release date **: 02 Feb 2015

- Support user management via app-scoped ID
- Support charging via phone card, SMS and e-banking
- Support checking transaction status

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
