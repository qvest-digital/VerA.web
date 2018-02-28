package io.dropwizard.client;

/*-
 * Copyright 2010-2013 Coda Hale and Yammer, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import io.dropwizard.util.Duration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

// TODO: 5/15/13 <coda> -- write tests for HttpClientConfiguration

/**
 * The configuration class used by {@link HttpClientBuilder}.
 *
 * @see <a href="http://www.dropwizard.io/manual/client/#configuration-defaults">Http Client Configuration</a>
 */
public class HttpClientConfiguration {
    @NotNull
    private Duration timeout = Duration.milliseconds(500);

    @NotNull
    private Duration connectionTimeout = Duration.milliseconds(500);

    @NotNull
    private Duration timeToLive = Duration.hours(1);

    private boolean cookiesEnabled = false;

    @Min(1)
    @Max(Integer.MAX_VALUE)
    private int maxConnections = 1024;

    @Min(1)
    @Max(Integer.MAX_VALUE)
    private int maxConnectionsPerRoute = 1024;

    @NotNull
    private Duration keepAlive = Duration.milliseconds(0);

    @Min(0)
    @Max(1000)
    private int retries = 0;

    @NotNull
    private Optional<String> userAgent = Optional.absent();

    @JsonProperty
    public Duration getKeepAlive() {
        return keepAlive;
    }

    @JsonProperty
    public void setKeepAlive(Duration keepAlive) {
        this.keepAlive = keepAlive;
    }

    @JsonProperty
    public int getMaxConnectionsPerRoute() {
        return maxConnectionsPerRoute;
    }

    @JsonProperty
    public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    @JsonProperty
    public Duration getTimeout() {
        return timeout;
    }

    @JsonProperty
    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    @JsonProperty
    public Duration getTimeToLive() {
        return timeToLive;
    }

    @JsonProperty
    public boolean isCookiesEnabled() {
        return cookiesEnabled;
    }

    @JsonProperty
    public void setTimeout(Duration duration) {
        this.timeout = duration;
    }

    @JsonProperty
    public void setConnectionTimeout(Duration duration) {
        this.connectionTimeout = duration;
    }

    @JsonProperty
    public void setTimeToLive(Duration timeToLive) {
        this.timeToLive = timeToLive;
    }

    @JsonProperty
    public void setCookiesEnabled(boolean enabled) {
        this.cookiesEnabled = enabled;
    }

    @JsonProperty
    public int getMaxConnections() {
        return maxConnections;
    }

    @JsonProperty
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    @JsonProperty
    public int getRetries() {
        return retries;
    }

    @JsonProperty
    public void setRetries(int retries) {
        this.retries = retries;
    }

    @JsonProperty
    public Optional<String> getUserAgent() {
        return userAgent;
    }

    @JsonProperty
    public void setUserAgent(Optional<String> userAgent) {
        this.userAgent = userAgent;
    }
}
