package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class MyHttpResponse {
  private final String dir = "./webapp";
  private static Logger log = LoggerFactory.getLogger(MyHttpResponse.class);

  private String line;
  private DataOutputStream os;
  private Map<String, String> header = new HashMap<>();

  public MyHttpResponse() {}

  public MyHttpResponse(OutputStream os) {
    this.os = new DataOutputStream(os);
  }

  public void forward(String fileName) {
    try {
      this.line = "HTTP/1.1 200 OK\r\n";

      String[] ext = fileName.split("\\.");
      addHeader("Content-Type","text/"+ext[1]+";charset=utf-8");

      byte[] length = Files.readAllBytes(new File(dir+fileName).toPath());
      addHeader("Content-Length",String.valueOf(length.length));

      outStreamWrite();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  // 302
  public void sendRedirect(String fileName) {
    this.line = "HTTP/1.1 302 Redirect \n";
    addHeader("Location",fileName);

    outStreamWrite();
  }

  public void addHeader(String key, Object value) {
    this.header.put(key, value.toString());
  }

  private void outStreamWrite() {
    try {
      this.os.writeBytes(this.line);
      for (Map.Entry<String, String> map : header.entrySet()) {
        this.os.writeBytes(map.getKey() + ": " + map.getValue() + "\r\n");
      }
      this.os.writeBytes("\r\n");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
