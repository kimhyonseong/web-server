package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
  private static Logger log = LoggerFactory.getLogger(HttpResponse.class);

  private String line;
  private OutputStream os;
  private Map<String, String> header = new HashMap<>();

  public HttpResponse() {}

  public HttpResponse(OutputStream os) {
    try {
      this.os = os;
      this.os.write("".getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  public void forward(String fileName) {
    this.line = "HTTP/1.1 200 OK\r\n";
    this.header.put("Content-Type","text/html;charset=utf-8");
    this.header.put("Content-Length","");

    try {
      this.os.write(this.line.getBytes(StandardCharsets.UTF_8));

      for (Map.Entry<String,String> map : header.entrySet()) {
        this.os.write(Integer.parseInt(map.getKey()+": "+map.getValue()+"\r\n"));
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  // 302
  public void sendRedirect(String fileName) throws IOException {
    this.os.write("HTTP/1.1 302 Redirect \r\n".getBytes(StandardCharsets.UTF_8));
    this.os.write(Integer.parseInt("Location: "+fileName+" \r\n"));
    this.os.write(Integer.parseInt("\r\n"));
  }

  public void addHeader(String key, Object value) {
    this.header.put(key, value.toString());
  }
}
