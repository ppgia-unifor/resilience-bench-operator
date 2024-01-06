test:
	mvn clean compile
	find ./target/classes/META-INF/fabric8 -type f -name "*-v1.yml" | xargs -I {} kubectl apply -f {}
	mvn test

deploy:
	find ./target/classes/META-INF/fabric8 -type f -name "*-v1.yml" | xargs -I {} kubectl apply -f {}
