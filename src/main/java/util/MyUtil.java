package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MyUtil {
  private static final Logger log = LoggerFactory.getLogger(MyUtil.class);
  public MyUtil() {}

  public Map<String,String> getUrlInfo(InputStream in) {
    try {
      Map<String,String> map = new HashMap<>();
      InputStreamReader reader = new InputStreamReader(in);
      BufferedReader bufferedReader = new BufferedReader(reader);
      String line = bufferedReader.readLine();

      String[] token = line.split(" ");
      log.debug("Method : " + token[0]);
      log.debug("Url : " + token[1]);

      map.put("method",token[0]);
      map.put("url",token[1]);
      return map;
    } catch (IOException e) {
      log.error(e.getMessage());
      return null;
    }
  }

  public byte[] getBody(String fileName) {
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
