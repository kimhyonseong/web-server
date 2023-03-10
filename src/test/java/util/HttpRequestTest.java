package util;

import http.HttpRequest;
import http.MyHttpRequest;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class HttpRequestTest {
  private final String testDir = "./src/test/resources/";

  @Test
  public void requestGet() throws FileNotFoundException {
    InputStream in = new FileInputStream(testDir+"Http_GET.txt");
    HttpRequest request = new HttpRequest(in);

    assertEquals(HttpMethod.GET,request.getMethod());
    assertEquals("/user/create",request.getPath());
    assertEquals("keep-alive",request.getHeader("Connection"));
    assertEquals("javajigi",request.getParameter("userId"));
  }

  @Test
  public void requestPost() throws Exception{
    InputStream in = new FileInputStream(testDir+"Http_POST.txt");
    MyHttpRequest request = new MyHttpRequest(in);

    assertEquals("POST",request.getMethod());
    assertEquals("/user/create",request.getPath());
    assertEquals("keep-alive",request.getHeader("Connection"));
    assertEquals("javajigi",request.getParameter("userId"));
  }
}
