package net.christophschubert.kafka.connect.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.christophschubert.kafka.connect.client.exceptions.ConnectRestException;
import net.christophschubert.kafka.connect.client.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectClient {

    final static String CONNECTORS_EP = "/connectors";
    final static Logger logger = LoggerFactory.getLogger(ConnectClient.class);

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public ConnectClient(String baseURL) {
        this.baseUrl = baseURL;
        this.httpClient = HttpClient.newBuilder().build();
    }

    HttpResponse<String> makeGetRequest(String endpoint) throws IOException, InterruptedException {
        return httpClient.send(prepare(endpoint), HttpResponse.BodyHandlers.ofString());
    }

    HttpRequest prepare(String endpoint) {
        return HttpRequest.newBuilder(URI.create(baseUrl + endpoint))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
    }

    HttpRequest prepare(String endpoint, String method) {
        return HttpRequest.newBuilder(URI.create(baseUrl + endpoint))
                .method(method, HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
    }

    HttpRequest prepare(String endpoint, String method, Object payload) throws JsonProcessingException {
        return HttpRequest.newBuilder(URI.create(baseUrl + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
    }



    String connectorsEp(String... components) {
        return CONNECTORS_EP + '/' + String.join("/", components);
    }


    /**
     *
     * Wraps <code>GET /</code>
     *
     * @return basic information on the connect cluster
     * @throws IOException
     * @throws InterruptedException
     */
    public ConnectClusterInfo getClusterInfo() throws IOException, InterruptedException {
        return mapper.readValue(makeGetRequest("").body(), ConnectClusterInfo.class);
    }


    /**
     *  Returns a list of active connectors.
     *
     *  Wraps <code>GET /connectors</code>.
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public Set<String> getConnectors() throws IOException, InterruptedException {
        final var response = httpClient.send(prepare(CONNECTORS_EP), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<Set<String>>() {});
    }

    /**
     * Wraps <code>POST /connectors</code>
     *
     * @param config
     * @throws IOException
     * @throws InterruptedException
     */
    // TODO: the API docs at
    // https://docs.confluent.io/platform/current/connect/references/restapi.html
    // don't mention the 'type' field. Fill a FF ticket for that!
    public ConnectorConfigAndTasks startConnector(ConnectorConfig config) throws IOException, InterruptedException {
        final var request = prepare(CONNECTORS_EP,"POST", config);
        logger.info("submitting config: " + mapper.writeValueAsString(config));
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        // it seems that the response only contains an error code and a text message.
        // a detailed status code would be great!
        if (response.statusCode() == 409)
            throw new ConnectRestException(response.body());
        return mapper.readValue(response.body(), ConnectorConfigAndTasks.class);
    }

    /**
     * Wraps <code>GET /connectors/{connectorName}</code>.
     *
     * @param connectorName name of the connector
     * @return
     */
    public ConnectorConfigAndTasks getConnectorInfo(String connectorName) throws IOException, InterruptedException {
        final var httpResponse = makeGetRequest(connectorsEp(connectorName));
        return mapper.readValue(httpResponse.body(), ConnectorConfigAndTasks.class);
    }

    /**
     *
     * Wraps <code>GET /connectors/{connectorName}/config</code>
     *
     * All values in the config map are strings (an equality comparison to the config map of a ConnectorConfig object
     * will most likely fail).
     *
     * @param connectorName the name of the connector
     * @return a map describing the config.
     * @throws IOException
     * @throws InterruptedException
     */
    public Map<String, String> getConnectorConfig(String connectorName) throws IOException, InterruptedException {
        final var httpResponse = makeGetRequest(connectorsEp(connectorName, "config"));
        return mapper.readValue(httpResponse.body(), new TypeReference<Map<String, String>>() {});
    }

    /**
     *
     * wraps <code>PUT /connectors/{connectorName}/config</code>
     *
     * @param connectorName the name of the connector
     * @param configMap configuration properties of the connector
     * @return
     */
    public ConnectorConfigAndTasks createOrUpdateConnector(String connectorName, Map<String, ?> configMap) throws IOException, InterruptedException {
        final var request = prepare(connectorsEp(connectorName, "config"), "PUT", configMap);
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 409)
            throw new ConnectRestException(response.body());
        return mapper.readValue(response.body(), ConnectorConfigAndTasks.class);
    }

    /**
     *
     * wraps <code>PUT /connectors/{config.name}/config</code>
     *
     * @param config the configuration
     * @return
     */
    public ConnectorConfigAndTasks createOrUpdateConnector(ConnectorConfig config) throws IOException, InterruptedException {
        return createOrUpdateConnector(config.name, config.config);
    }


    /**
     * Returns detailed status of a connector.
     *
     * Wraps <code>GET /connectors/{connector}/status</code>.
     * @param connectorName name of the connector
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public Map<String, Object> getConnectorStatus(String connectorName) throws IOException, InterruptedException {
        final var httpResponse = makeGetRequest(connectorsEp(connectorName, "status"));
        //TODO: return properly structured object
        return mapper.readValue(httpResponse.body(), Map.class);
    }

    /**
     * Wraps <code>POST /connectors/{connectorName}/restart</code>
     *
     * @param connectorName name of the connector
     * @throws IOException
     * @throws InterruptedException
     */
    public void restartConnector(String connectorName) throws IOException, InterruptedException {
        final var request = prepare(connectorsEp(connectorName, "restart"), "POST");
        httpClient.send(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Wraps <code>PUT /connectors/{connectorName}/pause</code>
     *
     * @param connectorName name of the connector
     * @throws IOException
     * @throws InterruptedException
     */
    public void pauseConnector(String connectorName) throws IOException, InterruptedException {
        final var request = prepare(connectorsEp(connectorName, "pause"), "PUT");
        httpClient.send(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Wraps <code>PUT /connectors/{connectorName}/resume</code>
     *
     * @param connectorName name of the connector
     * @throws IOException
     * @throws InterruptedException
     */
    public void resumeConnector(String connectorName) throws IOException, InterruptedException {
        final var request = prepare(connectorsEp(connectorName, "resume"), "PUT");
        httpClient.send(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Deletes a connector.
     *
     * Wraps <code>DELETE /connectors/{connectorName}</code>
     * @param connectorName the name of the connector
     * @throws IOException
     * @throws InterruptedException
     */
    public void deleteConnector(String connectorName) throws IOException, InterruptedException {
        final var request = prepare(connectorsEp(connectorName), "DELETE");
        httpClient.send(request, HttpResponse.BodyHandlers.discarding());
    }

    // methods to deal with tasks

    /**
     * Wraps <code>GET /connectors/{connectorName}/tasks</code>
     * @param connectorName the name of the task
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    //TODO: the corresponding response description on https://docs.confluent.io/platform/current/connect/references/restapi.html#tasks
    // contains an error: the field-name of config is missing
    public List<TaskConfig> getConnectorTasks(String connectorName) throws IOException, InterruptedException {
        final var response = makeGetRequest(connectorsEp(connectorName, "tasks"));
        return mapper.readValue(response.body(), new TypeReference<List<TaskConfig>>() {});
    }


    /**
     * Wraps <code>GET /connectors/{connectorName}/tasks/{taskId}/status</code>
     *
     * @param connectorName
     * @param taskId
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public TaskStatus getTaskStatus(String connectorName, int taskId) throws IOException, InterruptedException {
        final var response = makeGetRequest(connectorsEp(connectorName, "tasks", "" + taskId, "status"));
        return mapper.readValue(response.body(), TaskStatus.class);
    }

    public boolean restartTask(String connectorName, int taskId) throws IOException, InterruptedException {
        final var request = prepare(connectorsEp(connectorName, "tasks", ""+taskId, "restart"), "POST");
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode() == 200;
    }



    //topics
    public Set<String> getConnectorTopics(String connectorName) throws IOException, InterruptedException {
        final var endpoint = String.format("%s/%s/topics", CONNECTORS_EP, connectorName);
        var response = makeGetRequest(endpoint);
        final Map<String, Map<String, Set<String>>> r = mapper.readValue(response.body(), new TypeReference<Map<String, Map<String, Set<String>>>>() {});
        return r.get(connectorName).get("topics");
    }

    public void resetConnectorTopics(String connectorName) throws IOException, InterruptedException {
        final var endpoint = String.format("%s/%s/topics/reset", CONNECTORS_EP, connectorName);
        httpClient.send(prepare(endpoint, "PUT"), HttpResponse.BodyHandlers.discarding());
    }

    //methods to deal with connector plugins

    /**
     * Wraps <code>GET /connector-plugins</code>
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    //TODO: response in https://docs.confluent.io/platform/current/connect/references/restapi.html#get--connector-plugins-
    // incomplete, does not show type and version
    public List<ConnectorPluginInfo> getConnectorPlugins() throws IOException, InterruptedException {
        final var response = makeGetRequest("/connector-plugins");
        return mapper.readValue(response.body(), new TypeReference<List<ConnectorPluginInfo>>() {});
    }


    //TODO: introduce proper class for this
    public Map<String, Object> validateConfig(String connectorClassName, Map<String, String> config) throws IOException, InterruptedException {
        final String endpoint = String.format("/connector-plugins/%s/config/validate", connectorClassName);
        final var request = prepare(endpoint, "PUT", config);
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
    }
    //methods to deal with the admin and logging API

}
