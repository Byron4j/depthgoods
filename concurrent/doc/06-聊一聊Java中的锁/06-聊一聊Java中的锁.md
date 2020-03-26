# 聊一聊Java中的锁

当进行多线程编程的时候，可能争抢同一资源而引发不安全的问题。

多个线程访问方法、某个实例对象出现问题——线程安全问题。

如果一次仅仅允许一个线程操作使用就不会发生问题，对于这种资源称之为临界资源。

## 线程安全

线程安全是多线程场景下才会产生的问题，线程安全可以理解为某个方法或者实例对象在多线程环境中使用而不会出现问题。

那么怎么解决线程安全问题呢？

## 线程安全解决方式

Java提供了这么一些方式：

- 同步字`Synchronization`
- 并发包`java.util.concurrent.atomic`里面的原子类，例如AtomicInteger、AtomicBoolean等
- 并发包`java.util.concurrent.locks`里面的锁，如ReentrantLock、ReadWriteLock
- 线程安全的集合类：`ConcurrentHashMap`、`ConcurrentLinkedQueue`等
- `volatile` 关键字，可以保证线程可见性、有序性（但是不保证原子性）

## synchronize关键字

synchronize关键字是底层实现，通过`monitorenter`、`monitorexit`指令实现。可以通过字节码查看到。

synchronize是同步锁，也是可重入锁。

synchronize的同步锁，是互斥锁、悲观锁，其他线程只能阻塞等待来获得锁。

synchronize的用法：

- 修饰实例方法
- 修饰类（类.class）对象、静态方法
- 锁定对象

## 原子类

通过原子类如AtomicInteger也能实现线程安全，底层是通过操作系统的CAS原子操作+自旋来实现的。

### CAS

CAS操作是乐观锁的实现，当多个线程尝试使用 CAS 同时更新同一个变量时，只有其中一个线程能更新变量的值，而其它线程都失败，失败的线程并不会被挂起，而是被告知这次竞争中失败，并可以再次尝试。

CAS 操作包含三个操作数 —— 内存位置（V）、期望的原值（A）和要修改的目标新值(B)。如果内存中位置的值和期望原值A一样，则更新为B；否则不操作。

CAS是基于比较更新的操作，和数据库实现的乐观锁很类似。

数据库的乐观锁一般是增加一个冗余字段（通常是行记录的version），先查询到version的原值v，更新时带上version条件。

```mysql
update 表名 set 字段=值, version=version+1 where version=v
```

温馨提示：如果系统并发很高，数据库乐观锁可能导致大量事务回滚，很多线程白干活....

### ABA问题

CAS存在ABA问题：CAS是先拿到原值，在去和内存中指定位置的现值比较，在这期间可能发生过变化，系统状态可能发生了改变。

举个栗子：一个线程拿到的是A，另一个线程也拿到了A并做了某些业务处理改为了B，最后又改回了A，但是对于第一个线程来说，他通过CAS匹配是成立的，他不知道已经发生过系统状态变更了，可能会引发某些问题。

洗钱案例：不法分子盗用你的账号将1000万转走，把赃款1000万打给你，对你而言还是账内余额1000万，没有变化，但是发生了洗钱行为....

而上面提到的数据库的乐观锁不会出现ABA问题，因为version的值是不断递增的。

### ABA问题解决

可以通过如：原子引用类解决，如 `AtomicStampedReference`。

AtomicStampedReference 持有Integer的时间戳，可以根据时间戳比较判断是否发生过改变——是不是和数据库乐观锁实现方式类似了。

## Lock

和synchronize是由JVM控制的不同，并发包里面的锁Lock，是从编程角度来解决临界资源问题。

一般使用`ReentrantLock`较多，多个线程使用同一个`ReentrantLock`示例协调。可以手动控制加锁、解锁，常规使用方式如下：

```java
class X {
   private final ReentrantLock lock = new ReentrantLock();
   // ...

   public void m() {
     lock.lock();  // 获得锁
     try {
       // ... 
     } finally {
       lock.unlock();  // 解锁
     }
   }
 }
```

在 **JDK 1.5** 中，`synchronize` 是性能低效的。因为这是一个重量级操作，需要调用操作接口，导致有可能加锁消耗的系统时间比加锁以外的操作还多。

但是到了 **JDK 1.6**，发生了变化。`synchronize` 在语义上很清晰，可以进行很多优化，有适应自旋，锁消除，锁粗化，轻量级锁，偏向锁等等。导致在 **JDK 1.6** 上 `synchronize` 的性能并不比 `Lock` 差。

## volatile

volatile关键字可以实现可见性（线程数据从主内存获取）、有序性（禁止指令重排）。