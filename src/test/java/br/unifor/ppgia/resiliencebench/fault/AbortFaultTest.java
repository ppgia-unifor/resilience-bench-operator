package br.unifor.ppgia.resiliencebench.fault;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbortFaultTest {

  @Test
  public void testToString() {
    assertEquals("abort-500", new AbortFault(500).toString());
  }
}