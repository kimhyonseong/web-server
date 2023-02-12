package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequest;
import util.HttpRequestUtils;
import util.HttpResponse;
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

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            String url = getDefaultUrl(request.getPath());

            if ("/user/create".equals(url)) {
                User user = new User(
                        request.getParameter("userId"),
                        request.getParameter("password"),
                        request.getParameter("name"),
                        request.getParameter("email"));
                log.debug("user : {}", user);
                DataBase.addUser(user);
                response.sendRedirect("/index.html");
            } else if ("/user/login".equals(url)) {
                User user = DataBase.findUserById(request.getParameter("userId"));
                if (user != null) {
                    if (user.login(request.getParameter("password"))) {
                        response.addHeader("Set-Cookie","logined=true");
                    } else {
                        response.sendRedirect("/user/login_failed.html");
                    }
                } else {
                    response.sendRedirect("/user/login_failed.html");
                }
            } else if ("/user/list".equals(url)) {
                if (!isLogin(request.getHeader("Cookie"))) {
                    response.sendRedirect("/user/login.html");
                    return;
                }

                Collection<User> users = DataBase.findAll();
                StringBuilder sb = new StringBuilder();
                sb.append("<table border='1'>");
                for (User user : users) {
                    sb.append("<tr>");
                    sb.append("<td>").append(user.getUserId()).append("</td>");
                    sb.append("<td>").append(user.getName()).append("</td>");
                    sb.append("<td>").append(user.getEmail()).append("</td>");
                    sb.append("</tr>");
                }
                sb.append("</table>");
                response.forwardBody(sb.toString());
            } else {
                response.forward(url);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultUrl(String url) {
        if (url.equals("/")) {
            url = "/index.html";
        }
        return url;
    }

    private boolean isLogin(String line) {
        String[] headerTokens = line.split(":");
        Map<String,String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
        String value = cookies.get("logined");

        if (value == null) return false;

        return Boolean.getBoolean(value);
    }
}
