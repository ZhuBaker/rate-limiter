**什么是限流？**

限流是对系统的**出入流量**进行**控制**，防止大流量出入，导致**资源**不足，系统不稳定。

限流系统是对资源访问的控制组件，控制主要的两个功能：**限流策略**和**熔断策略**，对于熔断策略，
不同的系统有不同的熔断策略诉求，有的系统希望直接拒绝、
有的系统希望排队等待、有的系统希望服务降级、有的系统会定制自己的熔断策略，这里只针对**限流策略**这个功能做详细的设计。

**限流算法**

**1、限制瞬时并发数**

Guava RateLimiter 提供了令牌桶算法实现：平滑突发限流(SmoothBursty)和平滑预热限流(SmoothWarmingUp)实现。

**2、限制某个接口的时间窗最大请求数**

即一个时间窗口内的请求数，如想限制某个接口/服务每秒/每分钟/每天的请求数/调用量。如一些基础服务会被很多其他系统调用，
比如商品详情页服务会调用基础商品服务调用，但是怕因为更新量比较大将基础服务打挂，这时我们要对每秒/每分钟的调用量进行限速；
一种实现方式如下所示：
`LoadingCache<Long, AtomicLong> counter =
        CacheBuilder.newBuilder()
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .build(new CacheLoader<Long, AtomicLong>() {
                    @Override
                    public AtomicLong load(Long seconds) throws Exception {
                        return new AtomicLong(0);
                    }
                });
long limit = 1000;
while(true) {
    //得到当前秒
    long currentSeconds = System.currentTimeMillis() / 1000;
    if(counter.get(currentSeconds).incrementAndGet() > limit) {
        System.out.println("限流了:" + currentSeconds);
        continue;
    }
    //业务处理
}`

使用Guava的Cache来存储计数器，过期时间设置为2秒（保证1秒内的计数器是有的），
然后我们获取当前时间戳然后取秒数来作为KEY进行计数统计和限流，这种方式也是简单粗暴，刚才说的场景够用了。

**3、令牌桶**
https://pic2.zhimg.com/80/v2-bdd2dba71cb1f9145d03c75bcbee43db_hd.jpg

算法描述：
1.假如用户配置的平均发送速率为r，则每隔1/r秒一个令牌被加入到桶中
2.假设桶中最多可以存放b个令牌。如果令牌到达时令牌桶已经满了，那么这个令牌会被丢弃
3.当流量以速率v进入，从桶中以速率v取令牌，拿到令牌的流量通过，拿不到令牌流量不通过，执行熔断逻辑

**属性**
长期来看，符合流量的速率是受到令牌添加速率的影响，被稳定为：r
因为令牌桶有一定的存储量，可以抵挡一定的流量突发情况

M是以字节/秒为单位的最大可能传输速率。 M>r

T max = b/(M-r) 承受最大传输速率的时间

B max = T max * M 承受最大传输速率的时间内传输的流量

优点：流量比较平滑，并且可以抵挡一定的流量突发情况




