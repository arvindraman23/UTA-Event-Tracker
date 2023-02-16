package com.example.utaeventtracker;
import android.annotation.SuppressLint;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;


public class notificationListenerActivity {

//    public static void main(String[] args) throws IOException, TimeoutException {
//        String user_id= "1";
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("18.219.9.194");
//        factory.setPort(5672);
//        factory.setUsername("guest");
//        factory.setPassword("guest");
//        factory.setVirtualHost("/");
//        Connection connection = factory.newConnection();
//        Channel channel = connection.createChannel();
//        String consumerTag = "SimpleConsumer";
//        channel.exchangeDeclare("public", BuiltinExchangeType.FANOUT);
//        channel.exchangeDeclare("user", BuiltinExchangeType.DIRECT);
//
//        AMQP.Queue.DeclareOk queueResult = channel.queueDeclare("", false, true, true, null);
//        channel.queueBind(queueResult.getQueue(), "public", "");
//        channel.queueDeclare(user_id,false,true,true,null);
//        channel.queueBind(user_id,"user",user_id);
//
//        System.out.println("[$consumerTag] Waiting for messages...");
//
//        DeliverCallback deliverCallback = (x, delivery) -> {
//            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
//            System.out.println("[$x] Received message: '$message' "+message);
//        };
//
//        CancelCallback cancelCallback = (x) -> {
//            System.out.println("[$consumerTag] was canceled");
//        };
//
//        channel.basicConsume(queueResult.getQueue(), false, consumerTag, deliverCallback, cancelCallback);
//        channel.basicConsume(user_id, false, "userTag", deliverCallback, cancelCallback);
//    }

}