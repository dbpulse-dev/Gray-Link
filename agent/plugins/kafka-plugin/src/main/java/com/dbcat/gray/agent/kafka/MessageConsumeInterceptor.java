package com.dbcat.gray.agent.kafka;


import com.dbcat.gray.agent.core.context.Context;
import com.dbcat.gray.agent.core.context.InContextInterceptor;
import com.dbcat.gray.agent.core.dto.ComponentType;
import com.dbcat.gray.agent.core.dto.ConsumerMessage;
import com.dbcat.gray.agent.core.mq.MQListenerManager;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.lang.reflect.Method;


/**
 * 标记生产消息灰度变量
 *
 * @author Blackfost
 */
public class MessageConsumeInterceptor implements InContextInterceptor, InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        ConsumerRecord m = (ConsumerRecord) allArguments[0];
        Context context = buildContext(m);
        setInContext(context);
        MQListenerManager.onConsume(ConsumerMessage.build(ComponentType.KAFKA, m, context, allArguments));
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        this.clear();
        return ret;
    }

    private Context buildContext(ConsumerRecord record) {
        Headers headers = record.headers();
        return new Context() {
            @Override
            public void put(String key, Object value) {

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
