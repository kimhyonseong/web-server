package util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class UUIDTest {
  private static final Logger log = LoggerFactory.getLogger(UUIDTest.class);
  @Test
  public void createId() {
    UUID uuid = UUID.randomUUID();
    log.info("create id : {}",uuid);
  }
}
