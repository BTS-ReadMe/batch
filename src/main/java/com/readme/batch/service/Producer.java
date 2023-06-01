package com.readme.batch.service;

import com.readme.batch.requestObject.RequestPlusViewCount;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Producer {
    private final KafkaTemplate<String, RequestPlusViewCount> plusViewKafkaTemplate;

    public void sendPlusView(RequestPlusViewCount requestPlusViewCount) {
        System.out.println(String.format("Produce message(RequestKafkaMessage) : %s", requestPlusViewCount));
        this.plusViewKafkaTemplate.send("plusViewCount", requestPlusViewCount);
    }
}
