# Java魔法之java.net.URL【译】

最近发现一个很有意思的代码段：

```java
HashSet set = new HashSet();
set.add(new URL("http://google.com"));
System.out.println(set.contains(new URL("http://google.com")));
Thread.sleep(60000);
System.out.println(set.contains(new URL("http://google.com")));
```

你猜想下，第3、5行会输出什么？

结果肯定不是`true`，`true`。

好了，大都数情况下结果可能为`true, false` 。

如果你关闭网络再运行得到的结果就可能为：`true`，`true`了。

造成这种现象的原因就是`java.net.URL` 类的 `hashCode()` 和 `equals()`方法的具体实现导致的。



我们来看看它的hashCode方法：

```java
public synchronized int hashCode() {
    if (hashCode != -1)
        return hashCode;

    hashCode = handler.hashCode(this);
    return hashCode;
}


private int hashCode = -1;
```

我们可以看到hashCode是一个成员变量，只计算一次。

注意，`java.net.URL` 类是不可变的。

那么handler是啥玩意呢？是`URLStreamHandler`的一个子类，依赖于协议（file、http、ftp...）。

感兴趣的话可以查看下： `java.net.URLStreamHandler#hashCode`的逻辑。

我们看下`URL.hashCode()`的javadoc说明：

> 基于网址比较，是一个阻塞操作！



OMG!! 是一个阻塞操作呢！！



另一个刺激的是，handler会解析主机ip地址来计算哈希值。如果搞不定，则基于域名`http://google.com`计算哈希值。

如果ip是动态的，或者存在请求负载均衡，则主机的ip也是动态变化的——所以我们可能得到不同步的哈希值，这样在HashSet就是2个不同的实例的。

这样一点也不好，顺便说下，hashCode 和 equals 的性能很糟糕——因为URLStreamHandler会打开URLConnection。



**那么如何避免这种情况发生呢？**

- 使用`java.net.URI`替换`java.net.URL`; 这不是最好的选择，但是也有了确定的哈希实现。
- 不要在集合中使用`java.net.URL`，如果真要这么做，建议使用代表URL的String对象放到集合中。
- 在你计算哈希值的时候，断网！！！——开玩笑啦。。。
- 编写自己的URLStreamHandler子类实现合适的hashCode方法。



# 附录

参考：http://mishadoff.com/blog/java-magic-part-1-java-dot-net-dot-url/

