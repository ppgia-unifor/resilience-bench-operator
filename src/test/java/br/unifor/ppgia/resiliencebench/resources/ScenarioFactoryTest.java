package br.unifor.ppgia.resiliencebench.resources;

import br.unifor.ppgia.resiliencebench.resources.benchmark.*;
import br.unifor.ppgia.resiliencebench.resources.fault.AbortFault;
import br.unifor.ppgia.resiliencebench.resources.fault.DelayFault;
import br.unifor.ppgia.resiliencebench.resources.scenario.ScenarioFaultTemplate;
import br.unifor.ppgia.resiliencebench.resources.scenario.ScenarioSpec;
import br.unifor.ppgia.resiliencebench.resources.scenario.ScenarioWorkload;
import org.junit.jupiter.api.Test;

import br.unifor.ppgia.resiliencebench.resources.scenario.Scenario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ScenarioFactoryTest {
    
    @Test
    public void testCreate() {
        // create a mock benchmark object
//        Benchmark benchmark = mock(Benchmark.class);
//        BenchmarkSpec benchmarkSpec = mock(BenchmarkSpec.class);
//        Connection connection = mock(Connection.class);
//        Target target = mock(Target.class);
//        Source source = mock(Source.class);
//        BenchmarkFaultTemplate fault = mock(BenchmarkFaultTemplate.class);
//        Map<String, Object> sourcePatternsParameters = mock(Map.class);
//        ScenarioWorkload workload = mock(ScenarioWorkload.class);
//        ScenarioFaultTemplate faultTemplate = mock(ScenarioFaultTemplate.class);
//        ScenarioSpec scenarioSpec = mock(ScenarioSpec.class);
//        Scenario scenario = mock(Scenario.class);
//
//        // set up the mock objects
//        when(benchmark.getSpec()).thenReturn(benchmarkSpec);
//        when(benchmarkSpec.getWorkload()).thenReturn("workload");
//        when(benchmarkSpec.getConnections()).thenReturn(Arrays.asList(connection));
//        when(connection.getTarget()).thenReturn(target);
//        when(connection.getSource()).thenReturn(source);
//        when(target.getService()).thenReturn("targetService");
//        when(target.getFault()).thenReturn(fault);
//        when(fault.getPercentage()).thenReturn(Arrays.asList(10, 20));
//        when(fault.getDelay()).thenReturn(new DelayFault(1000));
//        when(source.getService()).thenReturn("sourceService");
//        when(sourcePatternsParameters.getPatterns()).thenReturn(Arrays.asList("pattern1", "pattern2"));
//        when(workload.getName()).thenReturn("workload");
//        when(workload.getUsers()).thenReturn(1);
//        when(faultTemplate.getPercentage()).thenReturn(10);
//        when(faultTemplate.getDelay()).thenReturn(1000);
//        when(faultTemplate.isAbort()).thenReturn(false);
//        when(scenarioSpec.getTargetService()).thenReturn("targetService");
//        when(scenarioSpec.getSourceService()).thenReturn("sourceService");
//        when(scenarioSpec.getSourcePatternsParameters()).thenReturn(sourcePatternsParameters);
//        when(scenarioSpec.getWorkload()).thenReturn(workload);
//        when(scenarioSpec.getFault()).thenReturn(faultTemplate);
//        when(scenario.getSpec()).thenReturn(scenarioSpec);
//
//        // call the method being tested
//        List<Scenario> scenarios = ScenarioFactory.create(benchmark);
//
//        // assert that the scenarios were created correctly
//        assertEquals(4, scenarios.size());
//        assertEquals(scenario, scenarios.get(0));
//        assertEquals(scenario, scenarios.get(1));
//        assertEquals(scenario, scenarios.get(2));
//        assertEquals(scenario, scenarios.get(3));
    }
}