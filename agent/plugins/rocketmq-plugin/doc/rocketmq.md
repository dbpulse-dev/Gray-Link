## 重复消费问题

    1.ConsumeFromWhere 如果配置为CONSUME_FROM_LAST_OFFSET(默认), 如果新境灰度组因为是新组offfset为0,
     会重头拉取(没有灰度标签，最终会丢掉)
    2.假如某个服务没在灰度组，如果采用正常组处理灰度消息，灰度组消息的offset还是没有偏移，当下次有灰度组是，
    灰度组拉取消息将是重将有部分是重复消费；反过也一样
    3.为了解决上面的问题,服务如果只有一种实例状态时:要么只灰正常实例，如果只有灰度实例时，
    变成同时启动两和消费组，各正只消费相应的消息
    4.域者 ConsumeFromWhere CONSUME_FROM_TIMESTAMP ,但是这种方式可能会存在少量重复或漏掉消息(实例的时间与服务端时间差) 
    5.通过灰度tag识别:默认会订阅*，即所有;只有打tag，才仅订阅tag相应的

## 双订阅实现

    1.拦截DefaultMQPushConsumer 创建
    2.拦载DefaultMQPushConsumer start方法，据状态创建正常consumer或是灰度 consumer,或同时创建
    3.给MessageListener 绑定组信息，用于识别listener所有那订阅组的，以便后续消息处理
      (灰度组只处理灰度消息，正常组处理正常消息

## 通过属性过滤

    1.不能同时使用tag和SQL92过滤
    2.SQL92 测试不支持中画线属性
    consumer.subscribe("YourTopic",
    MessageSelector.bySql("region = 'east' AND price > 100"));
    MessageSelector.bySql("e-env IS NOT NULL")) 

