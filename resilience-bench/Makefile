test:
	mvn clean compile -B operator/pom.xml
	find ./operator/target/classes/META-INF/fabric8 -type f -name "*-v1.yml" | xargs -I {} kubectl apply -f {}
	mvn test -B operator/pom.xml

deploy:
	find ./operator/target/classes/META-INF/fabric8 -type f -name "*-v1.yml" | xargs -I {} kubectl apply -f {}


clean:
	@kubectl delete scenario --all --all-namespaces
	@kubectl delete job --all --all-namespaces
	@kubectl delete queues --all --all-namespaces
	@kubectl delete benchmark --all --all-namespaces