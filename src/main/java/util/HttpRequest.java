package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {
  private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

  private BufferedReader br;
  private String line;
  private String method;
  private String path;
  private Map<String,String> header = new HashMap<>();
  private Map<String,String> parameter = new HashMap<>();

  public HttpRequest() {}

  public HttpRequest(InputStream is) {
    this.br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    setLine();
    setHeader();
    setParameter();
  }

  public String[] parseStr(String str, String regex) {
    if (str == null || str.length() < 1 || regex == null) {
      return null;
    }
    return str.split(regex);
  }

  public void setLine() {
    try {
      this.line = this.br.readLine();
      String[] splits = parseStr(this.line," ");

      if (splits != null && splits.length > 1) {
        this.method = splits[0];
        this.path = splits[1];
      }
    } catch (IOException e) {
      log.error("line error : {}",e.getMessage());
    }
  }

  public String getMethod() {
    return this.method;
  }

  public String getPath() {
    return this.path.split("\\?")[0];
  }

  public void setParameter() {
    String queryString = "";

    try {
      if (getMethod().equals("GET")) {
        int index = this.path.indexOf("?");
        queryString = this.path.substring(index + 1);
      }

      if (getMethod().equals("POST")) {
        queryString = this.br.readLine();
        System.out.println("queryString : "+queryString);
      }

      if (Objects.requireNonNull(queryString).length() > 0) {
        String[] params = queryString.split("&");

        for (String param : params) {
          String[] splits = param.split("=");

          if (splits.length > 1) {
            this.parameter.put(splits[0], splits[1]);
          }
        }
      }
    } catch (IOException e) {
      log.error("set parameter error : {}", e.getMessage());
    }
  }

  public String getParameter(String key) {
    // java 1.8
    return this.parameter.getOrDefault(key, null);
  }

  public void setHeader() {
    try {
      while (!this.line.equals("")) {
        this.line = this.br.readLine();
        String[] parse = parseStr(this.line,":");

        log.debug("this.line {}",this.line);

        if (parse != null && parse.length > 1) {
          this.header.put(parse[0].trim(), parse[1].trim());
        }
        
        if (this.line == null) {
          return;
        }
      }
    } catch (IOException e) {
      log.error("set pa");
    }
  }

  public String getHeader(String key) {
    return this.header.getOrDefault(key, null);
  }
}
