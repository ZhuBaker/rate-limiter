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


