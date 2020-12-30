package net.christophschubert.kafka.connect.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.christophschubert.kafka.connect.client.ConnectClient;

import java.util.Objects;

public class ConnectorTask {
    @JsonProperty("connector")
    public final String connector;

    @JsonProperty("task")
    public final int task;

    @JsonCreator
    public ConnectorTask(
            @JsonProperty("connector") String connector,
            @JsonProperty("task") int task
    ) {
        this.connector = connector;
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectorTask)) return false;
        ConnectorTask that = (ConnectorTask) o;
        return task == that.task && Objects.equals(connector, that.connector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connector, task);
    }

    @Override
    public String toString() {
        return "ConnectorTask{" +
                "connector='" + connector + '\'' +
                ", task=" + task +
                '}';
    }
}
