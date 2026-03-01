## rabbitmq 只有一种状态时

###1.只有正常状态，但要接口灰度消息
1.当前实例主动探测，如果当前所有实例都是正常状态，
那么当前实例应开启一个灰度vitualhost去订阅
2.如果当前实例已经同时订阅了(正常和灰度消息)，当探测到已经有了灰度服务，自动取消灰度消息的订阅
###2.只有灰度状态，但要接收正常消息
同上，反着操作
###3. 订阅控制
1.应用启动时，启动双订阅连接，但默认不拉取消息
2.当前状态开启的连接开启订阅
3.可控制FrameHandler#readFrame是否真正读取数据，
但需要识是什么状态的FrameHandler
ConnectionFactory:newConnection创建连接通道会设置virtualhost
ConnectionFactory#createConnection

## 订阅原理

    1.创建BlockingQueueConsumer会配置拉取一个或多个queue的信息
    2.同时BlockingQueueConsumer会维护ConnectionFactory实例，
    当ConnectionFactory连接的是灰度的virtualhost,则拉取灰度队列表的消息 
    3.BlockingQueueConsumer 会维护LinkedBlockingQueue实例，循环从服务端读取到队列消息临时存放到此，
    待另一个线程从该队列读取，并转发到业务代码 
    4.BlockingQueueConsumer start 通过 connectionfactory获取连接channel, 
    如果能控制factory连接的virtualhost,则可以实现双订阅

## 双订阅实现

    1.每个队列至少建立2个以上BlockingQueueConsumer
    2.分配机制，平均、正常的多，灰度的少,当前状态n+1
    3.拦截BlockingQueueConsumer 构造，获得该实例
    4.connectionfactory 代理,内部实现两个connectionfactory 
    5.com.rabbitmq.client.ConnectionFactory#newConnection,可在此修改virtaulhost,
    但是订阅如果使用通道模式,下次获取channle需要创建一个新的connection，才能实现双订阅;
    6.涉级到connect,channel缓存已绑定factory等问题，在创建BlockingQueueConsumer就分配置不同的factor比较合适
    7.存在一个应用在多集群，发送或订阅:
        7.1 集群识别,通过地址？
        7.2 需要通过 address+virtualhost识别为同一个集群
    