# Implementação para Troca de Variável de Ambiente no Kubernetes usando fabric8 SDK

Na abordagem discutida, optou-se por remover os pods antigos o mais rapidamente possível e aguardar a criação dos novos pods, uma vez que o tempo mínimo de downtime não é um requisito crítico no cenário de teste. Esse cenário envolve a atualização de variáveis de ambiente em um Deployment Kubernetes e, em seguida, a execução de testes que dependem dessas novas configurações.

## Atributos Manipulados no Rolling Update

- `maxSurge: 0:` Esse atributo controla o número de pods adicionais que podem ser criados além do número de réplicas desejadas durante a atualização. Definir esse valor como 0 garante que nenhum pod extra será criado antes da remoção dos pods antigos, forçando a troca completa.
- `maxUnavailable:` Esse atributo define quantos pods podem estar indisponíveis durante a atualização. Um valor mais alto, como 2, permite a remoção simultânea de múltiplos pods, o que acelera o processo de atualização.

Essa configuração garante que os pods antigos serão removidos rapidamente e que o sistema aguardará pela criação dos novos pods antes de continuar o processo.

## Importância de Esperar pelos Novos Pods e Remover os Antigos

A remoção rápida dos pods antigos é essencial neste caso, pois o cenário envolve testes que dependem da nova configuração. Após a remoção dos antigos, o código deve esperar até que os novos pods estejam completamente prontos para garantir que a configuração alterada tenha sido aplicada corretamente.

Esperar pelos novos pods evita condições de corrida em que pods antigos poderiam ser erroneamente considerados como prontos, comprometendo a validação dos novos parâmetros configurados.

## Como o Código Java Deve Esperar pelos Novos Pods

Para garantir que o código Java só continue após a criação e o estado "Ready" dos novos pods, utilizou-se o método waitUntilCondition do fabric8 SDK. Esse método é configurado para aguardar que o observedGeneration do Deployment seja igual à metadata.generation, confirmando que a atualização foi processada, e que o número de ReadyReplicas seja igual ao número de réplicas definidas no Deployment.

##Exemplo de código:

```java
Deployment deployment = client.apps().deployments().inNamespace("default").withName("my-deployment").edit()
    .editSpec()
        .editTemplate()
            .editSpec()
                .editFirstContainer()
                    .addToEnv(new EnvVarBuilder().withName("MY_ENV_VAR").withValue("new-value").build())
                .endContainer()
            .endSpec()
        .endTemplate()
    .endSpec()
    .done();

Long newGeneration = deployment.getMetadata().getGeneration();

client.apps().deployments().inNamespace("default").withName("my-deployment")
    .waitUntilCondition(d -> d.getStatus().getObservedGeneration() >= newGeneration
        && d.getStatus().getReadyReplicas() != null 
        && d.getStatus().getReadyReplicas().equals(d.getSpec().getReplicas()), 10, TimeUnit.MINUTES);
Esse código aguarda a conclusão da atualização, garantindo que os novos pods estão prontos antes de prosseguir.
````

## Impacto da Decisão

A decisão de maximizar a remoção dos pods antigos e aguardar pelos novos leva a um tempo de downtime mais longo, mas, como o objetivo é testar novos parâmetros, o tempo de indisponibilidade é aceitável e não prejudica o cenário. A principal prioridade é garantir que a atualização seja aplicada corretamente, permitindo que o teste ocorra com os parâmetros atualizados e sem riscos de executar com configurações antigas.