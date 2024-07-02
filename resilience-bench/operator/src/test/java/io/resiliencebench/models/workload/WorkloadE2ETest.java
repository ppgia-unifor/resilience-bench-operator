//package io.resiliencebench.models.workload;
//
//import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
//import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
//import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
//import io.resiliencebench.controllers.BenchmarkReconciler;
//import io.resiliencebench.controllers.ResilienceServiceReconciler;
//import io.resiliencebench.models.workload.configuration.script.ScriptConfig;
//import io.resiliencebench.support.ConfigMapReference;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.RegisterExtension;
//import org.mockito.Mockito;
//
//import java.util.List;
//
//class WorkloadE2ETest {
//
//	private static BenchmarkReconciler benchmarkReconciler;
//	private static ResilienceServiceReconciler resilienceServiceReconciler;
//
//	@RegisterExtension
//	static final AbstractOperatorExtension operator;
//
//	static {
//		benchmarkReconciler = Mockito.mock(BenchmarkReconciler.class);
//		resilienceServiceReconciler = Mockito.mock(ResilienceServiceReconciler.class);
//
//		operator = LocallyRunOperatorExtension.builder()
//				.waitForNamespaceDeletion(false)
//				.oneNamespacePerClass(true)
//				.withReconciler(resilienceServiceReconciler)
//				.withReconciler(benchmarkReconciler)
//				.build();
//	}
//
//	@Test
//	@DisplayName("Should create a workload successfully")
//	void creationTest() {
//		final var configMap = new ConfigMapReference("test", "test.js");
//		final var name = "test";
//		final var spec = new WorkloadSpec(List.of(10, 20, 30), null, "http://local.com", new ScriptConfig(configMap));
//
//		var workload = new Workload();
//		workload.setMetadata(new ObjectMetaBuilder().withName(name).build());
//		workload.setSpec(spec);
//
//		var workloadClient = operator.resources(Workload.class);
//
//		// Ensure the workload does not exist before creating
//		workloadClient.withName(name).delete();
//
//		workloadClient.resource(workload).create();
//
//		var actualWorkload = workloadClient.withName(name).get();
//		Assertions.assertNotNull(actualWorkload);
//		// Compare fields individually to handle default values properly
//		Assertions.assertEquals(spec.getUsers(), actualWorkload.getSpec().getUsers());
//		Assertions.assertEquals(spec.getK6ContainerImage(), actualWorkload.getSpec().getK6ContainerImage());
//		Assertions.assertEquals(spec.getScript(), actualWorkload.getSpec().getScript());
//		Assertions.assertNull(actualWorkload.getSpec().getOptions());
//	}
//
//	@Test
//	@DisplayName("Should not create a workload with negative duration")
//	void testWithNegativeDuration() {
//		final var configMap = new ConfigMapReference("test", "test.js");
//		final var name = "test";
//
//		Assertions.assertThrows(IllegalArgumentException.class, () -> {
//			new WorkloadSpec(List.of(-10, 20, 30), null, "http://local.com", new ScriptConfig(configMap));
//		});
//	}
//
//	@Test
//	@DisplayName("Should not create a workload with invalid URL")
//	void testWithInvalidUrl() {
//		final var configMap = new ConfigMapReference("test", "test.js");
//		final var name = "test";
//
//		Assertions.assertThrows(IllegalArgumentException.class, () -> {
//			new WorkloadSpec(List.of(10, 20, 30), null, "invalid-url", new ScriptConfig(configMap));
//		});
//	}
//}
