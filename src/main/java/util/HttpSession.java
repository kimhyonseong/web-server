package util;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
  private Map<String,Object> values = new HashMap<>();
  private final String id;

  public HttpSession(String id) {
    this.id = id;
  }

  public void setAttribute(String name,Object value) {
    values.put(name,value);
  }

  public Object getAttribute(String name) {
    return values.get(name);
  }

  public String getId() {
    return id;
  }

  public void removeAttribute(String name) {
    values.remove(name);
  }

  public void invalidate() {
    values.clear();
  }
}
