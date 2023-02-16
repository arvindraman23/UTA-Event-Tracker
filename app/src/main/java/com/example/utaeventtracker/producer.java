package com.example.utaeventtracker;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class producer {
    public void sendMessageToBroker(String userId, String venue, String title,
                                           String description, String data, String... routingKey) throws IOException, TimeoutException {
        String user_id = "1";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("18.219.9.194");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        Connection connection = factory.newConnection();


        if (routingKey != null) {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare("public", BuiltinExchangeType.FANOUT);
            channel.basicPublish("public","",null, data.getBytes(StandardCharsets.UTF_8));
        } else {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare("user",BuiltinExchangeType.DIRECT);
            channel.basicPublish("user",userId,null,data.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void main(){
//        sendMessageToBroker("1","","mav alert");
//        sendMessageToBroker("1","1","user specification notification");

    }
}
