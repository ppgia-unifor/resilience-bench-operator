package br.unifor.ppgia.resiliencebench.resources.benchmark;

import io.fabric8.crd.generator.annotation.PrinterColumn;

public class BenchmarkStatus {

    @PrinterColumn
    private int totalExecutions;
    @PrinterColumn
    private int executions;

    public BenchmarkStatus() { }

    public BenchmarkStatus(int totalExecutions, int executions) {
        this.totalExecutions = totalExecutions;
        this.executions = executions;
    }

    public void incrementExecutions(int executions) {
        this.executions += executions;
    }

    public int getExecutions() {
        return executions;
    }

    public int getTotalExecutions() {
        return totalExecutions;
    }
}
