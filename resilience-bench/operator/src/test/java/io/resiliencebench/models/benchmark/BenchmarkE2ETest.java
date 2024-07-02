//package io.resiliencebench.models.benchmark;
//
//import io.fabric8.kubernetes.api.model.Namespace;
//import io.fabric8.kubernetes.api.model.NamespaceBuilder;
//import io.fabric8.kubernetes.client.KubernetesClient;
//import io.fabric8.kubernetes.client.dsl.MixedOperation;
//import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
//import io.fabric8.kubernetes.client.dsl.Resource;
//import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
//import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
//import io.resiliencebench.controllers.BenchmarkReconciler;
//import io.resiliencebench.controllers.ResilienceServiceReconciler;
//import io.resiliencebench.models.benchmark.Benchmark;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.InputStream;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//public class BenchmarkE2ETest {
//
//	@BeforeAll
//	public static void setUpClass() {
//		System.setProperty("AWS_ACCESS_KEY", "test");
//		System.setProperty("AWS_SECRET_KEY", "test");
//	}
//
//	@Autowired
//	private BenchmarkReconciler benchmarkReconciler;
//
//	@Autowired
//	private ResilienceServiceReconciler resilienceServiceReconciler;
//
//	@Autowired
//	private KubernetesClient kubernetesClient;
//
//	static AbstractOperatorExtension operator;
//
//	@BeforeEach
//	public void setUp() {
//		if (operator == null) {
//			operator = LocallyRunOperatorExtension.builder()
//					.waitForNamespaceDeletion(true)
//					.oneNamespacePerClass(true)
//					.withReconciler(resilienceServiceReconciler)
//					.withReconciler(benchmarkReconciler)
//					.build();
//		}
//
//		// Create namespace for the test
//		Namespace namespace = new NamespaceBuilder()
//				.withNewMetadata()
//				.withName("test-namespace")
//				.endMetadata()
//				.build();
//		kubernetesClient.namespaces().resource(namespace).serverSideApply();
//	}
//
//	@Test
//	@DisplayName("Test Benchmark creation")
//	public void creationTest() {
//		// Arrange
//		InputStream resourceStream = getClass().getResourceAsStream("/benchmark-sample.yaml");
//		MixedOperation<Benchmark, ?, Resource<Benchmark>> benchmarkClient = kubernetesClient.resources(Benchmark.class);
//
//		// Ensure the namespace is set
//		Benchmark benchmark = benchmarkClient.load(resourceStream).get();
//		benchmark.getMetadata().setNamespace("test-namespace"); // Set namespace explicitly
//
//		// Act
//		Benchmark created = benchmarkClient.inNamespace("test-namespace").resource(benchmark).create();
//
//		// Assert
//		assertNotNull(created);
//	}
//}
