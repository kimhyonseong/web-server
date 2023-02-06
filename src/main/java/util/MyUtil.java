package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class MyUtil {
  private static final Logger log = LoggerFactory.getLogger(MyUtil.class);
  public MyUtil() {}

  public byte[] getBody(InputStream in) {
    String fileName = getUrl(in);
    return getFile(fileName);
  }

  public String getUrl(InputStream in) {
    try {
      InputStreamReader reader = new InputStreamReader(in);
      BufferedReader bufferedReader = new BufferedReader(reader);
      String line = bufferedReader.readLine();

      String[] token = line.split(" ");
      log.debug("Url : " + token[1]);
      return token[1];
    } catch (IOException e) {
      log.error(e.getMessage());
      return null;
    }
  }

  public byte[] getFile(String fileName) {
    try {
      Path path = null;
      File file = new File("./webapp" + fileName);

      if (file.exists()) path = file.toPath();
      if (path == null) throw new IOException("no file");

      return Files.readAllBytes(path);
    } catch (IOException e) {
      log.error(e.getMessage());
      return null;
    }
  }
}
