/*
package com.zbum.example.socket.server.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

*/
/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/5/22 15:03
 *//*

@Component
public class Consumer {

    @KafkaListener(topics = {"produce"})
    public void listen(ConsumerRecord<String, String> record){

        Optional<String> kafkaMessage = Optional.ofNullable(record.value());

        if (kafkaMessage.isPresent()) {

            Object message = kafkaMessage.get();
            //System.out.println("---->"+record);
            //System.out.println("---->"+message);

        }

    }
}
*/