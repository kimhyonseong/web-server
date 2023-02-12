import org.junit.Test;
import util.MyHttpResponse;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class HttpResponseTest {
  private final String testDir = "./src/test/resources/";

  @Test
  public void responseForward() throws Exception {
    MyHttpResponse response = new MyHttpResponse(createOutputStream("Http_Forward.txt"));
    response.forward("/index.html");
  }

  @Test
  public void responseRedirect() throws Exception {
    MyHttpResponse response = new MyHttpResponse(createOutputStream("Http_Redirect.txt"));
    response.sendRedirect("/index.html");
  }

  @Test
  public void responseCookies() throws Exception {
    MyHttpResponse response = new MyHttpResponse(createOutputStream("Http_Cookie.txt"));
    response.addHeader("Set-Cookie", "logined=true");
    response.sendRedirect("/index.html");
  }

  private OutputStream createOutputStream(String fileName) throws FileNotFoundException {
    return new FileOutputStream(testDir+fileName);
  }
}
