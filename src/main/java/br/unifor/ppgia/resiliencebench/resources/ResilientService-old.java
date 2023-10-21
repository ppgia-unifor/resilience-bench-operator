//package br.unifor.ppgia.resiliencebench.resources;
//
//import com.fasterxml.jackson.annotation.JsonAnyGetter;
//import com.fasterxml.jackson.annotation.JsonAnySetter;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.JsonNode;
//import io.fabric8.kubernetes.api.model.LabelSelector;
//
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class ResilientService {
//
//  private String namespace;
//  private String configuration;
//  private LabelSelector labelSelector;
//
//  @JsonInclude(JsonInclude.Include.NON_EMPTY)
//  private Map<String, JsonNode> patternConfig;
//
////  public ResilientService(String namespace, LabelSelector labelSelector, String configuration, Map<String, JsonNode> patternConfig) {
////    this.namespace = namespace;
////    this.configuration = configuration;
////    this.labelSelector = labelSelector;
////    this.patternConfig = patternConfig;
////    this.patternConfig = patternConfig.entrySet().stream()
////            .collect(Collectors.toMap(
////                    Map.Entry::getKey,
////                    entry -> entry.getValue().toString()
////            ));
////  }
//
//
//  public String getNamespace() {
//    return namespace;
//  }
//
//  public void setNamespace(String namespace) {
//    this.namespace = namespace;
//  }
//
//  public String getConfiguration() {
//    return configuration;
//  }
//
//  public void setConfiguration(String configuration) {
//    this.configuration = configuration;
//  }
//
//  public LabelSelector getLabelSelector() {
//    return labelSelector;
//  }
//
//  public void setLabelSelector(LabelSelector labelSelector) {
//    this.labelSelector = labelSelector;
//  }
//
//  @JsonAnyGetter
//  public Map<String, JsonNode> getPatternConfig() {
//    return patternConfig;
//  }
//
//  @JsonAnySetter
//  public void setPatternConfig(Map<String, JsonNode> patternConfig) {
//    this.patternConfig = patternConfig;
//  }
//}
