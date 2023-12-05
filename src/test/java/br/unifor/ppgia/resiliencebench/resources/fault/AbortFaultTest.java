package br.unifor.ppgia.resiliencebench.resources.fault;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbortFaultTest {

  @Test
  public void testToString() {
    assertEquals("abort-500", new AbortFault(500).toString());
  }
}