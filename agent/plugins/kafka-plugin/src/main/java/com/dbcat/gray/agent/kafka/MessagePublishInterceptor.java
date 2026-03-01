package com.dbcat.gray.agent.kafka;


import com.dbcat.gray.agent.core.context.Context;
import com.dbcat.gray.agent.core.context.OutContextInterceptor;
import com.dbcat.gray.agent.core.dto.ComponentType;
import com.dbcat.gray.agent.core.dto.PublishMessage;
import com.dbcat.gray.agent.core.mq.MQListenerManager;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.lang.reflect.Method;


/**
 * 标记生产消息灰度变量
 *
 * @author Blackfost
 */
public class MessagePublishInterceptor implements OutContextInterceptor, InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        ProducerRecord record = (ProducerRecord) allArguments[0];
        Context context = buildContext(record);
        this.setOutContext(context);
        MQListenerManager.onPublish(PublishMessage.build(ComponentType.KAFKA, record, context, allArguments));
    }


    private Context buildContext(ProducerRecord record) {
        Headers headers = record.headers();
        return new Context() {
            @Override
            public void put(String key, Object value) {
                headers.add(key, value.toString().getBytes());
            }

            @Override
            public Object get(String key) {
                Header header = headers.lastHeader(key);
                if (header == null) {
                    return null;
                }
                return new String(header.value());
            }
        };
    }

}
