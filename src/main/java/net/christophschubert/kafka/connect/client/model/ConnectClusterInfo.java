package net.christophschubert.kafka.connect.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ConnectClusterInfo {
    @JsonProperty("version")
    public final String version;

    @JsonProperty("commit")
    public final String commit;

    @JsonProperty("kafka_cluster_id")
    public final String kafkaClusterId;

    @JsonCreator
    public ConnectClusterInfo(
            @JsonProperty("version") String version,
            @JsonProperty("commit") String commit,
            @JsonProperty("kafka_cluster_id") String kafkaClusterId
    ) {
        this.version = version;
        this.commit = commit;
        this.kafkaClusterId = kafkaClusterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectClusterInfo)) return false;
        ConnectClusterInfo that = (ConnectClusterInfo) o;
        return Objects.equals(version, that.version) && Objects.equals(commit, that.commit) && Objects.equals(kafkaClusterId, that.kafkaClusterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, commit, kafkaClusterId);
    }

    @Override
    public String toString() {
        return "ConnectClusterInfo{" +
                "version='" + version + '\'' +
                ", commit='" + commit + '\'' +
                ", kafkaClusterId='" + kafkaClusterId + '\'' +
                '}';
    }
}
