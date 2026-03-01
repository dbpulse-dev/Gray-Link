##   

    KafkaConsumer
    ConcurrentMessageListenerContainer(多线程,包含多个KafkaMessageListenerContainer(单线程
    MessageListenerContainer
    KafkaMessageListenerContainer 有几个阅并发就创建几,可拦截它修改订阅组
    DefaultKafkaConsumerFactory 或ContainerProperties 修改订阅组
    AbstractCoordinator 订阅定位关键类，group_id 实现 KafkaConsumer->ConsumerCoordinator

## 双订阅实现

    拦载KafkaConsumer 保存获取订阅相关信息,内存再创建两个consumer,一个正常，一个灰度的
    ConcurrentMessageListenerContainer 循环调用 pollAndInvoke KafkaConsumer
    消息监听器代理实现类
    		if (listener instanceof AcknowledgingConsumerAwareMessageListener
				|| listener instanceof BatchAcknowledgingConsumerAwareMessageListener) {
			listenerType = ListenerType.ACKNOWLEDGING_CONSUMER_AWARE;
		}
		else if (listener instanceof ConsumerAwareMessageListener
				|| listener instanceof BatchConsumerAwareMessageListener) {
			listenerType = ListenerType.CONSUMER_AWARE;
		}
		else if (listener instanceof AcknowledgingMessageListener
				|| listener instanceof BatchAcknowledgingMessageListener) {
			listenerType = ListenerType.ACKNOWLEDGING;
		}
		else if (listener instanceof GenericMessageListener) {
			listenerType = ListenerType.SIMPLE;
		}

