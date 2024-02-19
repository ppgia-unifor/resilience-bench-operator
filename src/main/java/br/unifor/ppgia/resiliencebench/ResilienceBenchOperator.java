package br.unifor.ppgia.resiliencebench;

public class ResilienceBenchOperator {

    public static void main(String[] args) {
        var operator = new io.javaoperatorsdk.operator.Operator();
        operator.register(new BenchmarkReconciler());
        operator.register(new ScenarioReconciler());
        operator.start();
    }
}
