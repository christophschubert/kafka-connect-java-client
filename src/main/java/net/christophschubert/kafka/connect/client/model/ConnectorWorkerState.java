package net.christophschubert.kafka.connect.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * models a subresource in the response of <code>GET /connector/{connectorName}/status</code>
 */
public class ConnectorWorkerState {
    @JsonProperty("state")
    public final String state;

    @JsonProperty("worker_id")
    public final String workerId;

    @JsonCreator
    public ConnectorWorkerState(
            @JsonProperty("state") String state,
            @JsonProperty("worker_id")String workerId
    ) {
        this.state = state;
        this.workerId = workerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectorWorkerState)) return false;
        ConnectorWorkerState that = (ConnectorWorkerState) o;
        return Objects.equals(state, that.state) && Objects.equals(workerId, that.workerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, workerId);
    }

    @Override
    public String toString() {
        return "ConnectorWorkerState{" +
                "state='" + state + '\'' +
                ", workerId='" + workerId + '\'' +
                '}';
    }
}
