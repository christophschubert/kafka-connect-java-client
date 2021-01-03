package net.christophschubert.kafka.connect.client;

import net.christophschubert.cp.testcontainers.CPTestContainerFactory;
import net.christophschubert.kafka.connect.client.configs.DataGenConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;


public class ConnectClientIT {
    @Test
    public void startConnector() throws IOException, InterruptedException {
        final CPTestContainerFactory factory = new CPTestContainerFactory();

        final var kafka = factory.createKafka();
        kafka.start();

        final var connect = factory.createCustomConnector("confluentinc/kafka-connect-datagen:0.4.0", kafka);
        connect.start();

        final ConnectClient client = new ConnectClient(connect.getBaseUrl());
        final var connectorName = "datagen";
        final var dataGenConfig = new DataGenConfig(connectorName)
                .withKafkaTopic("users")
                .withQuickstart("users")
                .withIterations(1000000)
                .with("value.converter.schemas.enable", "false");
        client.startConnector(dataGenConfig);

        Assertions.assertEquals(Collections.singleton(connectorName), client.getConnectors());
        Assertions.assertEquals("RUNNING", client.getConnectorStatus(connectorName).connector.state);
        Thread.sleep(3000); // wait for task to be started properly
        Assertions.assertEquals("RUNNING", client.getConnectorStatus(connectorName).tasks.get(0).state);

    }
}
