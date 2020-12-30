package net.christophschubert.kafka.connect.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

//TODO: should enum for task status be introduced?
public class TaskStatus {
    @JsonProperty("id")
    public final int id;

    @JsonProperty("state")
    public final String state;

    @JsonProperty("worker_id")
    public final String workerId;

    @JsonProperty("trace")
    public final String trace;

    @JsonCreator
    public TaskStatus(
            @JsonProperty("id") int id,
            @JsonProperty("state") String state,
            @JsonProperty("worker_id") String workerId,
            @JsonProperty("trace") String trace
    ) {
        this.id = id;
        this.state = state;
        this.workerId = workerId;
        this.trace = trace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskStatus)) return false;
        TaskStatus that = (TaskStatus) o;
        return id == that.id && Objects.equals(state, that.state) && Objects.equals(workerId, that.workerId) && Objects.equals(trace, that.trace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, workerId, trace);
    }

    @Override
    public String toString() {
        return "TaskStatus{" +
                "id=" + id +
                ", state='" + state + '\'' +
                ", workerId='" + workerId + '\'' +
                ", trace='" + trace + '\'' +
                '}';
    }
}
