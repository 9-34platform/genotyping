This demo provides a web service running in local browser based on the Spring Boot framework, and according to what you provided the web service can execute a command on your server. Although there may not necessarily feedback some results, submitted command must be sent to server and executed. 

### Before start

It is necessary to complete the missing info in `Remote.java` , or you will not access any server: 

```
 private final String user = "";
 private final String host = "";
 private final int port = 0;
 private final String password = "";
```

### Usage

After launched the web service, you will see the web page at: http://localhost:8080/demo

In the input box, type a command that you want to submit to the server, after you press the 'submit' button, the browser will jump to a new page and show you the processing results if the results exists. 