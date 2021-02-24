This demo provides some web services running in local browser based on the Spring Boot framework. It is used to build index for `fasta` file with BWA/0.7.17-r1188 and show you the results. 

## Before start

It is necessary to complete the missing info in `Remote.java` , so you can access the designated server: 

```
 protected final String user = "";
 protected final String host = "";
 protected final int port = 0;
 protected final String password = "";
```

## Usage

### upload

After launched the program, you will see the upload page at: http://localhost:8080/indexUpload .

Make sure the file you want to build index has been saved in a specified path called "E:\\Cache\\". And now just press the first button to select the `fasta` format file you prepared and submit it to a server which deployed BWA software. 

### download

The processed file is showed at: http://localhost:8080/indexDownload .

In this page, you can see all the result files listing in user's directory. Select the file you want to download and input its name (including suffix) into the text box and click the "download" button. 

#### Note

Unfortunately tell you that the download function is not work perfectly yet. And I'm very sorry to make you use it so troublesome. It will be better soon. 
