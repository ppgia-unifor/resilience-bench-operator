package br.unifor.ppgia.resiliencebench.resources.benchmark;

import io.fabric8.kubernetes.model.annotation.PrinterColumn;

public class BenchmarkStatus {

    @PrinterColumn
    private final int totalExecutions;
    @PrinterColumn
    private int executions;

    public BenchmarkStatus(int totalExecutions) {
        this.totalExecutions = totalExecutions;
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
