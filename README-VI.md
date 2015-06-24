**Other language**: [English](https://github.com/app360/app360-android-sdk/blob/master/README.md)

# Giới thiệu

App360SDK cung cấp cách thức đơn giản nhất để quản lý user và tích hợp thanh toán (bao gồm sms, thẻ điện thoại và e-banking) vào ứng dụng của bạn.

The App360 SDK hỗ trợ Android version 4.0 trở lên.

# Bắt đầu với project Demo

Bạn cần clone hoặc download repository này về máy của bạn.

- `git clone https://github.com/app360/app360-android-sdk.git`
- Hoặc, download tại https://github.com/app360/app360-android-sdk/releases

Project demo tương thích với phiên bản mới nhất của cả Android Studio và Eclipse. 

## Android Studio

Để import project vào Android Studio, vui lòng đóng project hiện tại, chọn _Open an existing Android Studio project_ sau đó chọn repository bạn vừa download/clone.

![Android Studio import](http://i.imgur.com/yLBQ2lP.png)

## Eclipse

Để import project vào Eclipse, chọn _File > Import..._, trong dialog _Import_, chọn _General > Existing projects into workspace_, sau đó trong _Select root directory_, trỏ đến repository. **Select all** projects Eclipse nhận diện ra được, rồi bấm _Finish_. Có khá nhiều thư viện trong  project, nhưng chỉ để dùng cho ứng dụng demo (bao gồm cả Google Play Services để hỗ trợ login qua Google, Facebook SDK để hỗ trợ login qua Facebook, Android support libraries và một số thư viện cho UI).

![Eclipse import 1](http://i.imgur.com/ryT1Bqk.png)
![Eclipse import 2](http://i.imgur.com/LvXZzG4.png)

Mở LoginActivity trong demo project, tìm dòng `App360.initialize("appId", "appSecret", ...)` và thay thể placeholders với cặp key của ứng dụng.

Chạy project. Ứng dụng mô tả cho bạn khả năng cũng như cách tích hợp của App360SDK, bao gồm cả app-scoped ID và payment (thanh toán).

# 7 bước để tích hợp với App360

## 1. Tạo tài khoản

Việc đầu tiên bạn cần làm là [đăng ký một tài khoản miễn phí](https://developers.app360.vn/) trên App360. Sau khi có tài khoản, bạn có thể truy cập vào App360 dashboard để tạo và quản lý các ứng dụng của bạn.

## 2. Tạo ứng dụng

Để tích hợp với App360SDK, bạn cần phải tạo một ứng dụng trên trang developer của App360. Mỗi ứng dụng sẽ có một cặp key (app id và app secret). Cặp key này sẽ được dùng để xác thực với server của SDK

## 3. Tải SDK

Có nhiều cách để tải SDK. Bạn có thể sử dụng Gradle or Maven (coming soon), hoặc thêm file jar và các thư viện liên quan một cách thủ công.

> ### Tải App360SDK
>
> Tải Android SDK từ repo này. Sau khi download xong, thêm file jar vào classpath của ứng dụng. Thông thường, nó sẽ được thêm vào thư mục `libs`. Tùy thuộc vào IDE bạn sử dụng mà bạn có thể sẽ cần phải thêm thư viện như một dependency

## 4. Thêm Android Permissions

App360 yêu cầu một số quyền để có thể hoạt động được. Ứng dụng của bạn sẽ không chạy được nếu thiếu những quyền sau trong file `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<!-- Remove if SMS payment is not needed or you don't use SDK Payment UI -->
<uses-permission android:name="android.permission.SEND_SMS" />
```

## 5. Tích hợp App360 vào project của bạn

### Eclipse

Để import SDK vào trong Eclipse project, lặp lại những bước trên như import demo project, ngoại trừ ở bước cuối, là chọn project `app360sdk`. Chuột phải vào project của bạn, chọn _Properties_, bên trong _Android > Library_, thêm `app360sdk` như một library project dependency.


### Android Studio

Để import SDK vào trong Android Studio, làm theo những bước sau:

1. Chọn _File > Import Module..._, trỏ đến thư mục `app360sdk` bên trong repository. Bấm _Finish_ và đợi đến khi quá trình import kết thúc.
   ![AS import 1](http://i.imgur.com/62OtAPu.png)
2. Chọn _File > Project Structure..._, chọn application module của bạn, chọn tab _Dependencies_, bấm vào biểu tượng dấu cộng (+) ở góc trên bên phải, chọn _Module dependency_, sau đó chọn `app360sdk`.
   ![AS import 2](http://i.imgur.com/7GX9wjD.png)

## 6. Thêm file config của App360

Để hỗ trợ mô hình channeling, thư mục `assets` của ứng dụng cần một file property với tên `app360.properties`. File này chứa hai key là `channel` and `sub_channel`. `channel` là kênh phân phối, ví dụ như `mwork`, `appota` còn `sub_channel` là một chuỗi bất kì được định nghĩa bới kênh phân phối.

## 7. Bắt đầu viết code

App360 phải được khởi tạo với một Android context. Việc này cần phải được thực hiện trước khi gọi bất kì API nào của App360. Bạn có thể thêm đoạn code dưới đây vào Android Application’s hoặc phương thức `onCreate` của Activity.

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

Bạn có thể lấy `appID` và `appSecret` trong code mẫu bên trên từ App360 dashboard. Đăng nhập vào tài khoản của bạn, chọn ứng dụng mà bạn đang tích hợp, bạn sẽ thấy key bạn cần trong tab `Information`

![App credential](http://i.imgur.com/Bp1ymT0.jpg)

### Chú ý về channeling

Channeling đã được hỗ trợ qua app-scoped ID. Nói theo cách khác, channel của một giao dịch thanh toán bằng với channel của appscoped user thực hiện thanh toán đó. Hơn nữa, chanel của một app-scoped user được quyết định khi _khởi tạo_ và bằng với channel được đặt trong file `app360.properties` trong bản build mà user đăng ký. Do đó:

- Nếu bạn muốn cơ chế channeling theo user, bạn cần đồng bộ user của bạn với app-scoped ID (ví dụ: đặt user id trong ứng dụng của bạn làm `scopedId` trong 

    ```java
        public static void createSession(String scopedId, SessionCallback callback);
    ```

- Nếu bạn muốn cơ chế channeling theo device, bạn nên đặt device ID làm `scopedId` trong 
    ```java
        public static void createSession(String scopedId, SessionCallback callback);
    ```
# Làm gì tiếp theo?

- Xem thêm [tài liệu của chúng tôi](http://docs.app360.vn/) để biết thêm những thông tin chi tiết về các hàm của App360SDK.
- Tích hợp với [Payment API](http://docs.app360.vn/?page_id=271)
- Nếu gặp bất kì vấn đề gì, vui lòng xem qua [trang FAG](http://docs.app360.vn/?page_id=228) hoặc gửi yêu cầu hỗ trợ cho chúng tôi

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
