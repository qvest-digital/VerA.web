package org.evolvis.veraweb.selfreg;

import com.yammer.metrics.core.HealthCheck;

public class Health extends HealthCheck {

    public Health() {
        super("DropwizardDemo-health");
    }

    @Override
    protected Result check() {
        return Result.healthy();
    }

}
