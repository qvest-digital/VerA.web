package org.evolvis.veraweb.onlinereg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Data
public class Config extends Configuration {

    @JsonProperty
    private String verawebEndpoint;

    @JsonProperty
    private OsiamConfig osiam;

    @Valid
    @NotNull
    @JsonProperty
    private JerseyClientConfiguration jerseyClientConfiguration = new JerseyClientConfiguration();

}
