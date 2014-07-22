package org.evolvis.veraweb.onlinereg;

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
