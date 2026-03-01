package com.gray.web.consumer;

import com.gray.web.util.EnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Profile({"consumer"})
@Component
public class KafkaConsumer {
    protected static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    /**
     * 监听test主题,有消息就读取
     *
     * @param message
     */
    @KafkaListener(topics = {"kafka-gray-test"})
    public void consumer(String message) {
        logger.info("收到kafka 消息,x_env:{},内容{}", EnvUtils.getXEnv(), message);
    }


}