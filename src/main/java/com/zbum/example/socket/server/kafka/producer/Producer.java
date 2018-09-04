package com.zbum.example.socket.server.kafka.producer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/5/22 15:07
 */
@Component
public class Producer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    private static Gson gson = new GsonBuilder().create();


    //发送消息方法
    public void send(String msg) {
       Message message = new Message();
        message.setId("sawsensor_"+System.currentTimeMillis());
        message.setMsg(msg);
        message.setSendTime(new Date());
        kafkaTemplate.send("p1", gson.toJson(message));
        //System.out.println("produceMsg"+msg);
       // kafkaTemplate.send("p1",msg);
    }

}
