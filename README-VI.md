**Other language**: [English](https://github.com/app360/app360-android-sdk/blob/master/README.md)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Giới thiệu](#giới-thiệu)
- [Bắt đầu với project Demo](#bắt-đầu-với-project-demo)
- [Import SDK vào project của bạn](#import-sdk-vào-project-của-bạn)
- [Tích hợp ứng dụng của bạn với App360 SDK](#tích-hợp-ứng-dụng-của-bạn-với-app360-sdk)
  - [Tài liệu](#tài-liệu)
  - [Application Id & secret](#application-id-&-secret)
  - [AndroidManifest](#androidmanifest)
  - [Assets](#assets)
  - [Các SDK khác](#các-sdk-khác)
- [App-scoped ID](#app-scoped-id)
  - [Khởi tạo SDK](#khởi-tạo-sdk)
  - [Khởi tạo session](#khởi-tạo-session)
    - [Chú ý về channeling](#chú-ý-về-channeling)
  - [Liên kết app-scoped ID với Facebook/Google](#liên-kết-app-scoped-id-với-facebookgoogle)
- [Thanh toán](#thanh-toán)
  - [Luồng thanh toán](#luồng-thanh-toán)
  - [Using payment form UI](#using-payment-form-ui)
  - [Using request classes](#using-request-classes)
  - [Kiểm tra trạng thái của giao dịch](#kiểm-tra-trạng-thái-của-giao-dịch)
- [Release Notes](#release-notes)
  - [Version 1.0.0](#version-100)
- [Known Issues](#known-issues)
- [FAQ](#faq)
- [Hỗ trợ](#hỗ-trợ)
  - [Về những vấn đề kỹ thuật](#về-những-vấn-đề-kỹ-thuật)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Giới thiệu

App360SDK cung cấp cách thức đơn giản nhất để quản lý user và tích hợp thanh toán (bao gồm sms, thẻ điện thoại và e-banking) vào ứng dụng của bạn.

The App360 SDK hỗ trợ Android version 4.0 trở lên.

Sơ đồ luồng tích hợp App360 SDK

![Integration Flow](http://i.imgur.com/PXdCmb3.png)

# Bắt đầu với project Demo

Bạn cần clone hoặc download repository này về máy của bạn.

- `git clone https://github.com/app360/app360-android-sdk.git`
- Hoặc, download tại https://github.com/app360/app360-android-sdk/releases

Project demo tương thích với phiên bản mới nhất của cả Android Studio và Eclipse. Để import project vào Android Studio, vui lòng đóng project hiện tại, chọn _Open an existing Android Studio project_ sau đó chọn repository bạn vừa download/clone.

![Android Studio import](http://i.imgur.com/yLBQ2lP.png)

Để import project vào Eclipse, chọn _File > Import..._, trong dialog _Import_, chọn _General > Existing projects into workspace_, sau đó trong _Select root directory_, trỏ đến repository. **Select all** projects Eclipse nhận diện ra được, rồi bấm _Finish_. Có khá nhiều thư viện trong  project, nhưng chỉ để dùng cho ứng dụng demo (bao gồm cả Google Play Services để hỗ trợ login qua Google, Facebook SDK để hỗ trợ login qua Facebook, Android support libraries và một số thư viện cho UI).

![Eclipse import 1](http://i.imgur.com/ryT1Bqk.png)
![Eclipse import 2](http://i.imgur.com/LvXZzG4.png)

Mở LoginActivity trong demo project, tìm dòng `App360.initialize("appId", "appSecret", ...)` và thay thể placeholders với cặp key của ứng dụng.

Chạy project. Ứng dụng mô tả cho bạn khả năng cũng như cách tích hợp của App360SDK, bao gồm cả app-scoped ID và payment (thanh toán).

# Import SDK vào project của bạn

Để import SDK vào trong Eclipse project, lặp lại những bước trên như import demo project, ngoại trừ ở bước cuối, là chọn project `app360sdk`. Chuột phải vào project của bạn, chọn _Properties_, bên trong _Android > Library_, thêm `app360sdk` như một library project dependency.

Để import SDK vào trong Android Studio, làm theo những bước sau:

1. Chọn _File > Import Module..._, trỏ đến thư mục `app360sdk` bên trong repository. Bấm _Finish_ và đợi đến khi quá trình import kết thúc.
   ![AS import 1](http://i.imgur.com/62OtAPu.png)
2. Chọn _File > Project Structure..._, chọn application module của bạn, chọn tab _Dependencies_, bấm vào biểu tượng dấu cộng (+) ở góc trên bên phải, chọn _Module dependency_, sau đó chọn `app360sdk`.
   ![AS import 2](http://i.imgur.com/7GX9wjD.png)

# Tích hợp ứng dụng của bạn với App360 SDK

## Tài liệu
- Getting Started Guide: this README
- Online Javadoc: https://docs.app360.vn/javadoc/
- Javadoc archive: `app360sdk/app360sdk-javadoc.jar` inside this repository

## Application Id & secret

Để sử dụng bất kì chức năng nào của App360SDK, bạn cần phải có application id và secret key tương ứng. SDK sử dụng cặp key này để xác thực app (game) của bạn với server của SDK. Để lấy application ID và secret key, bạn cần đăng ký ứng dụng (game) của bạn trên https://developers.app360.vn/; application ID và secret key của của ứng dụng sẽ được hiển thị trong trang thông tin chi tiết của ứng dụng, tab `Information`.

![App credentials](http://i.imgur.com/4xZ8fYc.png)

## AndroidManifest

App360 SDK cần những quyền sau:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<!-- Bỏ quyền này nếu không dùng thanh toán qua SMS -->
<uses-permission android:name="android.permission.SEND_SMS" />
```

## Assets

Để hỗ trợ mô hình channeling, thư mục `assets` của ứng dụng cần một file property với tên `app360.properties`. File này chứa hai key là `channel` and `sub_channel`. `channel` là kênh phân phối, ví dụ như `mwork`, `appota` còn `sub_channel` là một chuỗi bất kì được định nghĩa bới kênh phân phối.

## Các SDK khác

App360 SDK hỗ trợ đăng nhập qua tài khoản Facebook/Google, tuy nhiên để tăng sự linh động, ứng dụng của bạn sẽ thực hiện các bước để lấy access token của user từ Facebook/Google. SDK chỉ nhận đầu vào là access token, do đó bạn cần tự tham khảo thêm tài liệu của những phần liên quan từ Facebook và Google, tại:
- https://developers.facebook.com/docs/android/getting-started
- https://developers.facebook.com/docs/android/login-with-facebook/v2.2
- http://developer.android.com/google/auth/http-auth.html

# App-scoped ID

App-scoped ID là dịch vụ dùng để định danh người dùng cuối cho các ứng dụng tích hợp SDK của App360. App-scoped IDs được sinh ra bởi ứng dụng và có giá trị cho ứng dụng đó (hence _app-scoped_). Một ứng dụng không thể truy cập vào dữ liệu và user của ứng dụng khác.

App-scoped ID SDK cung cấp các phương thức sau:
- Đăng ký/Đăng nhập nặc danh (anonymously)
- Đăng ký đăng nhập qua Facebook/Google
- Liên kết app-scoped ID nặc danh với Facebook/Google account

**Note**: tất cả các API của App360 đều yêu cầu phải tồn tại của một app-scoped ID.

## Khởi tạo SDK

SDK được khởi tạo qua `App360.initialize()`. Quá trình khởi tạo nên được thực hiện trong `onCreate` của main activity.

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

`appId` và `appSecret` có thể lấy trên [App360 Developers](https://developers.app360.vn). `ctx` là context của ứng dụng, e.g. `Activity.getApplicationContext()`. `listener` là một instance của `InitListener` interface, được định nghĩa như sau

```java
public interface InitListener {
    public void onSuccess();

    public void onFailure(Exception e);
}
```

Trong suốt quá trình khởi tạo, SDK sẽ tìm kiếm và mở lại session cũ. Nếu không mở được session nào, `SessionManager.getCurrentSession()` sẽ trả về. Khi đó ứng dụng sẽ phải tự mở một session mới...

```java
App360SDK.initialize("appID", "appSecret", getApplicationContext(), new InitListener() {
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

## Khởi tạo session

App-scoped ID được đăng nhập thông qua việc tạo một session mới bằng ID đó. Có hai cách để mở một session (cả hai đều thông qua class `SessionManager`):

- Anonymously
```java
    public static void createSession(String scopedId, SessionCallback callback);
```
- With Facebook/Google access token
```java
public static void createSession(String service, String token, SessionCallback callback);
```

Khi session được tạo bằng các nặc danh và scopedId đã tồn tại, một session mới sẽ được tạo cho app-scoped ID đó. Nếu scopedId không tồn tại, một app-scoped ID mới trùng với scopedId sẽ được tạo. Nếu scopedId null, nó sẽ được sinh một cách ngẫu nhiên.

Cần chú ý rằng sẽ không có bất kì cơ thế xác thực nào (password, token,...) cho việc đăng nhập nặc danh. Hình thức đăng nhập này nên được sử dụng sau khi ứng dụng đã có sẵn cơ chế xác thực riêng (trong trường hợp này, scopedId có thể được đặt theo username trong ứng dụng); hoặc khi ứng dụng không cần người dùng đăng nhập ngay. Anonymously logged-in user có thể liên kết với tài khoản Facebook và Google sau đó nhằm mục đích bảo mật và linh động (ví dụ như đăng nhập vào cùng 1 app-scoped ID trên những thiết bị khác nhau).

Khi session được tạo qua Facebook/Google access token, SDK sẽ kiểm tra token với server của Facebook/Google sau đó sẽ tìm một app-scoped ID đã tồn tài và đã liên kết với tài khoản mạng xã hội tương ứng. Nếu không có ID nào, thì một ID mới sẽ được sinh ngẫu nhiên và được liên kết với tài khoản mạng xã hội đó, session tương ứng sẽ được trả về. Nếu không, một session mới sẽ được sinh ra cho app-scoped ID tương ứng. Nếu sử dụng cách này thì cơ chế xác thực sẽ sử dụng qua Facebook và Google.

Ứng dụng sẽ nhận được session sau khi mở thông qua `SessionCallback` interface:

```java
public static interface SessionCallback {
    void onSuccess();

    void onFailure(Exception e);
}
```

Trong `onSuccess()` callback, application có thể access session hiện tại qua `SessionManager.getCurrentSession()` và scoped user hiện tại qua `ScopedUser.getCurrentUser()`. Ví dụ:

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

### Chú ý về channeling

Channeling đã được hỗ trợ qua app-scoped ID. Nói theo cách khác, channel của một giao dịch thanh toán bằng với channel của appscoped user thực hiện thanh toán đó. Hơn nữa, chanel của một app-scoped user được quyết định khi _khởi tạo_ và bằng với channel được đặt trong file `app360.properties` trong bản build mà user đăng ký. Do đó:

- Nếu bạn muốn cơ chế channeling theo user, bạn cần đồng bộ user của bạn với app-scoped ID (ví dụ: đặt user id trong ứng dụng của bạn làm `scopedId` trong `public static void createSession(String scopedId, SessionCallback callback);`
- Nếu bạn muốn cơ chế channeling theo device, bạn nên đặt device ID làm `scopedId` trong `public static void createSession(String scopedId, SessionCallback callback);`

## Liên kết app-scoped ID với Facebook/Google

Để liên kết app-scoped ID hiện tại với Facebook/Google, gọi  `public void linkFacebook(String token, SaveCallback cb)` or `public void linkGoogle(String token, SaveCallback cb)`

Ví dụ:
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

# Thanh toán


## Luồng thanh toán

Để đảm bảo độ tin cậy cho luồng thanh toán, ứng dụng của bạn có thể chọn tích hợp với SDK thông qua cả client-side và server-side, luồng thanh toán được mô tả trong sơ đồ dưới đây:

![Payment flow](http://www.websequencediagrams.com/cgi-bin/cdraw?lz=dGl0bGUgUGF5bWVudCBzZXF1ZW5jZQoKR2FtZS0-U0RLOgAUCXJlcXVlc3QgKDEpClNESy0-R2FtZTogVHJhbnNhY3Rpb24gaWQsIHN0YXR1cywgYW1vdW50ICgyKQBGB0dhbWUgc2VydmVyOiBzZW5kIHQAMAtkYXRhIGZvciBpbnNwZQBJBigzAF8LADIJACoMaWQAYQgsIHVzZXJfaWQgKDQAbAYAZQcAgT8HYWNrIChIVFRQAIEaByAyMDApICg1AB0PAIEYDWNvbmZpcm0gKDYAExMAFAs3KQ&s=rose)

1. Ứng dụng (client-side) gọi API thanh toán từ SDK, tùy chọn có thể gửi kèm payload (mô tả bên dưới).
2. SDK trả về transaction id và thông tin bổ sung (nếu có)
3. Ứng dụng client gửi thông tin về transaction lên server của ứng dụng để đợi xác nhận.
4. SDK server gọi đến server của ứng dụng đã được đăng ký để thông báo về trạng thái của giao dịch khi nó đã hoàn thành.
- Lưu ý rằng hai bước (3) và (4), thứ tự có thể không giống như sơ đồ. Bước (4) hoàn toàn có thể xảy ra trước bước (3).
5. Server của ứng dụng thông báo đã nhận được kết quả cho SDK server bằng việc trả về HTTP status code 200.
6. Server của ứng dụng duyệt giao dịch dựa vào các thông tin của nó (transaction ID, payload,....)
7. Server của ứng dụng xác nhận/thông báo cho ứng dụng về trạng thái của giao dịch.

**Note**:
- Để đăng kí địa chỉ server nhận callback, truy cập https://developers.app360.vn/; điền _Payment callback endpoint_ trong trang thông tin của ứng dụng, tab _Information_.
- Trước khi sử dụng bất kì phương thức thanh toán náo, ứng dụng phải khởi tạo SDK và khởi tạo session trước. Xem mục _Khởi tạo SDK_ ở trên.

Có hai cách để tích hợp thanh toán của App360SDK vào ứng dụng của bạn:
- Sử dụng UI mặc định mà SDK cung cấp
- Tự xây dựng UI của bạn và sử dụng các API thanh toán mà SDK cung cấp.

## Using payment form UI

Để sử dụng giao diện thanh toán, bạn cần thêm những activity sau vào file manifest của project (nếu manifestmerger đã diabled):

```xml
<activity android:name="vn.mog.app360.sdk.payment.activities.CardActivity" />
<activity android:name="vn.mog.app360.sdk.payment.activities.BankActivity" />
<activity android:name="vn.mog.app360.sdk.payment.activities.SmsActivity" />
<activity android:name="vn.mog.app360.sdk.payment.activities.BankWebviewActivity"/>
<activity android:name="vn.mog.app360.sdk.payment.activities.PaymentFormActivity" />
```

Để hiển thị giao diện thanh toán, build `PaymentForm` qua `PaymentForm.Builder`. Ví dụ:

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
Vui lòng tham khảo javadoc và project demo để hiểu rõ hơn về từng tham số. Bạn cũng không cần set tất cả các tham số, mà chỉ cần set cho những phương thức thanh toán mà ứng dụng của bạn sử dụng. Ví dụ, nếu bạn không sử dụng thanh toán qua SMS, bạn sẽ không cần phải sử dụng `setSMSAmount`.

Sau khi build, gọi `paymentForm.showCardForm()`, `paymentForm.showBankForm()`, `paymentForm.showSmsForm()` and `paymentForm.showPaymentForm()` để hiển thị từng giao diện thanh toán riêng lẻ. `showPaymentForm()` sẽ hiển thị giao diện cho phép user chọn những phương thức thanh toán được hỗ trợ.

![](http://i.imgur.com/ooSBqnf.png)![Imgur](http://i.imgur.com/npicbmU.png)
![Imgur](http://i.imgur.com/62lJjpJ.png)![Imgur](http://i.imgur.com/QvJS792.png)

Để nhận callback khi quá trình thanh toán kết thúc, hãy cài đặt `PaymentFormListener` và set listener khi build `PaymentForm.Builder#setListener`.  Interface được định nghĩa như sau:

```java
public interface PaymentFormListener {
    public void onFinish(Transaction transaction);
    public void onCancel();
    public void onError(Throwable e);
}
```

Chú ý rằng `onFinish` chỉ có nghĩa user đã kết thúc việc gửi yêu cầu thanh toán, còn bản thân giao dịch có thể vẫn chưa kết thúc. Ví dụ, user có thể đã gửi tin nhắn nhưng backend của chúng tôi vẫn chưa nhận được nó. Để kiểm tra trạng thái của giao dịch, hãy sử dụng `StatusRequest`.

## Using request classes

Để thanh toán qua thẻ điện thoại bằng cách gửi request, gọi:

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

Để thanh toán qua SMS bằng cách gửi request, gọi:
```java
SmsRequest smsRequest = new SmsRequest.Builder()
                .setAmounts(Const.SmsAmount.AMOUNT_1000, Const.SmsAmount.AMOUNT_15000)
                .setPayload("payload")
                .setListener(new MyRequestListener())
                .build();
smsRequest.execute();
```

Để thanh toán qua E-banking bằng cách gửi request, gọi:
```java
BankRequest bankRequest = new BankRequest.Builder()
                .setAmount(25000)
                .setPayload("payload")
                .setListener(new MyRequestListener())
                .build();
bankRequest.execute();
```

## Kiểm tra trạng thái của giao dịch

Để kiểm tra trạng thái của giao dịch, sử dụng `StatusRequest` như dưới đây:

```java
StatusRequest statusRequest = new StatusRequest.Builder()
                .setListener(statusListener)
                .setTransactionId(transId)
                .build();
statusRequest.execute();
```

# Release Notes

## Version 1.0.0

**Release date**: 02 Feb 2015

 - Support user management via app-scoped ID
 - Support charging via phone card, SMS and e-banking
 - Support checking transaction status

# Known Issues

There's no known issues for now

# FAQ

**Application id và client key là gì?**

Đây là cặp key, dùng để xác thực ứng dụng (game) của bạn với SDK server.

**Application id và client key có thể lấy ở đâu?**

1. Truy cập https://developers.app360.vn
2. Đăng nhập nếu bạn đã có tài khoản, nếu chưa hãy đăng ký tài khoản mới
3. Mở ứng dụng của bạn trong App360 dashboard. Nếu chưa có hãy tạo mới. Sau đó, chọn tab Information
4. Trong tab này, copy application key và secret key

# Hỗ trợ
Vui lòng liên hệ với [chúng tôi](mailto:support@app360.vn) về những vấn đề chung.

## Về những vấn đề kỹ thuật
Trong trường hợp bạn có những vấn đề về kỹ thuật, vui lòng liên hệ với [đội kỹ thuật của chúng tôi](mailto:support@app360.vn).
Vui lòng cung cấp những thông tin sau khi liên hệ, chúng sẽ giúp chúng tôi hỗ trợ bạn nhanh hơn rất nhiều.

- **Phiên bản của SDK** bạn đang sử dụng. Bạn có thể biết được phiên bản của SDK thông qua việc gọi hàm `App360SDK.getVersion()`.
- **Môi trường** sử dụng để có thể tái hiện lại vấn đề (máy ảo hay thiết bị thật, model nào, android version bao nhiêu).
- **Các bước** để tái hiện vấn đề.
- Nếu có thể, bạn hãy cung cấp **một vài đoạn code**, thậm chí cả project nếu có thể.

> Để biết thêm thông tin chi tiết, vui lòng truy cập https://developers.app360.vn.
