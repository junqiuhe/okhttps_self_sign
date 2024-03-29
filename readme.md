使用HTTPS协议链接网络是一种常用方法。但实际使用中有如下几个困难

1、使用商业证书的成本

2、使用自定义证书不被系统承认

3、忽略证书检验则可能被“中间人攻击”

assets目录下文件说明:
    
    1、jackh_server.jks: 放在服务器端存储非对称秘钥对以及数字证书的证书库
    
    keytool -genkeypair -alias jackh_server -keyalg RSA -keystore jackh_server.jks -validity 3600 -storepass 123456
    
    2、jackh_server.cer: 放在客户端用于验证服务器身份的数据证书
    
    keytool -exportcert -alias jackh_server -file jackh_server.cer -keystore jackh_server.jks
    
    以下与双向认证相关
    3、jackh_client.jks, jackh_client.cer: 也是通过上面的命令生成的. 
    只不过jackh_client.jks放于客户端，而jackh_client.cer放于服务端.
    
    因为jackh_client.cer文件格式在tomcat服务器配置时不对。因此需要转换成jackh_client_for_server.jks文件
    
    keytool -importcert -alias jackh_client -file jackh_client.cer -keystore jackh_client_for_server.jks
    
    jackh_client.jks文件格式对于Android客户端不兼容，
    因此需要通过Portecle工具转换成Android能够识别的bks文件(jackh_client.bks).
    
```xml
<Connector SSLEnabled="true" acceptCount="100"
	    disableUploadTimeout="true" enableLookups="true" maxSpareThreads="75" 
	    maxThreads="200" minSpareThreads="5" port="8443" 
	    protocol="org.apache.coyote.http11.Http11NioProtocol" scheme="https" 
	    secure="true" sslProtocol="TLS"
	    keystoreFile="C:\Users\admin'pc\jackh_server.jks" keystorePass="123456"
	    clientAuth="false" truststoreFile="C:\Users\admin'pc\jackh_client_for_server.jks" truststorePass="123456"/>
```

#### Android WebView SSL 自签名安全校验解决方案

    1、在App中需要通过WebView访问URL, 如果服务器采用的是自签名证书，而不是CA认证时
    使用WebView加载URL的时候会显示为空白，出现无法加载网页的情况。
    
    2、使用CA认证的证书，在WebView则可以直接显示出来，不需要特殊出来
    
    针对自签名证书的解决方案（参考WebViewFragment）：
        
        1、继承WebViewClient且重写onReceivedSSLError方法，直接使用handler.proceed().该方案其实是忽略了证书校验，存在安全隐患
        
        2、安全的方案是当出现了证书问题的时候，读取 asserts 中保存的的根证书，然后与服务器校验，
        假如通过了，继续执行 handler.proceed()，否则执行 handler.cancel()。
    
    
#### 参考资料
    
[Android Https相关完全解析 当OkHttp遇到Https](https://blog.csdn.net/lmj623565791/article/details/48129405)

[
在Android应用中使用自定义证书的HTTPS连接（上）](https://blog.csdn.net/raptor/article/details/18896375)

[在Android应用中使用自定义证书的HTTPS连接（下）](https://blog.csdn.net/raptor/article/details/18898937)

[Java 安全套接字编程以及 keytool 使用最佳实践](https://www.ibm.com/developerworks/cn/java/j-lo-socketkeytool/index.html?ca=drs)
    
[Android安全开发之安全使用HTTPS](https://www.cnblogs.com/alisecurity/p/5939336.html)

[Android Webview SSL 自签名安全校验解决方案](https://www.cnblogs.com/liyiran/p/7011317.html)