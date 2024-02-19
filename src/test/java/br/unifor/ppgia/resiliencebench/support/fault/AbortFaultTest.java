package br.unifor.ppgia.resiliencebench.support.fault;

import br.unifor.ppgia.resiliencebench.fault.AbortFault;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbortFaultTest {

  @Test
  public void testToString() {
    assertEquals("abort-500", new AbortFault(500).toString());
  }
}