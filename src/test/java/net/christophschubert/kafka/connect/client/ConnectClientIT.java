package net.christophschubert.kafka.connect.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

class ConnectClientIT {

    final String baseURL = "http://localhost:8083";

    @Test
    public void localTry() throws IOException, InterruptedException {


        final ConnectorConfig config = ConnectorConfig.source("file4", "FileStreamSource")
                .with("topic", "file.contents")
                .with("file", "/tmp/test.sink.txt")
                .with("max.tasks", 3);


        ConnectClient client = new ConnectClient(baseURL);
        System.out.println(client.getConnectors());
        System.out.println(client.startConnector(config));
        System.out.println(client.getConnectors());
    }


    @Test
    public void getFileStatus() throws IOException, InterruptedException {
        ConnectClient client = new ConnectClient(baseURL);
        System.out.println(client.getConnectorStatus("file3"));
        System.out.println(client.getConnectorInfo("file3"));
        System.out.println(client.getConnectorTopics("file2"));
        System.out.println(client.getConnectorConfig("file4"));

        final ConnectorConfig config = ConnectorConfig.source("file4", "FileStreamSource")
                .with("topic", "file.contents")
                .with("file", "/tmp/test.sink.txt")
                .with("max.tasks", 1);
        System.out.println(client.createOrUpdateConnector(config));
    }

    @Test
    public void getClusterInfoTest() throws IOException, InterruptedException {
        final ConnectClient client = new ConnectClient(baseURL);
        System.out.println(client.getClusterInfo());
        System.out.println(client.getConnectorTasks("file4"));
        System.out.println(client.getTaskStatus("file4", 0));
    }

    @Test
    public void getPluginsTest() throws IOException, InterruptedException {
        final ConnectClient client = new ConnectClient(baseURL);
        System.out.println(client.getConnectorPlugins());

        final ConnectorConfig config = ConnectorConfig.source("file4", "FileStreamSource")
                .with("topic", "file.contents")
                .with("file", "/tmp/test.sink.txt")
                .with("max.tasks", 1);

        //TODO: look into this: we need to provide a name on the same level as the other config values
        System.out.println(client.validateConfig("FileStreamSinkConnector", Map.of("topics", "file.contents"
                , "file", "/tmp/test.sink.txt", "name", "file5", "connector.class", "org.apache.kafka.connect.file.FileStreamSinkConnector")));

        System.out.println(client.getLogLevels());
        System.out.println(client.getLogLevel("root"));
    }

    @Test
    public void connectorStatusTest() throws IOException, InterruptedException {
        ConnectClient client = new ConnectClient(baseURL);
        System.out.println(client.getConnectorStatus("file3"));
    }
}
