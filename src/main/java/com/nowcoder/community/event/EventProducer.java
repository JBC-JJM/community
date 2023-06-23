package com.nowcoder.community.event;

import com.alibaba.fastjson.JSON;
import com.nowcoder.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 将事件发布到指定的主题
    public void fireEvent(Event event) {
        kafkaTemplate.send(event.getTopic(), JSON.toJSONString(event));
    }
}
