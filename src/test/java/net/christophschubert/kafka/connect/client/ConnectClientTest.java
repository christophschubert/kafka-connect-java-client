package net.christophschubert.kafka.connect.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpClassCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class ConnectClientTest {

    final String baseURL = "http://localhost:18080";

    static ClientAndServer mockServer;
    static Map<String, Map<String, String>> connectors = new HashMap<>();
    static ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setUpMockServer() throws JsonProcessingException {
        mockServer = ClientAndServer.startClientAndServer(18080);
        mockServer.when(
                request("/")
                        .withMethod("GET")
                        .withPath("/")
        ).respond(
                response().withStatusCode(200).withBody("{\n" +
                        "  \"version\":\"5.5.0\",\n" +
                        "  \"commit\":\"e5741b90cde98052\",\n" +
                        "  \"kafka_cluster_id\":\"I4ZmrWqfT2e-upky_4fdPA\"\n" +
                        "}")
        );


        mockServer.when(request("/connectors").withMethod("GET")).respond(HttpClassCallback.callback().withCallbackClass(GetConnectorsCallback.class));
        mockServer.when(request("/connectors").withMethod("POST")).respond(HttpClassCallback.callback().withCallbackClass(ConnectorsEndpointCallback.class));

    }

    public static class GetConnectorsCallback implements ExpectationResponseCallback {
        @Override
        public HttpResponse handle(HttpRequest httpRequest) throws Exception {
            return response(mapper.writeValueAsString(connectors.keySet()));
        }
    }

    public static class ConnectorsEndpointCallback implements ExpectationResponseCallback {
        @Override
        public HttpResponse handle(HttpRequest httpRequest) throws Exception {

            final var body = mapper.readValue(httpRequest.getBodyAsString(), new TypeReference<Map<String, Object>>() {
            });
            final var connectorName = body.get("name").toString();
            final Map<String, String> config = new HashMap<>();

            ((Map) body.get("config")).forEach((k, v) -> config.put(k.toString(), v.toString()));
            connectors.put(connectorName, config);


            final var responseData = Map.of("name", connectorName, "config", connectors.get(connectorName), "tasks", Collections.singletonList(Map.of("connector", connectorName, "task", 1)));

            return response(mapper.writeValueAsString(responseData));

        }
    }

    @Test
    public void getClusterInfoTest() throws IOException, InterruptedException {
        ConnectClient client = new ConnectClient("http://localhost:18080");
        final var clusterInfo = client.getClusterInfo();
        Assertions.assertEquals("5.5.0", clusterInfo.version);
        Assertions.assertEquals("e5741b90cde98052", clusterInfo.commit);
        Assertions.assertEquals("I4ZmrWqfT2e-upky_4fdPA", clusterInfo.kafkaClusterId);
    }

    @Test
    public void submitConnectorTest() throws IOException, InterruptedException {

        final ConnectorConfig config = ConnectorConfig.source("file4", "FileStreamSource")
                .with("topic", "file.contents")
                .with("file", "/tmp/test.sink.txt")
                .with("max.tasks", 3);

        ConnectClient client = new ConnectClient(baseURL);
        Assertions.assertTrue(client.getConnectors().isEmpty());
        final var connectorConfigAndTasks = client.startConnector(config);
        Assertions.assertEquals(1, connectorConfigAndTasks.tasks.size());
        Assertions.assertEquals(Set.of("file4"), client.getConnectors());
    }


}
