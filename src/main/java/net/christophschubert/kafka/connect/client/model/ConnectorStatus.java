package net.christophschubert.kafka.connect.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * models the response from <code>GET /connector/{connectorName}/status</code>
 */
public class ConnectorStatus {
    @JsonProperty("name")
    public final String name;

    @JsonProperty("connector")
    public final ConnectorWorkerState connector;

    @JsonProperty("tasks")
    public final List<TaskStatus> tasks;

    @JsonProperty("type")
    public final String type;

    @JsonCreator
    public ConnectorStatus(
            @JsonProperty("name") String name,
            @JsonProperty("connector") ConnectorWorkerState connector,
            @JsonProperty("tasks") List<TaskStatus> tasks,
            @JsonProperty("type")String type
    ) {
        this.name = name;
        this.connector = connector;
        this.tasks = tasks;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectorStatus)) return false;
        ConnectorStatus that = (ConnectorStatus) o;
        return Objects.equals(name, that.name) && Objects.equals(connector, that.connector) && Objects.equals(tasks, that.tasks) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, connector, tasks, type);
    }

    @Override
    public String toString() {
        return "ConnectorStatus{" +
                "name='" + name + '\'' +
                ", connector=" + connector +
                ", tasks=" + tasks +
                ", type='" + type + '\'' +
                '}';
    }
}



