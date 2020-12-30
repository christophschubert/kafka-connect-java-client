package net.christophschubert.kafka.connect.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Objects;

/**
 * Models the response object of <code>GET /connectors/{connectorName}/tasks</code>
 */
public class TaskConfig {
    @JsonProperty("id")
    public final ConnectorTask id;

    @JsonProperty("config")
    public final Map<String, String> config;

    @JsonCreator
    public TaskConfig(
            @JsonProperty("id") ConnectorTask id,
            @JsonProperty("config") Map<String, String> config
    ) {
        this.id = id;
        this.config = config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskConfig)) return false;
        TaskConfig that = (TaskConfig) o;
        return Objects.equals(id, that.id) && Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, config);
    }

    @Override
    public String toString() {
        return "TaskConfig{" +
                "id=" + id +
                ", config=" + config +
                '}';
    }
}
