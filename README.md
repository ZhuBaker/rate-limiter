# RateLimiter

## 限流普及
限流是对**出入流量**进行控制 ， 防止大量流入，导致资源不足，系统不稳定。
限流系统是对资源访问的控制组件 ， 控制主要有两个功能 ， 限流策略和熔断策略，对不同的系统有不同的熔断策略诉求，
有的系统希望直接拒绝、有的系统希望排队等待、有的系统希望服务降级、有的系统会定制自己的熔断策略，这里只对**限流策略** 这个功能做详细的设计。

## 限流算法
### 1.限制瞬时发并发数
Guava RateLimiter提供了令牌桶算法实现：平滑突发限流(SmoothBursty) 和 平滑预热限流(SmoothWarmingUp)实现
### 2.限制某个接口的时间窗口最大请求数
即一个时间窗口内的请求数，如想限制某个接口/服务 每秒/每分钟/每天的 请求数/调用量。如一些基础服务会被很多其他系统调用，
比如商品详情页服务会调用基础商品服务调用，但是怕因为更新量比较大，将基础服务打挂，这时我们需要对每秒/每分钟的调用量进行限速；


## 引用技术
* Redis
* Lua
* Guava (RateLimiter)

## 亮点
* 支持配置文件
* 支持硬编码
* 粒度小控制简单

## 核心类

### 锁控制： 单位时间窗口最大请求数
* 单机锁控制
    * com.github.bakerzhu.common.ratelimiter.lock.SingleLock
* 分布式锁控制
    * com.github.bakerzhu.common.ratelimiter.lock.DistributedLock

### limiter控制： 限制瞬时并发
* 单机控制
    * com.github.bakerzhu.common.ratelimiter.limiter.SingleLimiter
* 并发控制
    * com.github.bakerzhu.common.ratelimiter.limiter.DistributedLimiter



## 具体参考
### 依赖项目：
* [redis-client-common](https://github.com/ZhuBaker/redis-client-common)

### 核心代码：
* [SingleLockTest](https://github.com/ZhuBaker/rate-limiter/blob/master/src/test/java/com/github/bakerzhu/common/lock/SingleLockTest.java)
* [DistributedLockTest2](https://github.com/ZhuBaker/rate-limiter/blob/master/src/test/java/com/github/bakerzhu/common/lock/DistributedLockTest2.java)
* [LimiterTest](https://github.com/ZhuBaker/rate-limiter/blob/master/src/test/java/com/github/bakerzhu/common/limiter/LimiterTest.java)

# 缺陷

此次控制分布式瞬发限流的设计类似于 窗口最大请求数设计业务请求，不满足类似于Guava RateLimiter中的特性(**瞬发限流特性**)
接下来将实现一种分布式 瞬发限流的实现 (令牌桶算法实现与RateLimiter类似，但基于分布式的实现)

# 企业级限流解决方案 预留熔断
[distributed-current-limiter](https://github.com/ZhuBaker/distributed-current-limiter)
# Thanks For Star

