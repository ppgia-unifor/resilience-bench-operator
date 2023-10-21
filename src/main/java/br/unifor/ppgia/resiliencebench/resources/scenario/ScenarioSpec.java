package br.unifor.ppgia.resiliencebench.resources.scenario;

import br.unifor.ppgia.resiliencebench.resources.Fault;
import br.unifor.ppgia.resiliencebench.resources.ResilientService;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ScenarioSpec {

    @JsonIgnore
    private String id;
    private int round;
    private String name;
    private Workload workload;
    private Fault fault;
    private List<ResilientService> services = new ArrayList<>();

    public ScenarioSpec() { }

    public ScenarioSpec(String name, int round, Workload workload, Fault fault) {
        this();
        this.round = round;
        this.name = name;
        this.workload = workload;
        this.fault = fault;
    }

    public String getId() {
        if (id == null) {
            this.id = UUID.randomUUID().toString().substring(0, 5).toLowerCase();
        }
        return "%s.r%s.w%s-%s".formatted(getName(), getRound(), getWorkload().getUsers(), this.id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Workload getWorkload() {
        return workload;
    }

    public void setWorkload(Workload workload) {
        this.workload = workload;
    }

    public Fault getFault() {
        return fault;
    }

    public void setFault(Fault fault) {
        this.fault = fault;
    }

    public List<ResilientService> getServices() {
        return services;
    }

    public void setServices(List<ResilientService> services) {
        this.services = services;
    }

    public void addServices(Collection<ResilientService> services) {
        getServices().addAll(services);
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }


}
