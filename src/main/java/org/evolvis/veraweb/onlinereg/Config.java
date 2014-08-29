package org.evolvis.veraweb.onlinereg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import lombok.Getter;
import org.evolvis.veraweb.onlinereg.osiam.OsiamConfig;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Configuration
 */
@Getter
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
