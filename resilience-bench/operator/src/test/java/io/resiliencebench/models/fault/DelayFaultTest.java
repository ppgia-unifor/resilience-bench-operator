package io.resiliencebench.models.fault;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DelayFaultTest {

  @Test
  public void testToString() {
    assertEquals("delay-100ms", new DelayFault(100).toString());
  }
}