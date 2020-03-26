# Java魔法之SecurityManager

介绍一些关于`SecurityManager `的使用案例。



## 介绍

我们可以通过`sun.misc.Unsafe`做一些可怕的事情（操作底层）。

而[SecurityManager](https://docs.oracle.com/javase/8/docs/api/java/lang/SecurityManager.html) 刚好相反，提供防护措施，阻止一些敏感操作（如io、网络、反射等）。

如果操作不允许，则抛出``SecurityExeption` `异常。

```java
SecurityManager manager = System.getSecurityManager();
if (manager != null) {
    manager.checkAction(action);
}
```



## 一些案例

设想一个场景：系统功能迭代升级，我们在开发一些代码后，需要提交。

当然，运行不受信任的代码是不安全的，所以我们需要确保代码提交者不会危害整个系统。

例如，sumbitter可以读取密码并修改数据库中的某些条目。更糟糕的是，它可能会填满整个文件系统、内存或消耗所有线程，并阻止其他提交者进行处理。



`SecurityManager `就是解决这些事情的。

**首先**  我们可以扩展这个类，实现自身的一些拦截策略：

```java
/**
 * 扩展SecurityManager限制一些访问操作
 */
public class MySecurityManager extends SecurityManager {

    @Override
    public void checkRead(FileDescriptor fd) {
        throw new SecurityException("File reading is not allowed");
    }

    @Override
    public void checkWrite(FileDescriptor fd) {
        throw new SecurityException("File writing is not allowed");
    }

    @Override
    public void checkConnect(String host, int port) {
        throw new SecurityException("Socket connections are not allowed");
    }
}
```

**然后**， 在运行时设置这个安全管理器。

```java
System.setSecurityManager(new MySecurityManager());
```



观测到SecurityManager的`check`前缀的方法时，JVM会做大量的检查。



虽然，安全管理器是配置对子系统的访问和防止不可信代码做坏事的有用工具，但有些操作不受安全管理器的控制。



### 内存分配

内存分配不受SecurityManager管理控制，如果需要去验证一些不受信任的代码是否可靠，可以考虑使用单的的JVM并且设置一下最大内存比如：`java -Xmx128m`。

### lib库

你可以使用`SecurityManager.checkPackageAccess`方法限制整个包的使用。



参考： http://mishadoff.com/blog/java-magic-part-5-securitymanager/