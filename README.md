# ResilienceBench Operator

ResilienceBench is a language-agnostic benchmark environment to support the experimental evaluation of microservice resiliency patterns, such as Retry and Circuit Breaker. Consider a microservices-based application, where the connectors represent communication between the different services. Each service is subject to various failure possibilities and workload variations, and for each of these situations, there is an appropriate configuration of resilience patterns. In this context, the tool's purpose is to automate the creation of test scenarios, exhaustively evaluating all possible scenarios under different load levels and failure conditions with minimal configuration.

## Prerequisites

Before you begin development, ensure you have the following prerequisites installed and configured on your system:

- **Java JDK 17**: Required for developing Java applications. Ensure JAVA_HOME is set to the JDK's installation directory.
- **Maven**: Used for project build and dependency management. Verify its installation by running `mvn -v` in your terminal.
- **Docker**: Necessary for building and pushing container images.
- **kubectl**: The Kubernetes command-line tool, used to interact with your Kubernetes cluster.
- **A Kubernetes Cluster**: You need an accessible Kubernetes cluster where the operator will be deployed.
- **Istio**: Ensure Istio is installed and properly configured in your Kubernetes cluster to manage network traffic.

## Installation

### Installing Prerequisites in Your Cluster

Before starting development, install the k6-Operator and Istio into your Kubernetes cluster by following their respective installation guides:

- **k6-Operator Installation Guide**: [k6 Operator](https://github.com/grafana/k6-operator)
- **Istio Installation Guide**: [Istio Installation Documentation](https://istio.io/latest/docs/setup/install/)

Please replace the URLs with the actual links to the installation guides for k6-Operator and Istio.

### Project Setup

1. Clone the repository to your local machine and install it:

   ```bash
   git clone https://github.com/ppgia-unifor/resilience-bench-operator.git
   cd resilience-bench-operator/resilience-bench
   mvn clean install
   make deploy
   ```

## Contributing

We welcome contributions! Please read our Contributing Guide for details on how to submit pull requests to the project.

## License

This project is licensed under the MIT License - see the [LICENSE](license.md) file for details.
