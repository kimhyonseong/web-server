package util;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class RequestLineTest {
  @Test
  public void createMethod() {
    RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
    assertEquals("GET",line.getMethod());
    assertEquals("/index.html",line.getPath());

    line =  new RequestLine("POST /index.html HTTP/1.1");
    assertEquals("/index.html", line.getPath());
  }

  @Test
  public void createPathAndParams() {
    RequestLine line = new RequestLine("GET /user/create?userId=javajigi&password=pass HTTP/1.1");
    assertEquals("GET",line.getMethod());
    assertEquals("/user/create",line.getPath());

    Map<String,String> params = line.getParams();
    assertEquals(2,params.size());
    assertEquals("pass",params.get("password"));
  }
}