package net.christophschubert.kafka.connect.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ConnectorPluginInfo {
    @JsonProperty("class")
    public final String className;

    @JsonProperty("type")
    public final String type;

    @JsonProperty("version")
    public final String version;

    @JsonCreator
    public ConnectorPluginInfo(
            @JsonProperty("class") String className,
            @JsonProperty("type") String type,
            @JsonProperty("version") String version
    ) {
        this.className = className;
        this.type = type;
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectorPluginInfo)) return false;
        ConnectorPluginInfo that = (ConnectorPluginInfo) o;
        return Objects.equals(className, that.className) && Objects.equals(type, that.type) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, type, version);
    }

    @Override
    public String toString() {
        return "ConnectorPluginInfo{" +
                "className='" + className + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
