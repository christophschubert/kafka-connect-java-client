package net.christophschubert.kafka.connect.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.christophschubert.kafka.connect.client.ConnectClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConnectorConfigAndTasks {
    @JsonProperty("name")
    public final String name;

    @JsonProperty("config")
    public final Map<String, Object> config;

    @JsonProperty("tasks")
    public final List<ConnectorTask> tasks;

    @JsonProperty("type")
    public final String type;

    @JsonCreator
    public ConnectorConfigAndTasks(
            @JsonProperty("name") String name,
            @JsonProperty("config") Map<String, Object> config,
            @JsonProperty("tasks") List<ConnectorTask> tasks,
            @JsonProperty("type") String type
    ) {
        this.name = name;
        this.config = config;
        this.tasks = tasks;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectorConfigAndTasks)) return false;
        ConnectorConfigAndTasks that = (ConnectorConfigAndTasks) o;
        return Objects.equals(name, that.name) && Objects.equals(config, that.config) && Objects.equals(tasks, that.tasks) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, config, tasks, type);
    }

    @Override
    public String toString() {
        return "ConnectorConfigAndTasks{" +
                "name='" + name + '\'' +
                ", config=" + config +
                ", tasks=" + tasks +
                ", type='" + type + '\'' +
                '}';
    }
}
