package br.unifor.ppgia.resiliencebench.resources.scenario;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.*;
import org.apache.commons.lang3.StringUtils;

@Group("resiliencebench.io")
@Version("v1")
@ShortNames("sc")
@Plural("scenarios")
@Kind("Scenario")
public class Scenario extends CustomResource<ScenarioSpec, ScenarioStatus> {

    public Scenario() {
    }

    public Scenario(ScenarioSpec spec) {
        super();
        this.spec = spec;
    }

    @Override
    public ObjectMeta getMetadata() {
        var meta = super.getMetadata();
        if (StringUtils.isEmpty(meta.getName())) {
            if (getSpec() != null) {
                meta.setName(getSpec().getId());
            }
        }
        return meta;
    }
}
