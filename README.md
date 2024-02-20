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

- **k6-Operator Installation Guide**: [k6-Operator Installation Documentation](https://example.com/k6-operator-installation)
- **Istio Installation Guide**: [Istio Installation Documentation](https://istio.io/latest/docs/setup/install/)

Please replace the URLs with the actual links to the installation guides for k6-Operator and Istio.

### Project Setup

1. Clone the repository to your local machine and install it:

   ```bash
   git clone https://github.com/ppgia-unifor/resilience-bench-operator.git
   cd resilience-bench-operator
   mvn clean install
   make deploy
   ```

## Contributing

We welcome contributions! Please read our Contributing Guide for details on how to submit pull requests to the project.

## License

This project is licensed under the MIT License - see the [LICENSE](license.md) file for details.
