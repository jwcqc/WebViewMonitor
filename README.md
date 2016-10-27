# WebViewMonitor
监控WebView请求中的页面性能数据，各个资源请求时间，JS错误等

核心其实就是 https://github.com/jwcqc/WebViewMonitor/blob/master/app/src/main/assets/collector.js 这个js文件，当WebView中页面加载完成后，通过重写WebViewClient的onPageFinished(WebView view, String url) 方法，调用WebView的loadUrl去加载一段JS，新加一个script标签到head标签中，并在script中包含要注入的collecotr.js的url地址，再为加入的script标签添加onload事件，确保该script已加载完成后调用js文件中编写好的的startWebViewMonitor()方法即可。

代码中首先为webview提供了一个接口，注入了一个 Java 对象到页面中，以实现 Java 和 Javascript 的交互：
```
webview.addJavascriptInterface(new JSObject(), "myObj");
```

在collecor.js中分别写有两个方法，将获取到的性能数据传递给JSObject对象：
```
function sendResourceTiming(e) {
	myObj.sendResource(JSON.stringify(e))
};

function sendErrors() {
	var err = errorMonitor.getError();
	if (err.length > 0) {
		var errorInfo = {
	        type: "monitor_error",
            payload: {
	            url: hrefUrl,
	            domain: hostname,
	            uri: pathname,
	            error_list: err
            }
        };

        myObj.sendError(JSON.stringify(errorInfo))
    }
};
```

然后在JSObject这个对象中相应的方法即可捕获到数据，然后进行处理：
```
public class JSObject {

    @JavascriptInterface
    public void sendResource(String msg) {
       //handleResource(msg);
    }

    @JavascriptInterface
    public void sendError(String msg) {
        //handleError(msg);
    }
}
```

至于collector.js中对页面性能数据获取的实现方法，则是通过Performance API实现的，这里主要用到了页面加载Navigation Timing和页面资源加载Resource Timing，这两个API非常有用，可以帮助我们获取页面的domready时间、onload时间、白屏时间等，以及单个页面资源在从发送请求到获取到response各阶段的性能参数。

Navigation Timing对象包含了各种与浏览器性能有关的时间数据，提供浏览器处理网页各个阶段的耗时，它包含的页面性能属性如下表：

属性 | 含义
---|---
navigationStart | 准备加载新页面的起始时间
redirectStart | 如果发生了HTTP重定向，并且从导航开始，中间的每次重定向，都和当前文档同域的话，就返回开始重定向的timing.fetchStart的值。其他情况，则返回0
redirectEnd   | 如果发生了HTTP重定向，并且从导航开始，中间的每次重定向，都和当前文档同域的话，就返回最后一次重定向，接收到最后一个字节数据后的那个时间.其他情况则返回0
fetchStart | 如果一个新的资源获取被发起，则 fetchStart必须返回用户代理开始检查其相关缓存的那个时间，其他情况则返回开始获取该资源的时间
domainLookupStart | 返回用户代理对当前文档所属域进行DNS查询开始的时间。如果此请求没有DNS查询过程，如长连接，资源cache,甚至是本地资源等。 那么就返回 fetchStart的值
domainLookupEnd | 返回用户代理对结束对当前文档所属域进行DNS查询的时间。如果此请求没有DNS查询过程，如长连接，资源cache，甚至是本地资源等。那么就返回 fetchStart的值
connectStart | 返回用户代理向服务器服务器请求文档，开始建立连接的那个时间，如果此连接是一个长连接，又或者直接从缓存中获取资源（即没有与服务器建立连接）。则返回domainLookupEnd的值
(secureConnectionStart) | 可选特性。用户代理如果没有对应的东东，就要把这个设置为undefined。如果有这个东东，并且是HTTPS协议，那么就要返回开始SSL握手的那个时间。 如果不是HTTPS， 那么就返回0
connectEnd | 返回用户代理向服务器服务器请求文档，建立连接成功后的那个时间，如果此连接是一个长连接，又或者直接从缓存中获取资源（即没有与服务器建立连接）。则返回domainLookupEnd的值
requestStart | 返回从服务器、缓存、本地资源等，开始请求文档的时间
responseStart | 返回用户代理从服务器、缓存、本地资源中，接收到第一个字节数据的时间
responseEnd | 返回用户代理接收到最后一个字符的时间，和当前连接被关闭的时间中，更早的那个。同样，文档可能来自服务器、缓存、或本地资源
domLoading | 返回用户代理把其文档的 "current document readiness" 设置为 "loading"的时候
domInteractive | 返回用户代理把其文档的 "current document readiness" 设置为 "interactive"的时候.
domContentLoadedEventStart | 返回文档发生 DOMContentLoaded事件的时间
domContentLoadedEventEnd | 文档的DOMContentLoaded 事件的结束时间
domComplete | 返回用户代理把其文档的 "current document readiness" 设置为 "complete"的时候
loadEventStart | 文档触发load事件的时间。如果load事件没有触发，那么该接口就返回0
loadEventEnd | 文档触发load事件结束后的时间。如果load事件没有触发，那么该接口就返回0

浏览器获取网页时，会对网页中每一个静态资源（脚本文件、样式表、图片文件等等）发出一个HTTP请求。Resource Timing可以获取到单个静态资源从开始发出请求到获取响应之间各个阶段的Timing，返回的是一个对象数组，数组的每一个项都是一个对象，这个对象中包含了当前静态资源的加载Timing用法如下:
```
var resourcesObj = performance.getEntries();
```

获取到这些数据以后，如果需要进一步获得其他对我们比较有用的页面性能数据，比如DNS查询耗时、TCP链接耗时、request请求耗时、解析dom树耗时、白屏时间、domready时间、onload时间等，可以通过上面的performance.timing各个属性的差值计算得到，方法如下：

> DNS查询耗时 ：domainLookupEnd - domainLookupStart  
TCP链接耗时 ：connectEnd - connectStart  
request请求耗时 ：responseEnd - responseStart  
解析dom树耗时 ： domComplete- domInteractive  
白屏时间 ：responseStart - navigationStart  
domready时间 ：domContentLoadedEventEnd - navigationStart  
onload时间 ：loadEventEnd - navigationStart  


JS错误的捕获，只需通过调用addEventListener(type, listener, useCapture)，type传error即可。

具体的实现方法，可以仔细查看代码。

参考链接：  
http://www.bubuko.com/infodetail-1228020.html  
https://segmentfault.com/a/1190000004010453  

