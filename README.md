# resilience-bench operator

A Kubernetes Operator developed using the Java Operator Framework.

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

Before starting development, install Istio into your Kubernetes cluster by following their respective installation guide:

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

## License

This project is licensed under the MIT License - see the [LICENSE](license.md) file for details.
