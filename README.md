[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.hassan-ghahraloud/azcaptcha-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.hassan-ghahraloud/azcaptcha-java)

# JAVA Module for AZCaptcha API
The easiest way to quickly integrate [AZCaptcha] into your code to automate solving of any types of captcha.

- [Installation](#installation)
- [Configuration](#configuration)
- [Solve captcha](#solve-captcha)
  - [Normal Captcha](#normal-captcha)
  - [ReCaptcha v2](#recaptcha-v2)
  - [ReCaptcha v3](#recaptcha-v3)
  - [hCaptcha](#hcaptcha)
- [Other methods](#other-methods)
  - [send / getResult](#send--getresult)
  - [balance](#balance)
  - [report](#report)
- [Error handling](#error-handling)


## Installation
azcaptcha-java artifact is available in [Maven Central]

## Configuration
`AZCaptcha` instance can be created like this:
```java
AZCaptcha solver = new AZCaptcha('YOUR_API_KEY');
```
Also there are few options that can be configured:
```java
solver.setSoftId(123);
solver.setCallback("https://your.site/result-receiver");
solver.setDefaultTimeout(120);
solver.setRecaptchaTimeout(600);
solver.setPollingInterval(10);
```

### AZCaptcha instance options

|Option|Default value|Description|
|---|---|---|
|softId|-|your software ID obtained after publishing in [azcaptcha sofware catalog]|
|callback|-|URL of your web-sever that receives the captcha recognition result. The URl should be first registered in [pingback settings] of your account|
|defaultTimeout|120|Polling timeout in seconds for all captcha types except ReCaptcha. Defines how long the module tries to get the answer from `res.php` API endpoint|
|recaptchaTimeout|600|Polling timeout for ReCaptcha in seconds. Defines how long the module tries to get the answer from `res.php` API endpoint|
|pollingInterval|10|Interval in seconds between requests to `res.php` API endpoint, setting values less than 5 seconds is not recommended|

>  **IMPORTANT:** once *callback URL* is defined for `AZCaptcha` instance with `setCallback`, all methods return only the captcha ID and DO NOT poll the API to get the result. The result will be sent to the callback URL.
To get the answer manually use [getResult method](#send--getresult)

## Solve captcha
When you submit any image-based captcha use can provide additional options to help azcaptcha workers to solve it properly.

### Captcha options
|Option|Default Value|Description|
|---|---|---|
|hintImg|-|an image with hint shown to workers with the captcha|
|hintText|-|hint or task text shown to workers with the captcha|

Check out [examples directory] to find more examples with all available options.

### Normal Captcha
To bypass a normal captcha (distorted text on image) use the following method. This method also can be used to recognize any text on the image.

```java
Normal captcha = new Normal();
captcha.setFile("path/to/captcha.jpg");
captcha.setHintImg(new File("path/to/hint.jpg"));
captcha.setHintText("Type red symbols only");
```

### ReCaptcha v2
Use this method to solve ReCaptcha V2 and obtain a token to bypass the protection.

```java
ReCaptcha captcha = new ReCaptcha();
captcha.setSiteKey("6Le-wvkSVVABCPBMRTvw0Q4Muexq1bi0DJwx_mJ-");
captcha.setUrl("https://mysite.com/page/with/recaptcha");
captcha.setInvisible(true);
captcha.setAction("verify");
captcha.setProxy("HTTPS", "login:password@IP_address:PORT");
```
### ReCaptcha v3
This method provides ReCaptcha V3 solver and returns a token.

```java
ReCaptcha captcha = new ReCaptcha();
captcha.setSiteKey("6Le-wvkSVVABCPBMRTvw0Q4Muexq1bi0DJwx_mJ-");
captcha.setUrl("https://mysite.com/page/with/recaptcha");
captcha.setVersion("v3");
captcha.setAction("verify");
captcha.setScore(0.3);
captcha.setProxy("HTTPS", "login:password@IP_address:PORT");
```

### hCaptcha
Method to solve GeeTest puzzle captcha. Returns a set of tokens as JSON.

```java
HCaptcha captcha = new HCaptcha();
captcha.setSiteKey("10000000-ffff-ffff-ffff-000000000001");
captcha.setUrl("https://www.site.com/page/");
captcha.setProxy("HTTPS", "login:password@IP_address:PORT");
```

## Other methods

### send / getResult
These methods can be used for manual captcha submission and answer polling.

```java
String captchaId = solver.send(captcha);

Thread.sleep(20 * 1000);

String code = solver.getResult(captchaId);
```
### balance
Use this method to get your account's balance

```java
double balance = solver.balance();
```
### report
Use this method to report good or bad captcha answer.

```java
solver.report(captcha.getId(), true); // captcha solved correctly
solver.report(captcha.getId(), false); // captcha solved incorrectly
```

## Error handling
If case of an error captcha solver throws an exception. It's important to properly handle these cases. We recommend to use `try catch` to handle exceptions.

```java
try {
    solver.solve(captcha);
} catch (ValidationException e) {
    // invalid parameters passed
} catch (NetworkException e) {
    // network error occurred
} catch (ApiException e) {
    // api respond with error
} catch (TimeoutException e) {
    // captcha is not solved so far
}
```
[Maven Central]: https://maven-badges.herokuapp.com/maven-central/com.github.hassan-ghahraloud/azcaptcha-java
[examples directory]: /src/main/java/examples
[AZCaptcha]: https://azcaptcha.com/
