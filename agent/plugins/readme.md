## 消息统计

## 关系收集

    发布:
    rabbit: (exchange+routingKey)+vitualhost+address
    rocket: topic+group+address
    kafka: topic+group+address
    订阅:
    rabbit: queues
    rocket: topic
    kafka : topic 

## rabbit

    1. 分virtualhost订阅：正常virtualhost和灰度virtualhost
    生产和订阅都使用到同一个 ConnectionFactory
    启动时，如要使用生产端能同时发正常消息和灰度消息
    生产端则需要同时创建 正常virtualt连接或灰度的连接
    AbstractMessageListenerContainer# executeListener(Channel channel, Message messageIn) 
    在切换灰度状态时:
        生产端连接不发生变化
        订阅端需要切换
    2.关系收集
   
    
    MessageListener

## rocket mq

    分组订阅：正常组和灰度组
    监听 MessageListener,可能会批
    ConsumeFromWhere

## kafka

    分组订阅:正常组和灰度组,双订阅
    
