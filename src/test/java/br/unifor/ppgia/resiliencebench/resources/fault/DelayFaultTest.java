package br.unifor.ppgia.resiliencebench.resources.fault;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DelayFaultTest {

  @Test
  public void testToString() {
    assertEquals("delay-100ms", new DelayFault(100).toString());
  }
}