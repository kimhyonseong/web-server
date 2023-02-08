package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = br.readLine();

            if (line == null) return;
            String method = HttpRequestUtils.getMethod(line);
            String url = HttpRequestUtils.getUrl(line);
            Map<String,String> headers = new HashMap<>();

            if (url.equals("/")) url = "/index.html";

            while (!"".equals(line)) {
                log.debug("header : {}",line);
                line = br.readLine();
                String[] headerTokens = line.split(":");

                if (headerTokens.length == 2)
                    headers.put(headerTokens[0].trim(),headerTokens[1].trim());
            }

            log.debug("Content-Length : {}",headers.get("Content-Length"));
            DataOutputStream dos = new DataOutputStream(out);
            Map<String, Object> headerInfo = new HashMap<>();
            headerInfo.put("code",200);
            headerInfo.put("codeStr","OK");
            headerInfo.put("Accept",headers.get("Accept"));
            headerInfo.put("Sec-Fetch-Dest",headers.get("Sec-Fetch-Dest"));

            if (method.equals("POST")) {
                String requestBody = IOUtils.readData(br,Integer.parseInt(headers.get("Content-Length")));
                log.debug("Request Body : {}",requestBody);
                Map<String, String> params = HttpRequestUtils.parseQueryString(requestBody);

                if (url.endsWith("create")) {
                    User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                    log.debug("User : {}",user);

                    DataBase.addUser(user);
                } else if(url.endsWith("login")) {
                    User user = DataBase.findUserById(params.get("userId"));
                    
                    headerInfo.put("cookie",setCookie(user, params));
                }

                headerInfo.put("code",302);
                headerInfo.put("codeStr","Found");
                headerInfo.put("returnUrl","/index.html");
            }
            responseHeader(dos, headerInfo);

            File file = new File("./webapp"+url);

            if (file.exists()) {
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                headerInfo.put("contentLength", body.length);
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String setCookie(User user, Map<String, String> params) {
        String returnStr = "";

        if (user == null) {
            log.debug("User Not Found");
        } else if (user.getPassword().equals(params.get("password"))) {
            log.debug("login success");
            returnStr = "userId="+params.get("userId");
        } else {
            log.debug("Password Mismatch");
        }

        return returnStr;
    }

    private void responseHeader(DataOutputStream dos, Map<String,Object> headerInfo) {
        try {
            dos.writeBytes("HTTP/1.1 "+headerInfo.get("code")+" "+headerInfo.get("codeStr")+" \r\n");
            dos.writeBytes("Content-Type: "+getContentType(headerInfo)+";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + headerInfo.get("contentLength") + "\r\n");

            if (headerInfo.containsKey("returnUrl"))
                dos.writeBytes("Location: "+headerInfo.get("returnUrl")+"\r\n");

            if (headerInfo.containsKey("cookie"))
                dos.writeBytes("Set-Cookie: "+headerInfo.get("cookie")+"; Domain=localhost; path=/\r\n");

            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getContentType(Map<String,Object> headerInfo) {
        String contentType = headerInfo.get("Accept").toString().split(",")[0];

        if (headerInfo.get("Sec-Fetch-Dest").toString().contains("script"))
            contentType = "application/javascript";

        return contentType;
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
