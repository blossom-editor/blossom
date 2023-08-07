# 系统预热

> 官方例子：[https://github.com/alibaba/Sentinel/blob/master/sentinel-demo/sentinel-demo-basic/src/main/java/com/alibaba/csp/sentinel/demo/flow/WarmUpFlowDemo.java](https://github.com/alibaba/Sentinel/blob/master/sentinel-demo/sentinel-demo-basic/src/main/java/com/alibaba/csp/sentinel/demo/flow/WarmUpFlowDemo.java)

## 基本用法

预热的意思即为将流量的的控制逐步放宽，允许通过的流量逐步增大，直到所允许的最大流量。我是真的服了, 你看这个? 为啥么饿饿饿饿饿 饿饿饿饿饿饿饿饿饿饿

- 预热并不是从收到的第一个请求开始预热，而是当请求量达到允许最大值的 1/3 时才开始进入预热阶段。低于 1/3 时，系统会一直处于冷状态
- 为什么是 1/3，而不是 1/4，1/5 呢？是通过`sentinel.properties`文件中配置`csp.sentinel.flow.cold.factor`中配置的，可以修改该值，该值越大，预热开始的初始值就越低，相应的 QPS 在预热阶段的变化范围就越大

1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111
1111111111111111111111111111111111111111111111111111111111111111111

配置示例

```java
public String flowCheck () throws InterruptedException {
    FlowRule rule = new FlowRule();
    rule.setLimitApp("default");
    // 资源名
    rule.setResource("getTest");
    // 系统预热限流方式
    rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP);
    // 阈值类型,QPS,系统预热其实只能针对QPS进行判断
    rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
    // 阈值个数,系统进入稳定期时最大的放行速度,QPS
    rule.setCount(50);
    // 系统进入稳定期需要的时长,单位秒,实际并不是精准的在这个时间进入稳定期的
    rule.setWarmUpPeriodSec(60);
    try (Entry ignored = SphU.entry("getTest")) {
        System.out.println("SUCC");
    } catch (BlockException e) {
        System.out.println("> FAIL");
    }
}
```

系统预热的请求流量变化如下图：
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1875939/1635301000694-bc79f1ee-f6fe-43d9-8241-ce357d64e1b5.png#averageHue=%23ededed&clientId=u515bedae-5c42-4&from=paste&height=300&id=VctBl&originHeight=600&originWidth=1250&originalType=binary&ratio=1&rotation=0&showTitle=false&size=56651&status=done&style=shadow&taskId=uf75f5149-fb5c-4cfe-aab0-525995e6198&title=&width=525)

## 源码解析

> 官方源码：[https://github.com/alibaba/Sentinel/blob/release-1.8/sentinel-core/src/main/java/com/alibaba/csp/sentinel/slots/block/flow/controller/WarmUpController.java](https://github.com/alibaba/Sentinel/blob/release-1.8/sentinel-core/src/main/java/com/alibaba/csp/sentinel/slots/block/flow/controller/WarmUpController.java)

在阅读源码前，整理一些逻辑会有助于源码的阅读。
在 Sentinel 中，对于系统预热的设计思想阐述的并不详尽，只是说主要想法来自[《Guava》](https://github.com/google/guava)，所以阅读这部分源码前，可以先读懂[《Guava》](https://github.com/google/guava)的实现有助于我们使用 **Sentinel。**
[《Guava》](https://github.com/google/guava)将这种限流方式称为平滑限流`SmoothRateLimiter`，其中包括`SmoothWarmingUp`平滑预热，以及`SmoothBursty`平滑突发两种流控方式，其中`SmoothBursty`即我们所熟知的漏桶算法。我们的主要目的是阅读`SmoothRateLimiter`抽象类中的说明以及`SmoothWarmingUp`的实现方式。

> Guava 官方源码：[https://github.com/google/guava/blob/master/guava/src/com/google/common/util/concurrent/SmoothRateLimiter.java](https://github.com/google/guava/blob/master/guava/src/com/google/common/util/concurrent/SmoothRateLimiter.java)

```java

  /*
   * How is the RateLimiter designed, and why?
   * RateLimiter 是如何设计的，为什么?
   *
   * The primary feature of a RateLimiter is its "stable rate", the maximum rate that it should
   * allow in normal conditions. This is enforced by "throttling" incoming requests as needed. For
   * example, we could compute the appropriate throttle time for an incoming request, and make the
   * calling thread wait for that time.
   * RateLimiter 的主要特点是它的“稳定速率 - stable rate”，即在正常情况下允许的最大速率。这是通过根据需要 "限制 - throttling" 传入请求来实现的。
   * 例如，我们可以计算传入请求的适当节流时间，并让调用线程等待该时间。
   *
   * The simplest way to maintain a rate of QPS is to keep the timestamp of the last granted
   * request, and ensure that (1/QPS) seconds have elapsed since then. For example, for a rate of
   * QPS=5 (5 tokens per second), if we ensure that a request isn't granted earlier than 200ms after
   * the last one, then we achieve the intended rate. If a request comes and the last request was
   * granted only 100ms ago, then we wait for another 100ms. At this rate, serving 15 fresh permits
   * (i.e. for an acquire(15) request) naturally takes 3 seconds.
   * 维护 QPS 速率的最简单方法是保存最后一个被授予请求的时间戳，并确保从那时起已经经过了(1/QPS)秒。
   * 例如，对于QPS=5(每秒5个令牌)的速率，如果我们确保一个请求在最后一个请求之后200毫秒之前没有被授予，那么我们就达到了预期的速率。
   * 如果一个请求来了，而最后一个请求在100毫秒之前才被授予，那么我们再等待100毫秒。按照这个速度，服务15个新的许可(即获取15个)自然需要3秒。
   * ===================================================== 批注 =========================================================
   * 这里说的其实是漏桶的大致原理，即每个请求之间的间隔是相同的，每次保存最后一个请求的通过时间。下个请求的通过时间则需要向后递延一个令牌
   * 的下发周期。Guava 的实现与 Sentinel 略微不同，Guava 更注重调整每个请求之间的间隔，而 Sentinel 更注重 QPS，所以 Guava 的实现其
   * 实是基于漏桶的，而 Sentinel 与令牌桶更相似。
   * 这里记录几个名词，下面也会用到：
   * ● rate：速率，相当于每秒允许通过的资源，当然也可以是QPS，线程数，网络带宽等等...
   * ● permits：许可，可理解为1个令牌
   * ● fresh permits：新许可，新创建的令牌
   * ===================================================================================================================
   *
   * It is important to realize that such a RateLimiter has a very superficial memory of the past:
   * it only remembers the last request. What if the RateLimiter was unused for a long period of
   * time, then a request arrived and was immediately granted? This RateLimiter would immediately
   * forget about that past underutilization. This may result in either underutilization or
   * overflow, depending on the real world consequences of not using the expected rate.
   * 重要的是要意识到这样的情况，一个 RateLimiter 对过去一段时间的系统情况的记忆是粗浅的:
   * 这句话可以理解为，它只记得最后一个请求。如果限流器在很长一段时间内未使用，然后一个请求到达并立即被批准后，会怎么样?
   * 这个限流器会立即忘记过去的这段时间系统是很长时间不被使用的。这可能导致资源未充分利用或溢出，具体取决于没有使用预期速率的实际结果。
   * ===================================================== 批注 =========================================================
   * 限流器并不了解系统的整体运行情况，系统在一段时间内未被使用，然后突然被访问，那对于限流器来说，它会认为记录这次请求为最后一次请求，并且认为系统过去是一直被使用的。
   * 名词：
   * ● past underutilization：过去未充分利用，(用系统过去一段时间处于冷却阶段可能更好理解)
   * ===================================================================================================================
   *
   * Past underutilization could mean that excess resources are available. Then, the RateLimiter
   * should speed up for a while, to take advantage of these resources. This is important when the
   * rate is applied to networking (limiting bandwidth), where past underutilization typically
   * translates to "almost empty buffers", which can be filled immediately.
   * 过去的资源未充分利用可能意味着有多余的资源可用。那么，限流器应该加速一段时间，以便利用这些资源。
   * 当速率应用于网络(限制带宽)时，这一点很重要，因为过去的未充分利用通常会导致“几乎为空的缓冲区”，这些缓冲区可以立即被填充。
   * ===================================================== 批注 =========================================================
   * 情况一：允许更大的流量通过，这是为了让这些闲置的资源更快的被利用起来，随后逐渐将流量降低。
   * ===================================================================================================================
   *
   *
   * On the other hand, past underutilization could mean that "the server responsible for handling
   * the request has become less ready for future requests", i.e. its caches become stale, and
   * requests become more likely to trigger expensive operations (a more extreme case of this
   * example is when a server has just booted, and it is mostly busy with getting itself up to
   * speed).
   * 另一方面,过去未充分利用可能意味着 “服务器负责处理请求已经变得不那么准备未来的请求”, 即其缓存变得陈旧, 请求变得更容易触发昂贵操作
   * (一个更极端的例子,这个例子是当一个服务器刚刚启动,而且它主要忙于让自己跟上速度)。
   * ===================================================== 批注 =========================================================
   * 情况二：允许较少的流量通过，随后逐渐将流量提高。因为这时一些功能的缓存可能已经失效，或者一些保持的连接已经过期需要重新连接等情况。
   * ===================================================================================================================
   *
   * To deal with such scenarios, we add an extra dimension, that of "past underutilization",
   * modeled by "storedPermits" variable. This variable is zero when there is no underutilization,
   * and it can grow up to maxStoredPermits, for sufficiently large underutilization. So, the
   * requested permits, by an invocation acquire(permits), are served from:
   * - stored permits (if available)
   * - fresh permits (for any remaining permits)
   * 为了处理这样的场景，我们添加了一个额外的维度，即由 “storedPermits” 变量建模的 “过去未充分利用” 维度。
   * 当过去没有未充分利用的资源时，该变量为零，当过去有未充分利用的资源时，它可以增长到 maxStoredPermits 令牌桶的最大容量。
   * 因此，请求的许可证，通过从以下两个来调用 acquire(permits) 获取:
   * - 存储的令牌, 即令牌桶(如有)
   * - 新发的令牌(任何剩余许可证)
   * ===================================================== 批注 =========================================================
   * 为了处理这样的场景，Guava 设置了一个变量storedPermits，用来衡量过去未充分利用这一指标。当系统处于忙时：storedPermits为0；当系统
   * 冷却时，storedPermits可以逐渐增长到maxStoredPermits。
   * 名词：
   * ●    storedPermits：变量，意思为存储起来的令牌数，也可理解为令牌桶。
   * ● maxStoredPermits：变量，表示storedPermits可以存储的最大值，也可理解为令牌桶的最大容量。
   * ===================================================================================================================
   *
   * How this works is best explained with an example:
   * 最好有一个例子解释它时如何工作的:
   *
   * For a RateLimiter that produces 1 token per second, every second that goes by with the
   * RateLimiter being unused, we increase storedPermits by 1. Say we leave the RateLimiter unused
   * for 10 seconds (i.e., we expected a request at time X, but we are at time X + 10 seconds before
   * a request actually arrives; this is also related to the point made in the last paragraph), thus
   * storedPermits becomes 10.0 (assuming maxStoredPermits >= 10.0). At that point, a request of
   * acquire(3) arrives. We serve this request out of storedPermits, and reduce that to 7.0 (how
   * this is translated to throttling time is discussed later). Immediately after, assume that an
   * acquire(10) request arriving. We serve the request partly from storedPermits, using all the
   * remaining 7.0 permits, and the remaining 3.0, we serve them by fresh permits produced by the
   * rate limiter.
   * 对于每秒产生1个令牌的 RateLimiter，在 RateLimiter 未使用的情况下每过一秒，我们将 storedPermit 增加1。
   * 假设我们让 RateLimiter 闲置10秒(即，我们期望在X时间有一个请求，但我们在X + 10秒的时间请求实际到达;
   * 这也与上一段中提到的一点有关)，因此 storedPermit 变成了10.0(假设 maxStoredPermits >= 10.0)。此时，获取请求(3)到达。
   * 我们从 storedPermit 提供此请求，并将其减少到7.0(稍后将讨论如何将其转换为节流时间)。
   * 紧接着，假设一个获取(10)请求到达。我们服务的请求部分来自 storedPermit，使用所有剩余的7.0许可证，和剩余的3.0许可证，
   * 我们提供他们由速率限制器产生的新鲜许可证。
   * ===================================================== 批注 =========================================================
   * 此处举了一个例子来说明：
   * 1. 限流器每秒产生1个令牌，最多存储10个令牌。
   * 2. 限流器闲置了10秒，也就是已经存储了10个令牌了。
   * 3. 这时有3个请求来了，那么就从令牌桶中取3个令牌给这三个请求。
   * 4. 紧接着又来了10个请求，那么从令牌桶中取剩余的7个令牌给其中的7个请求。
   * 5. 剩余的3个请求，就要等限流器重新生成了。
   * ===================================================================================================================
   *
   * We already know how much time it takes to serve 3 fresh permits: if the rate is
   * "1 token per second", then this will take 3 seconds. But what does it mean to serve 7 stored
   * permits? As explained above, there is no unique answer. If we are primarily interested to deal
   * with underutilization, then we want stored permits to be given out /faster/ than fresh ones,
   * because underutilization = free resources for the taking. If we are primarily interested to
   * deal with overflow, then stored permits could be given out /slower/ than fresh ones. Thus, we
   * require a (different in each case) function that translates storedPermits to throttling time.
   * 我们已经知道提供 3 个 fresh permits 需要多少时间: 如果速率是 "每秒1个令牌" ，那么这将需要3秒。
   * 但是服务 7 个 stored permits 证意味着什么?
   * 如上所述，没有唯一的答案。
   * - 如果我们主要关心的是未充分利用的的资源，那么我们希望 stored permits 比 fresh permits 发放得更快, 因为未充分利用的资源 = 免费资源。
   * - 如果我们主要想处理溢出的问题，那么 stored permits 的发放速度可能会比 fresh permits 慢。
   * 因此，我们需要一个(在每种情况下不同)函数将 storedPermit 属性转换为节流时间。
   * ===================================================== 批注 =========================================================
   * 需要一个函数, 能够控制存储的令牌的发放速度
   * ===================================================================================================================
   *
   * This role is played by storedPermitsToWaitTime(double storedPermits, double permitsToTake). The
   * underlying model is a continuous function mapping storedPermits (from 0.0 to maxStoredPermits)
   * onto the 1/rate (i.e. intervals) that is effective at the given storedPermits. "storedPermits"
   * essentially measure unused time; we spend unused time buying/storing permits. Rate is
   * "permits / time", thus "1 / rate = time / permits". Thus, "1/rate" (time / permits) times
   * "permits" gives time, i.e., integrals on this function (which is what storedPermitsToWaitTime()
   * computes) correspond to minimum intervals between subsequent requests, for the specified number
   * of requested permits.
   * 这个角色由 storedPermitsToWaitTime(double storedpermit, double permitsToTake)方法来扮演。
   * 底层模型是一个连续的函数，将 storedPermit (从 0.0 到 maxStoredPermits ) 映射到1/请求速率(即每个令牌的间隔)，这在给定的 storedPermit 中是有效的。
   * "storedPermit" 本质上度量的时未使用的时间; 我们把未使用的时间花在 购买或储存 permits(令牌) 上。
   * Rate 是 [permits / time](令牌/时间, 例如令牌为5,时间为1s,则请求的速率就是0.2s) ，因此 [1 / Rate = time / permits]。
   * 因此，[1/rate](time/permits)乘以 permits 给出时间，即这个函数(storedPermitsToWaitTime()) 计算的结果就是在指定请求
   * 令牌 permits 的数量时, 后续请求之间的最小间隔.
   *
   * Here is an example of storedPermitsToWaitTime: If storedPermits == 10.0, and we want 3 permits,
   * we take them from storedPermits, reducing them to 7.0, and compute the throttling for these as
   * a call to storedPermitsToWaitTime(storedPermits = 10.0, permitsToTake = 3.0), which will
   * evaluate the integral of the function from 7.0 to 10.0.
   * 这里有一个storedPermitsToWaitTime的例子：
   * 如果storedPermits == 10.0，我们需要3个 permits，我们从storedPermits取出，并将其减少至7.0，并计算这些作为调用的限流
   * storedPermitsToWaitTime (storedPermits = 10.0, permitsToTake = 3.0)，它将计算该函数从7.0到10.0的积分。
   *
   * Using integrals guarantees that the effect of a single acquire(3) is equivalent to {
   * acquire(1); acquire(1); acquire(1); }, or { acquire(2); acquire(1); }, etc, since the integral
   * of the function in [7.0, 10.0] is equivalent to the sum of the integrals of [7.0, 8.0], [8.0,
   * 9.0], [9.0, 10.0] (and so on), no matter what the function is. This guarantees that we handle
   * correctly requests of varying weight (permits), /no matter/ what the actual function is - so we
   * can tweak the latter freely. (The only requirement, obviously, is that we can compute its
   * integrals).
   * 使用积分可以保证单个acquire(3)的效果等同于{acquire(1);acquire(1);acquire(1);}，或{acquire(2);acquire(1);}等，因为函数
   * 在[7.0,10.0]中的积分等价于[7.0,8.0]，[8.0,9.0]，[9.0,10.0](等等)的积分之和，无论函数是什么。这保证了我们正确地处理不同权重的
   * 请求，而不管实际的功能是什么——所以我们可以自由地调整后者。(唯一的要求是，我们可以计算它的积分)。
   *
   * Note well that if, for this function, we chose a horizontal line, at height of exactly (1/QPS),
   * then the effect of the function is non-existent: we serve storedPermits at exactly the same
   * cost as fresh ones (1/QPS is the cost for each). We use this trick later.
   * 请注意，如果对于这个函数，我们选择了一条高度恰好为(1/QPS)的水平线，那么这个函数的效果是不存在的：我们以与 fresh permits 完全相同
   * 的成本提供storedPermits(1/QPS是每个 permits 的成本)。我们稍后会用到这个技巧。
   *
   * ^ 令牌的生成时间，越小说明越快
   * |
   * +----------
   * |
   * |
   * |
   * +--------------→
   *
   * If we pick a function that goes /below/ that horizontal line, it means that we reduce the area
   * of the function, thus time. Thus, the RateLimiter becomes /faster/ after a period of
   * underutilization. If, on the other hand, we pick a function that goes /above/ that horizontal
   * line, then it means that the area (time) is increased, thus storedPermits are more costly than
   * fresh permits, thus the RateLimiter becomes /slower/ after a period of underutilization.
   * 如果我们选择一个在水平线以下的函数，这意味着我们减少了函数的面积，从而减少了时间。因此，RateLimiter在一段时间的未充分利用后变得/更
   * 快。也就是令牌的生成速度变低了
   * ^ 令牌的生成时间，越小说明越快
   * |
   * +-----\
   * |      \
   * |       \
   * |        \
   * +--------------→
   * 如果我们选择一个在水平线以上的函数，那么这意味着面积(时间)增加了，因此存储许可证比新的许可证更昂贵，因此RateLimiter在一段时间未充
   * 分利用后变得慢。也就是令牌的生成速度变慢了，也就让系统的通过数变低了。
   * ^ 令牌的生成时间，越小说明越快
   * |
   * |        /
   * |       /
   * |      /
   * +-----/
   * +--------------→
   *
   *
   * Last, but not least: consider a RateLimiter with rate of 1 permit per second, currently
   * completely unused, and an expensive acquire(100) request comes. It would be nonsensical to just
   * wait for 100 seconds, and /then/ start the actual task. Why wait without doing anything? A much
   * better approach is to /allow/ the request right away (as if it was an acquire(1) request
   * instead), and postpone /subsequent/ requests as needed. In this version, we allow starting the
   * task immediately, and postpone by 100 seconds future requests, thus we allow for work to get
   * done in the meantime instead of waiting idly.
   * 最后，但并非最不重要：考虑一个速率为每秒1许可证的RateLimiter，目前完全未使用，并且出现了一个昂贵的获取(100)请求。仅仅等待100秒，
   * 然后/然后/开始实际的任务是没有意义的。为什么什么都不做而等待?一个更好的方法是立即/允许/请求(就好像它是一个acquire(1)请求)，然后根
   * 据需要推迟/后续/请求。在这个版本中，我们允许立即启动任务，并将未来的请求推迟100秒，因此我们允许在此期间完成工作，而不是无所事事地等待。
   *
   * This has important consequences: it means that the RateLimiter doesn't remember the time of the
   * _last_ request, but it remembers the (expected) time of the _next_ request. This also enables
   * us to tell immediately (see tryAcquire(timeout)) whether a particular timeout is enough to get
   * us to the point of the next scheduling time, since we always maintain that. And what we mean by
   * "an unused RateLimiter" is also defined by that notion: when we observe that the
   * "expected arrival time of the next request" is actually in the past, then the difference (now -
   * past) is the amount of time that the RateLimiter was formally unused, and it is that amount of
   * time which we translate to storedPermits. (We increase storedPermits with the amount of permits
   * that would have been produced in that idle time). So, if rate == 1 permit per second, and
   * arrivals come exactly one second after the previous, then storedPermits is _never_ increased --
   * we would only increase it for arrivals _later_ than the expected one second.
   * 这有重要的结果:它意味着RateLimiter不记得_last_请求的时间，但它记得_next_请求的(预期的)时间。这也使我们能够立即判断(参见tryAcquire(timeout))
   * 特定的超时是否足以让我们到达下一个调度时间点，因为我们总是保持这个时间点。和我们所说的“一个未使用的RateLimiter”概念也定义为:当我们
   * 观察到“预期下一个请求到达的时间”实际上是在过去,然后现在(过去)的差异RateLimiter正式未使用的时间,这是我们翻译的时间storedPermits。
   * (我们增加storedPermits的数量，在空闲时间将会生产的许可证)。因此，如果rate == 1 permit / s，并且到达时间恰好比前一个晚一秒，那么
   * storedpermit将永远不会增加——我们只会为比预期晚一秒到达的到达增加它。
   */

  /**
   * This implements the following function where coldInterval = coldFactor * stableInterval.
   * 这实现了以下函数，其中coldInterval = coldFactor * stableInterval。
   * coldInterval     ：令牌生成的最慢速度，此时系统允许的QPS是最低的
   * stableInterval   ：令牌生成的最快速度，此时系统允许的QPS是最高的
   * thresholdPermits ：令牌最少的个数
   * maxPermits       ：令牌最大的个数
   * warmupPeriod     ：系统预热的时间，也就是令牌数从 0 -> maxPermits 所需的时间
   *                  　　　　　　　　　也是令牌生成速 0 —> coldInterval 所需的时间
   *
   * <pre>
   *          ^ throttling
   *          |
   *    cold  +                  /
   * interval |                 /.
   *          |                / .
   *          |               /  .   ← "warmup period" is the area of the trapezoid between
   *          |              /   .     thresholdPermits and maxPermits
   *          |             /    .
   *          |            /     .
   *          |           /      .
   *   stable +----------/  WARM .
   * interval |          .   UP  .
   *          |          . PERIOD.
   *          |          .       .
   *        0 +----------+-------+--------------→ storedPermits
   *          0 thresholdPermits maxPermits
   * </pre>
   *
   * Before going into the details of this particular function, let's keep in mind the basics:
   * 在深入了解这个特定函数的细节之前，让我们先记住一些基础知识:
   * <ol>
   *   <li>The state of the RateLimiter (storedPermits) is a vertical line in this figure.
   *       存储的令牌个数在图中是一条垂直线。
   *
   *   <li>When the RateLimiter is not used, this goes right (up to maxPermits)
   *       当限流器不使用时，令牌数会一直向右增长(直到最大许可停止增长)
   *
   *   <li>When the RateLimiter is used, this goes left (down to zero), since if we have
   *       storedPermits, we serve from those first
   *       当限流器在使用时，这将向左下降(最终下降到零)，因为如果我们有存储的令牌，我们将首先这里获取令牌
   *
   *   <li>When _unused_, we go right at a constant rate! The rate at which we move to the right is
   *       chosen as maxPermits / warmupPeriod. This ensures that the time it takes to go from 0 to
   *       maxPermits is equal to warmupPeriod.
   *       当未被使用时，我们以恒定的速度前进, 向右移动的速率选择为 maxPermit / warmupPeriod。这确保从 0 到 maxPermit 所花费的时间
   *       等于 warmupPeriod(预热时间)。
   *
   *   <li>When _used_, the time it takes, as explained in the introductory class note, is equal to
   *       the integral of our function, between X permits and X-K permits, assuming we want to
   *       spend K saved permits.
   *       当使用时，它所花费的时间，正如在导论笔记中解释的，等于我们的函数的积分，在X许可和X-K许可之间，假设我们想花费K个保存的许可。
   *
   * </ol>
   *
   * <p>In summary, the time it takes to move to the left (spend K permits), is equal to the area of
   * the function of width == K.
   * 总之，向左移动的时间(也就是使用掉K个许可)，等于宽度为K的函数的面积。
   *
   * <p>Assuming we have saturated demand, the time to go from maxPermits to thresholdPermits is
   * equal to warmupPeriod. And the time to go from thresholdPermits to 0 is warmupPeriod/2. (The
   * reason that this is warmupPeriod/2 is to maintain the behavior of the original implementation
   * where coldFactor was hard coded as 3.)
   * 假设我们有饱和的需求，从最大令牌数到稳定令牌数的时间等于预热时间。从稳定令牌数到0的时间是预热时间/2 。
   * (预热时间/2的原因是为了维护原始实现的行为，其中 coldFactor 被硬编码为3。)
   *
   * <p>It remains to calculate thresholdsPermits and maxPermits.
   * 接下来还需要计算 thresholdsPermits 和 maxPermit。
   *
   * <ul>
   *   <li>The time to go from thresholdPermits to 0 is equal to the integral of the function
   *       between 0 and thresholdPermits. This is thresholdPermits * stableIntervals. By (5) it is
   *       also equal to warmupPeriod/2. Therefore
   *       <blockquote>
   *       thresholdPermits = 0.5 * warmupPeriod / stableInterval
   *       </blockquote>
   *       从 thresholdsPermits 到 0 的时间等于函数在 0 和 thresholdsPermits 之间的积分。
   *       这是 thresholdsPermits * stableIntervals。(5)它也等于 warmupPeriod/2 。因此
   *
   *   <li>The time to go from maxPermits to thresholdPermits is equal to the integral of the
   *       function between thresholdPermits and maxPermits. This is the area of the pictured
   *       trapezoid, and it is equal to 0.5 * (stableInterval + coldInterval) * (maxPermits -
   *       thresholdPermits). It is also equal to warmupPeriod, so
   *       <blockquote>
   *       maxPermits = thresholdPermits + 2 * warmupPeriod / (stableInterval + coldInterval)
   *       </blockquote>
   *       从最大令牌数到稳定令牌数的时间等于稳定令牌数和最大令牌数之间函数的积分。
   *       这是图中梯形的面积，它等于0.5 * (stableInterval + coldInterval) * (maxpermit - thresholdpermit)。它也等于warmupPeriod，所以
   * </ul>
   */
```

上面是关于[《Guava》](https://github.com/google/guava)对限流的设计解释。下面笔者尝试用自己的理解来说明一下，相较于翻译过来的文字可能门槛会更容易理解一些。在这部分中，可能会涉及到一些较为基础的数学知识，包含：**斜率公式**，**斜截式，截距**，**定积分，梯形的面积计算。**如果熟悉这几种数学知识，对下面的说明会更好理解。

我们知道限流功能主要是控制请求的速率，一个很容易理解的做法是，系统记录下次允许通过的时间，当最新的请求到来时，如果最新请求小于下次允许通过的时间，那么新请求将被拒绝，或让新等待一段时间后再通过，等待的时间就是下次允许通过的时间`减去`新请求到来的时间。这里其实就是漏桶的大致原理(在前文中已经详细阐述过，这里不在重复解释了)。我们需要注意的是，限流器在使用时具有局限性，那就是**它对系统一段时间内的负载的记忆是粗浅的**，比如：系统已经很长一段时间未使用，这时突然来了请求，那么限流器就会记录这次请求的时间或下次允许通过的时间，这时对于限流器而言，是不知道系统已经很久未被使用了。那这意为着什么呢？

这意为着限流器将继续按照我们设定的速率来放行请求，但这可能并不是我们期望发生的事情，比如：

- **情况一**：系统已经很长一段时间未使用，这时突然来了请求，我们期望请求限流器能**按照比平时更大的速率来放行请求**，因为过去一段时间系统未使用可能说明，我们有很多资源被浪费了，比如在接收网络请求时，一段时间没有被使用，则说明系统有充足的缓冲区能允许更多的网络请求来使用这些未被使用的系统资源。
- **情况二**：系统已经很长一段时间未使用，这时突然来了请求，我们期望请求限流器能**按照比平时更小的速率来放行请求**，因为过去一段时间系统未使用可能说明，我们有很多资源需要重新获取或创建，比如一些常用的缓存被释放、一些保持的连接已经断开需要重连。

为了处理这样的情景，[《Guava》](https://github.com/google/guava)设计了一个变量`storedPermits`(存储的令牌)**来衡量这一段未使用的系统资源，当系统繁忙时，`storedPermits`为零，当系统闲置时，`storedPermits`可以逐渐增长(这意为着系统闲置的时间越来越长了)。当然，`storedPermits`是不可以无限增长的，毕竟系统资源不是无限的。那么可增长的最大值，也可设定为`maxPermits`(最大令牌数)**。到这里可以看出，`storedPermits`的设计有些类似令牌桶，用令牌桶来存储令牌，让系统在有突发请求时可以有令牌使用，但其实[《Guava》](https://github.com/google/guava)的限流器从始至终就是漏桶的设计方式，而不是令牌桶。

下面先让我们用一个例子来模拟这种情况：假如限流器每秒允许一个请求通过，也就相当于一秒产生一个令牌，假设此时已经 10 秒钟没有请求到来了，那`storedPermits`存储的令牌数就为`10`，也相当于系统已经闲置了`10`秒了，举一个极端的例子，此时我们数据库链接已经超时并断开，所需要的缓存也已经过期，**那么这时如果我们从`storedPermits`中取出了一个令牌给新到来的\*\***请求 1**，平时一个请求需要 1 秒钟可以处理完业务，现在由于请求需要做更多的准备工作(如创建链接，刷新缓存等等)该请求需要更多的时间。**这时当请求 2 到来，由于`storedPermits`尚有`9`个令牌，所以我们又给一个新的\***\*请求 2\*\***下发了`storedPermits`的令牌\*\*，但前一个请求实际并未处理完成，且新的请求可能会与第一个请求做一些重复的操作(如创建链接，刷新缓存)，以及占用一些共用资源，这时就意味着我们的系统超过了能承载的上限，而变得不那么稳定了(如频繁 GC，内存溢出，响应超时等等)。

从上述例子也可以看出，令牌桶只能保存令牌，但不能决定令牌的下发速度，如上面例子中，**请求 1**和**请求 2**由于都是使用的`storedPermits`存储起来的令牌，所以按理来说，这两个请求应该以区别于正常的速率通过。所以[《Guava》](https://github.com/google/guava)的设计是，根据`storedPermits`的大小，来计算每个请求允许通过时间，如果`storedPermits`越大，那么允许通过的时间就要越快(对应情况一)或越慢(对应情况二)，也就是说，我们需要把`storedPermits`与系统的放行速率，即令牌的生成速度`interval`来建立映射关系。那么我们可以将这个映射关系放到坐标系中，如下：

```java
  ^ y: 令牌的生成时间，即每个请求之间的间隔 Interval
  |
  |
  |
  |
  |
  |
0 +------------------→ x:令牌的数量 storedPermits
```

用一张正规的坐标系表示，则如下：

- **情况一**：表示随着令牌数变多，令牌的生成时间逐渐变快
- **情况二**：表示随着令牌数变多，令牌的生成时间逐渐变慢

![image.png](https://cdn.nlark.com/yuque/0/2021/png/1875939/1639295022083-d4b14b09-f7dd-4013-9e40-bfa64e678e96.png#averageHue=%23faf4f2&clientId=u8fdf4a17-d0a2-4&from=paste&height=197&id=u1233f0e0&originHeight=197&originWidth=886&originalType=binary&ratio=1&rotation=0&showTitle=false&size=35881&status=done&style=shadow&taskId=u611a681d-5a5d-4224-bee3-799c07e00f5&title=&width=886)
下面将以以情况二举例（实际[《Guava》](https://github.com/google/guava)也只实现了情况二），由于`storedPermits`与`interval`的比值不同，那么二者的映射关系在坐标系中的呈现也是不同的，例如如下几种：
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1875939/1639295087738-0bf0ea2f-abf9-4ce9-8250-aafa534bc874.png#averageHue=%23f9f8f7&clientId=u8fdf4a17-d0a2-4&from=paste&height=350&id=u76ed5d28&originHeight=350&originWidth=892&originalType=binary&ratio=1&rotation=0&showTitle=false&size=65631&status=done&style=shadow&taskId=u1ec12994-0a99-4d8e-9753-8fc76dbd645&title=&width=892)
这三条线中，黑色线的**最慢速率**一定是最高的，黄色线的**最慢速率**一定是最低的，因为 Y 轴代表了每个**令牌生成的间隔，生成间隔越大，代表了单位时间内允许通过的请求数就越低。**另外需要补充的一点是，Y 轴是不可能为 0 的，如果令牌的生成间隔为 0，则说明单位时间内允许通过的请求时无限的，所以速率一定会有一个大于 0 的最小值，并且速率 = 1 秒/每秒通过的请求，这个值我们称之为`stableInterval`(稳定间隔)**，将稳定间隔的含义代入到这个坐标系中，结果如下：可以看到 Y 轴的最小值为`1`(仅考虑第一象限，其他象限的情况是不存在的。)
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1875939/1639295184460-f468fc64-6060-45b3-a8d3-43f7666d2d6b.png#averageHue=%23f9f8f7&clientId=u8fdf4a17-d0a2-4&from=paste&height=352&id=ubbf51434&originHeight=352&originWidth=892&originalType=binary&ratio=1&rotation=0&showTitle=false&size=65791&status=done&style=shadow&taskId=u79cb5daa-19f7-427a-b26f-1fdb1c34226&title=&width=892)
上图还可以体现出另一个现象，通俗讲就是斜线的倾斜程度越大，则这个限流器的最低速率就越小，比如下图中当令牌桶的个数为 10 时，三条线对应的**令牌生成的间隔\*\*是不同的

- 黄色为：4
- 蓝色为：3
- 黑色为：2

![image.png](https://cdn.nlark.com/yuque/0/2021/png/1875939/1639295322169-a95d7b85-2cd0-4c89-b116-34e01e38f381.png#averageHue=%23f9f8f7&clientId=u8fdf4a17-d0a2-4&from=paste&height=357&id=u0c923def&originHeight=357&originWidth=889&originalType=binary&ratio=1&rotation=0&showTitle=false&size=66573&status=done&style=shadow&taskId=u5adff63b-9d50-4601-a700-a60f387e384&title=&width=889)

那么当令牌数为`10`，即`maxPermits`(最大令牌数)时，**一个令牌的生成速度应该是多少呢？很高兴这里不需要浪费过多心智了，因为[《Guava》](1)将此值设置为`stableInterval`的`3`倍，在此例中就是`3`秒，我们称之为`coldInterval`(冷却间隔)。**(虽然不理解为什么是 3，但这个值应该与`HashMap`的`_DEFAULT_LOAD_FACTOR_`属性一样，是一个多次测试得来的值，是一个基于性能和稳定性均和考虑的值)。也就是黑色线在`y`轴到达`3`的时候，将不会继续增长了。那么这条线将会被固定为如下：

![image.png](https://cdn.nlark.com/yuque/0/2021/png/1875939/1639295988650-e78c9e47-733c-4bd0-96f1-251241c7bdbc.png#averageHue=%23f9f9f9&clientId=u8fdf4a17-d0a2-4&from=paste&height=353&id=u14dafebb&originHeight=353&originWidth=893&originalType=binary&ratio=1&rotation=0&showTitle=false&size=45755&status=done&style=shadow&taskId=uf4580c46-31da-44b6-8079-526e434214b&title=&width=893)

那么最大令牌数是如何确定的呢？[《Guava》](1)将此参数叫做`warmupPeriod`(预热时间)，\*\*他表示限流器能存储的最大令牌数，以及从系统从冷却状态到稳定状态所需要的时间。现状我们来整理一下我们设定的各项条件：

- ` stableInterval`：常量，稳定间隔，当前例子中为：`1`。
- ` coldInterval`：常量，冷却间隔，当前例子中未：`3`。
- `thresholdPermits`：常量，稳定间隔对应的令牌数，当前例子中为：`0`。
- ` maxPermits`：常量，冷却间隔对应的令牌数，当前例子中为：`10`。该值 = `stableInterval` \* `warmupPeriod`。
- ` storedPermits`：变量，当前存储的令牌数。该值 = `(当前时间 - 上次通过时间) * stableInterval`

那么再次带入到这个例子中，假设此时`storedPermits`为`8`，那么如何确定此时该请求的下次通过时间呢？也就是如何计算对应 y 轴的值呢？

通过斜率公式，我们可以计算该值，**斜率**的公式的，如下：

![](https://cdn.nlark.com/yuque/__latex/3e4f3cab19eb730778906ba6492b4bc7.svg#card=math&code=k%3D%20%5Cfrac%7B%5CDelta%20y%7D%7B%5CDelta%20x%7D%20%3D%20%5Cfrac%7By_2%EF%BC%8Dy_1%7D%7Bx_2%EF%BC%8Dx_1%7D&id=XRt9n)

```text
$k= \frac{\Delta y}{\Delta x} = \frac{y_2－y_1}{x_2－x_1}$
```

其中`k`代表斜率，`(x1，y1)`和`(x2，y2)`代表了斜线上的任意两个点。其中(`y1`为`stableInterval`，`x1`为`0`)，(`y2`为`coldInterval`,`x2`为`maxPermits`)，，此时可以计算出斜率等于

![](https://cdn.nlark.com/yuque/__latex/44815c28ba5cc5aab68227919a5a895d.svg#card=math&code=k%3D%20%5Cfrac%7B%5CDelta%20y%7D%7B%5CDelta%20x%7D%20%3D%20%5Cfrac%7B3%EF%BC%8D1%7D%7B10%EF%BC%8D0%7D%3D0.2&id=srZMp)

```text
$k= \frac{\Delta y}{\Delta x} = \frac{3－1}{10－0}=0.2$
```

即`slope`(斜率) = (`coldInterval`-`stableInterval`) / (`maxPermits`-`thresholdPermits`)\*_
那么带入公式，就能算出，当`storedPermits`为`8`时，对应的`y`轴等于 =`(8-0) _ 0.2 + 1` =`2.6`。也就是存储的令牌数为8时，可以允许的最大QPS是`2.6`。

**到此，好像我们已经完成了限流器的设计，但其中其实仍然有很大的问题。**

由于`storedPermits`是个变化的值，所以每次请求到来时，我们都要计算当前时间与上次时间的间隔，来计算当前的存储的令牌数，按照当前例子，时隔`1`秒后有请求到来，这时需要先计算`storedPermits`的值，该值计算得`1`。带入公式算得该请求需要到`(1-0) * 0.2 + 1`=`1.2`秒后才能通过，但其实当前请求应该**立即通过，**所以为了避免这种请求的发生。[《Guava》](1)将稳定间隔对应的令牌数设置成了一段区间而不是固定值`0`，相当于为限流器设置了一段缓冲空间，在令牌数为这段区间内，系统仍然按照稳定速率来放行请求。如下图：

![image.png](https://cdn.nlark.com/yuque/0/2021/png/1875939/1639298424343-a94a7e58-ce6b-429f-a706-54c14d03ffa9.png#averageHue=%23faf8f8&clientId=u8fdf4a17-d0a2-4&from=paste&id=uaf5d1fa0&originHeight=350&originWidth=889&originalType=binary&ratio=1&rotation=0&showTitle=false&size=50473&status=done&style=shadow&taskId=u0d8fa858-3c47-4c31-a87f-bc99da0ba74&title=)

这段缓冲空间为：`warmupPeriod / 2`，那么上述条件中`thresholdPermits`(稳定间隔的令牌数)**的就成了`0~5`，也就是当存储的令牌数大于`5`时，才开始进入冷却阶段，此时才需要将请求的间隔延长，也就是常量`thresholdPermits`的字面意思，即**触发许可数**，也可叫做**触发令牌数。\*\*那么上面的计算工式响应的也要进行修改，如下：

![1](https://cdn.nlark.com/yuque/__latex/64df2ee8d4301c52c215cd67ffbfd17a.svg#card=math&code=k%3D%20%5Cfrac%7B%5CDelta%20y%7D%7B%5CDelta%20x%7D%20%3D%20%5Cfrac%7B3%EF%BC%8D1%7D%7B10%EF%BC%8D5%7D%3D0.4&id=XbH5J)

```
k= \frac{\Delta y}{\Delta x} = \frac{3－1}{10－5}=0.4
```

接下来我们继续看，假设当前存储的令牌为`10`，此时同时`6`个请求到来，则需计算：
1. 请求 1：通过时间`0.0`，当前令牌数为`10-1`，算得请求间隔为`(9-5) * 0.4 + 1`=`2.6`，即下次请求需要在`2.6`秒后。
2. 请求 2：通过时间`2.6`，当前令牌数为` 9-1`，算得请求间隔为`(8-5) * 0.4 + 1`=`2.2`，即下次请求需要在`2.2`秒后。
3. 请求 3：通过时间`4.8`，当前令牌数为` 8-1`，算得请求间隔为`(7-5) * 0.4 + 1`=`1.8`，即下次请求需要在`1.8`秒后。
4. 请求 4：通过时间`6.6`，当前令牌数为` 7-1`，算得请求间隔为`(6-5) * 0.4 + 1`=`1.4`，即下次请求需要在`1.4`秒后。
5. 请求 5：通过时间`8.0`，当前令牌数为` 6-1`，算得请求间隔为`(5-5) * 0.4 + 1`=`1.0`，即下次请求需要在`1.0`秒后。
6. 请求 6：通过时间`9.0`，当前令牌数为` 5-1`<触发令牌数，因此间隔=`stableInterval`，即下次请求需要在`1.0`秒后。
   > 这里需要拓展的一点，请求`1`其实并不需要等待`3`秒后才允许通过，因为此时限流器至少有`10`秒钟没有请求到来了，那么让请求 1 再等待 3 秒是无意义的。

可以看出：请求 5 时的令牌数为`thresholdPermits`(稳定间隔的令牌数)，**所以请求六的间隔即为稳定间隔，也就是此时系统进入了稳定期，但是限流器是在第`9`秒的时候允许第一个稳定间隔的请求通过的，这不符合我们设定的`warmupPeriod`(预热时间)：10 秒，**那么问题出在哪呢？其实是因为我们计算的并不那么准确，在[《Guava》](1)中，计算的并不是不同`storedPermits`对应的请求间隔。因为[《Guava》](1)的`SmoothWarmingUp`允许一次占用掉多个令牌，相当于并不是每次都执行`storedPermits-1`这样的操作，而是可能直接`storedPermits-3`，或`storedPermits-5`，那么就需要有一种方式直接计算`storedPermits`在`7-10`的时间，并且要求计算的结果与`7-8`，`8-9`，`9-10`的和是相同的，一种稳定的方式是计算这段的**积分(integrals)。**

首先，计算 x 轴上`7-10`的积分，必须明确一个函数，那么这个函数是什么呢？上图可知我们计算的就是在斜线区间的`xa`与`xb`的积分区间的定积分，那么这段斜线用**斜截式**表达则为：
![斜截式](https://cdn.nlark.com/yuque/__latex/3949503bba9afc0a00ba9efa3e07fded.svg#card=math&code=y%3Dkx%2Bb%3D0.4x%2B1&id=sqVpD)

```
$y=kx+b=0.4x+1$
```

其中`k`为斜率，`b`为与`Y**轴`的截距，图中斜线的截距当然不是`1`了，但若我们忽略掉横线部分，那么斜线的截距也就是`stableInterval`了，当然与其对应的，在计算令牌数时则为`storedPermit-thresholdPermits`，那么放在图中即为：
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1875939/1639312291312-4344fe25-346a-444d-aa4e-653a6d55494c.png#averageHue=%23fbfafa&clientId=ue08adfb6-d545-4&from=paste&height=342&id=ue0b88bf4&originHeight=342&originWidth=873&originalType=binary&ratio=1&rotation=0&showTitle=false&size=41434&status=done&style=shadow&taskId=u6739a2d7-f654-4985-b266-7f76950e328&title=&width=873)
那么这段函数定积分表达即为：

![111](https://cdn.nlark.com/yuque/__latex/385f3554544f9724cba53dd678681d7c.svg#card=math&code=%5Cint_%7Ba%7D%5E%7Bb%7D%200.4x%2B1%20dx&id=ZHjjD)

```
$\int_{a}^{b} 0.4x+1 dx$
```

此时再来计算上述例子：

1. 请求 1：通过时间`0.0`，当前令牌数为`10-5-1`，积分为`2.8`，即下次请求需要在`2.8`秒后。
2. 请求 2：通过时间`2.8`，当前令牌数为` 9-5-1`，积分为`2.4`，即下次请求需要在`2.4`秒后。
3. 请求 3：通过时间`5.2`，当前令牌数为` 8-5-1`，积分为`2.0`，即下次请求需要在`2.0`秒后。
4. 请求 4：通过时间`7.2`，当前令牌数为` 7-5-1`，积分为`1.6`，即下次请求需要在`1.6`秒后。
5. 请求 5：通过时间`8.8`，当前令牌数为` 6-5-1`，积分为`1.2`，即下次请求需要在`1.2`秒后。
6. 请求 6：通过时间`10.`，当前令牌数为` 5-1`<触发令牌数，因此间隔=`stableInterval`，即下次请求需要在`1.0`秒后。

可以看到，第一个稳定速率的请求，是在`warmupPeriod`(预热时间)：10 秒\*\*时被通过的。最后一个值得关注的是，[《Guava》](1)并没有使用复杂的积分计算公式，而是简单的计算了下图中不同梯形部分的面积，因为定积分实际就是在计算面积，这部分可见：[《定积分的意义为什么是面积？》](https://www.zhihu.com/question/351048683)

![image.png](https://cdn.nlark.com/yuque/0/2021/png/1875939/1639314749997-5985c70c-01f7-4b57-a855-5866f5c5d33c.png#averageHue=%23f9f8f8&clientId=ue08adfb6-d545-4&from=paste&height=347&id=ud9d4c948&originHeight=347&originWidth=802&originalType=binary&ratio=1&rotation=0&showTitle=false&size=50296&status=done&style=shadow&taskId=uee5e07de-572d-4675-a312-0c47b661460&title=&width=802)

梯形的面积为`(上底 + 下底) * 高 / 2`，其中上底和下底可通过`storedPermit`(x 轴)**与`slope`(斜率**)得出，高为`1`，那么上面的例子继续用梯形面积公式计算则为：

1. 请求 1：通过时间`0.0`，当前令牌数为`10-5-1`，即为绿色梯形面积`(2.6 + 3.0) * 1 / 2`=`2.8`，即下次请求需要在`2.8`秒后。
2. 请求 2：通过时间`2.8`，当前令牌数为` 9-5-1`，即为绿色梯形面积`(2.2 + 2.6) * 1 / 2`=`2.4`，即下次请求需要在`2.4`秒后。
3. 请求 3：通过时间`5.2`，当前令牌数为` 8-5-1`，即为绿色梯形面积`(1.8 + 2.2) * 1 / 2`=`2.0`，即下次请求需要在`2.0`秒后。
4. 请求 4：通过时间`7.2`，当前令牌数为` 7-5-1`，即为绿色梯形面积`(1.4 + 1.8) * 1 / 2`=`1.6`，即下次请求需要在`1.6`秒后。
5. 请求 5：通过时间`8.8`，当前令牌数为` 6-5-1`，即为绿色梯形面积`(1.0 + 1.4) * 1 / 2`=`1.2`，即下次请求需要在`1.2`秒后。
6. 请求 6：通过时间`10.`，当前令牌数为` 5-1`<触发令牌数，因此间隔=`stableInterval`，即下次请求需要在`1.0`秒后。

结果与积分计算结果相同。[《Guava》](1)源码如下：

```java
abstract class SmoothRateLimiter extends RateLimiter {

    static final class SmoothWarmingUp extends SmoothRateLimiter {
        @Override
        long storedPermitsToWaitTime(double storedPermits, double permitsToTake) {
            ...
            /**
             * length 即为上底 + 下底
             */
            double length = permitsToTime(availablePermitsAboveThreshold) + permitsToTime(availablePermitsAboveThreshold - permitsAboveThresholdToTake);
            micros = (long) (permitsAboveThresholdToTake * length / 2.0);
            ...
        }
    }
}
```

预热功能的完整图形如下：
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1875939/1639317780444-8a816a94-8acc-4e0a-92a4-2e921dff2474.png#averageHue=%23f5eaea&clientId=ue08adfb6-d545-4&from=paste&height=410&id=u462203c5&originHeight=410&originWidth=1013&originalType=binary&ratio=1&rotation=0&showTitle=false&size=89103&status=done&style=shadow&taskId=u055b760f-2d92-4382-9801-8498c368675&title=&width=1013)

以上是**Guava**中对预热限流的实现，而 Sentinel 与之大同小异，唯一的区别是，**Sentinel 将稳定间隔与冷却间隔两个值替换成了 QPS。**其余计算方式等都相同。

> Sentinel 官方源码：[WarmUpController.java](https://github.com/alibaba/Sentinel/blob/master/sentinel-core/src/main/java/com/alibaba/csp/sen~~~~tinel/slots/block/flow/controller/WarmUpController.java)

有趣的是，Guava 的开发者写有一行 **TODO**，也许`length`参数改叫梯形底之和，会更容易理解。

```java
// TODO(cpovirk): Figure out a good name for this variable.
double length = permitsToTime(availablePermitsAboveThreshold) + permitsToTime(availablePermitsAboveThreshold - permitsAboveThresholdToTake);
```

> 本文画图工具：[https://www.geogebra.org](https://www.geogebra.org/)
> 定积分计算器：[https://zh.numberempire.com/definiteintegralcalculator.php](https://zh.numberempire.com/definiteintegralcalculator.php)
