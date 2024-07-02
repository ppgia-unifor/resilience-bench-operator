# resilience-bench operator

A Kubernetes Operator developed using the Java Operator Framework.

## Prerequisites

Before you begin development, ensure you have the following prerequisites installed and configured on your system:

- **Java JDK 17**: Required for developing Java applications. Ensure JAVA_HOME is set to the JDK's installation directory.
- **Maven**: Used for project build and dependency management. Verify its installation by running `mvn -v` in your terminal.
- **Docker**: Necessary for building and pushing container images.
- **kubectl**: The Kubernetes command-line tool, used to interact with your Kubernetes cluster.
- **A Kubernetes Cluster**: You need an accessible Kubernetes cluster where the operator will be deployed.
- **k6-Operator**: This custom operator must be installed in your Kubernetes cluster before developing this project.
- **Istio**: Ensure Istio is installed and properly configured in your Kubernetes cluster to manage network traffic.

## Installation

### Installing Prerequisites in Your Cluster

Before starting development, install the k6-Operator and Istio into your Kubernetes cluster by following their respective installation guides:

- **k6-Operator Installation Guide**: [k6 Operator](https://github.com/grafana/k6-operator)
- **Istio Installation Guide**: [Istio Installation Documentation](https://istio.io/latest/docs/setup/install/)

Please replace the URLs with the actual links to the installation guides for k6-Operator and Istio.

### Project Setup

1. Clone the repository to your local machine:

   ```bash
   git clone https://github.com/ppgia-unifor/resilience-bench-operator.git
   cd resilience-bench-operator/resilience-bench
   ```

2. Compile the operator and install the CRDs:

   ```bash
   mvn clean install
   make deploy
   ```

### Custom Resource Definitions (CRDs)

Custom Resource Definitions (CRDs) allow you to define custom resources in Kubernetes. In this project, the following CRDs are defined:

- **Benchmark**: Represents a benchmarking test configuration. It includes properties such as the number of rounds, workload specifications, and scenarios.
- **Queue**: Represents a queue of benchmarking scenarios to be executed. It tracks the progress and status of each scenario.
- **ResilientService**: Represents a service that is part of a resilience test. It includes selectors for identifying the services in the cluster.
- **Scenario**: Defines the specific resilience test scenarios, including fault injections and service patterns.
- **Workload**: Defines the workload configurations for resilience tests, including the duration, target URLs, and user configurations.

### Understanding the Custom Resource Definitions (CRDs)

- **Benchmark CRD**:
   - **connections**: Defines the connections between services, including fault injection settings like abort and delay.
   - **rounds**: Specifies the number of rounds to execute the benchmark.
   - **workload**: Defines the workload to be applied during the benchmark.
   - **status**: Tracks the execution status of the benchmark, including the number of executions and total executions.

- **Queue CRD**:
   - **benchmark**: References the benchmark associated with the queue.
   - **items**: Lists the scenarios in the queue, along with their status (e.g., running, pending, completed).
   - **status**: Can be used to track the overall status of the queue.

- **ResilientService CRD**:
   - **selector**: Specifies the labels used to select the services that are part of the resilience test.
   - **status**: Can be used to track the health and status of the resilient service.

- **Scenario CRD**:
   - **fault**: Defines the fault injection settings, such as HTTP abort status and delay duration.
   - **patternConfig**: Allows specifying custom patterns for service interactions.
   - **sourceServiceName**: The name of the source service in the scenario.
   - **targetServiceName**: The name of the target service in the scenario.
   - **workload**: Specifies the workload configuration for the scenario.

- **Workload CRD**:
   - **cloud**: Contains cloud-specific settings, such as project ID and token.
   - **duration**: The duration of the workload in seconds.
   - **script**: Defines the script to be used for generating the workload.
   - **targetUrl**: The target URL for the workload.
   - **users**: Specifies the number of users for the workload.
   - **options**: Additional options for configuring the workload.


### Applying the CRDs and Deploying Resources

1. First, apply the CRDs from the `resilience-bench-operator-env` repository. This repository contains the necessary CRD definitions and additional configuration files.

   ```bash
   git clone https://github.com/ppgia-unifor/resilience-bench-operator-env.git
   cd resilience-bench-operator-env
   kubectl apply -f crd/
   ```

   This command applies all the CRD YAML files located in the `crd` directory, registering the custom resources with the Kubernetes API server.

2. Apply the remaining resources defined in the `httpbin` directory:

   ```bash
   kubectl apply -k httpbin
   ```

   This command uses `kubectl kustomize` to apply all the configurations defined in the `kustomization.yaml` file within the `httpbin` directory. This includes deployments, services, and other Kubernetes resources necessary for the example setup.

3. Then, switch to the `resilience-bench-operator` project and deploy the operator:

   ```bash
   cd ../resilience-bench-operator/resilience-bench
   make deploy
   ```

   The `make deploy` command builds and deploys the operator to your Kubernetes cluster. It ensures that the operator is running and ready to manage the custom resources defined by the CRDs.

4. Finally, compile the operator with Maven. This step is necessary to ensure all dependencies are resolved and the operator is correctly packaged:

   ```bash
   cd operator
   mvn clean install -DskipTests -T12
   ```

   This Maven command cleans any previous builds, compiles the project, and installs the packages, skipping the tests to speed up the process.

### Accessing Test Results

To access the results of your benchmarks, you can use a temporary pod to explore the contents of the Persistent Volume Claim (PVC) where the results are stored.

1. **Create a Temporary Pod**

   Use the following command to create a temporary pod that mounts the PVC:

   ```bash
   kubectl run tmp-shell --rm -i --tty --image busybox --namespace <namespace> --overrides='
   {
     "apiVersion": "v1",
     "kind": "Pod",
     "metadata": {
       "name": "tmp-shell"
     },
     "spec": {
       "containers": [
         {
           "name": "shell",
           "image": "busybox",
           "command": ["/bin/sh"],
           "volumeMounts": [
             {
               "mountPath": "/results",
               "name": "test-results"
             }
           ],
           "stdin": true,
           "tty": true
         }
       ],
       "volumes": [
         {
           "name": "test-results",
           "persistentVolumeClaim": {
             "claimName": "test-results"
           }
         }
       ]
     }
   }'
   ```

   This command creates a temporary pod named `tmp-shell` that mounts the `test-results` PVC at the `/results` directory. The pod uses the `busybox` image and opens an interactive shell.

2. **Explore the Results**

   Once inside the pod, navigate to the directory where the results are stored and list the files:

   ```sh
   cd /results
   ls -l
   ```

   To view the contents of a specific result file:

   ```sh
   cat <result-file-name>.json
   ```

   Replace `<result-file-name>.json` with the actual name of the result file you wish to inspect.

## Contributing

We welcome contributions! Please read our Contributing Guide for details on how to submit pull requests to the project.

## License

This project is licensed under the MIT License - see the [LICENSE](license.md) file for details.