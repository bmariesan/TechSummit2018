package ro.bmariesan.techsummit.publisher;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import java.util.UUID;

@SpringBootApplication
@IntegrationComponentScan
@Component
public class MqttPublisher {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(MqttPublisher.class)
                .web(WebApplicationType.NONE)
                .run(args);
        OutboundChannel gateway = context.getBean(OutboundChannel.class);
        MqttPublisher publisher = new MqttPublisher();
        publisher.sendMessages(gateway);
    }

    private void sendMessages(OutboundChannel gateway) {
        for (int i = 1; i <= 100; i++) {
            gateway.sendToMqtt("Hello TechSummit!");
        }
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        factory.setPersistence(mqttClientDataStore());
        return factory;
    }

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setServerURIs(new String[]{"tcp://localhost:1883"});
        mqttConnectOptions.setWill("/techSummitTopic", "I'll be back...".getBytes(), 0, true);
        mqttConnectOptions.setKeepAliveInterval(5000);
        mqttConnectOptions.setUserName("tech");
        mqttConnectOptions.setPassword("summit".toCharArray());
        return mqttConnectOptions;
    }

    @Bean
    public MemoryPersistence mqttClientDataStore() {
        return new MemoryPersistence();
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    private interface OutboundChannel {
        void sendToMqtt(String data);

    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        String clientId = UUID.randomUUID().toString();
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId, mqttClientFactory());
        messageHandler.setDefaultTopic("/techSummitTopic");
        messageHandler.setDefaultQos(0);
        messageHandler.setCompletionTimeout(50000);
        messageHandler.start();
        return messageHandler;
    }
}