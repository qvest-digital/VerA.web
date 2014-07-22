package org.evolvis.veraweb.onlinereg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Data
public class Config extends Configuration {

    @JsonProperty
    private String myConfigValue;
    @Valid
    @NotNull
    @JsonProperty
    private DatabaseConfiguration database = new DatabaseConfiguration();
}
