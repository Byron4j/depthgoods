# Unsafe-java的魔法类

## Unsafe的介绍

> Unsafe的源代码：http://www.docjar.com/html/api/sun/misc/Unsafe.java.html



**Unsafe**类全限定名为`sun.misc.Unsafe`，顾名思义不是安全的。

一般而言，编写底层代码或者影响JVM是很难实现的，当然你可以使用JNI来达到目的，JNI需要和C打交道。

在java平台通过`sun.misc.Unsafe`的API，也可以进行底层编码，比如操作目标对象的地址，直接修改属性字段所在的地址的值......当然使用这个类比较危险，所以慎用。

**Unsafe**在原子类中大量使用到了，可以通过`compareAndSwapXX`方法调用底层操作糸统的原子操作指令，来进行原子操作。



## Unsafe对象的获取

考虑创建Unsafe对象之前，先来看下这个类的源代码：

```java
public final class Unsafe {
    private static final Unsafe theUnsafe;
    private Unsafe() {
    }
    @CallerSensitive
    public static Unsafe getUnsafe() {
        Class var0 = Reflection.getCallerClass();
        if (!VM.isSystemDomainLoader(var0.getClassLoader())) {
            throw new SecurityException("Unsafe");
        } else {
            return theUnsafe;
        }
    }
    static {
        theUnsafe = new Unsafe();
    }
```

Unsafe 类有这么一些特点：

1. final修饰的
2. 有一个静态变量也是Unsafe类型的实例`theUnsafe`
3. 构造器私有
4. 静态方法getUnsafe()可以获得一个Unsafe实例对象theUnsafe，但是检查了类加载器（只有JVM的引导加载器才允许，否则抛出`SecurityException`异常）
5. 静态代码块实例化了theUnsafe变量



那么我们要创建Unsafe 的实例对象，怎么做呢？

针对上面的特点针对性的解决。以下是两种方案。

### 追加类到引导类加载器BoostrapClassloader

我们可以利用Unsafe类的静态方法getUnsafe()，但是这个方法会检查类加载器是否为BoostrapClassloader。

所以我们可以将当前类所在路径追加到BoostrapClassloader的扫描路径下去。

> 这里扩展一点: 得到BoostrapClassloader加载的路径：
>
> > ```java
> > String property = System.getProperty("sun.boot.class.path");
> > for (String s : property.split(";")) {
> >     System.out.println(s);
> > }
> > ```
>
> 输出：
>
> C:\Program Files\Java\jdk1.8.0_211\jre\lib\resources.jar
> C:\Program Files\Java\jdk1.8.0_211\jre\lib\rt.jar
> C:\Program Files\Java\jdk1.8.0_211\jre\lib\sunrsasign.jar
> C:\Program Files\Java\jdk1.8.0_211\jre\lib\jsse.jar
> C:\Program Files\Java\jdk1.8.0_211\jre\lib\jce.jar
> C:\Program Files\Java\jdk1.8.0_211\jre\lib\charsets.jar
> C:\Program Files\Java\jdk1.8.0_211\jre\lib\jfr.jar
> C:\Program Files\Java\jdk1.8.0_211\jre\classes
>
> 

>扩展：得到Extend类加载器加载的路径：
>
>```java
>String property2 = System.getProperty("java.ext.dirs");
>for (String s : property2.split(";")) {
>    System.out.println(s);
>}
>```
>
>输出：
>
>C:\Program Files\Java\jdk1.8.0_211\jre\lib\ext
>C:\Windows\Sun\Java\lib\ext

#### Xbootclasspath扩展

Java 命令行提供了如何扩展BoostrapClassloader的简单方法.

- `-Xbootclasspath: 新的jar`    完全替换jdk的的Java class 搜索路径，不建议；
- `-Xbootclasspath/a:追加的jar` 追加在jdk的java class搜索路径后面，很实用（多个jar在unix冒号分隔，windows分号分隔）；
- `-Xbootclasspath/p:放在前面的jar` 放jdk的java class搜索路径前面，不建议；以免引起不必要的冲突。



所以我们可以通过在IDEA中设置jvm运行时参数（添加VM options）：

```
-Xbootclasspath/a:D:\framework\concurrent\target\concurrent-1.0-SNAPSHOT.jar 
```

再run即可。**注意这里要打包成jar，才能追加**

```java
/*
运行时添加 VM options
-Xbootclasspath/a:D:\framework\concurrent\target\concurrent-1.0-SNAPSHOT.jar
*/
Unsafe unsafe0 = Unsafe.getUnsafe();
System.out.println("-Xbootclasspath/a:添加jar包：" + unsafe0);  // 可以获得Unsafe实例对象
```

![1585063368237](assets\1585063368237.png)



这种方式太难了.....所以我们使用下面的第二种方式。



### 反射Field获取Unsafe实例对象【推荐用法】

从Unsafe的特点来看，我们得知，其内部存在一个theUnsafe变量就是Unsafe的一个实例：

```java
private static final Unsafe theUnsafe;
```

既然构造器不能用，我们就使用反射直接获取该字段的值：

```java
// 所以，选择反射获取
// 因为内部存在一个单例实例：Unsafe theUnsafe;
Class clazz = Unsafe.class;
Field field = clazz.getDeclaredField("theUnsafe");
field.setAccessible(true);
Unsafe unsafe = (Unsafe)field.get(null);   // 得到其内部的字段theUnsafe
```



## Unsafe的API

Unsafe提供了大量的API（100多个方法），来操作底层。

主要分为这么几大类：

### Info获得信息

返回一些底层的内存信息。

- addressSize：本地指针大小，值一般为4或者8；存储在本地块的原始类型由他们的内容信息决定。
- pageSize：返回内存页大小，字节为单位，值为2的n次方

```java
// info
System.out.println(unsafe.pageSize());  // 4096
System.out.println(unsafe.addressSize());  // 8
```



### Objects操作对象

提供操作对象以及它的字段的方法。

- allocateInstance：分配一个实例对象，但是不会调用任何的构造器（变量值没有初始化）

  - ```java
    public native Object allocateInstance(Class cls) 
    ```

示例，很有意思：

```java
@Data
public class UnsafeDemo {
    private String name = "zs";
    private int age = 18;
}

// 运行以下代码的结果：
UnsafeDemo unsafeDemo3 = (UnsafeDemo)unsafe.allocateInstance(UnsafeDemo.class);
System.out.println(unsafeDemo3);  // UnsafeDemo(name=null, age=0)
UnsafeDemo unsafeDemo1 = new UnsafeDemo();
System.out.println(unsafeDemo1);   // UnsafeDemo(name=zs, age=18)
```



- objectFieldOffset： 获得某个类的某个字段在内存中的偏移量

  - ```java
    public native long objectFieldOffset(Field f);
    ```

示例：获得UnsafeDemo类的age字段在内存中的偏移量

```java
long ageOffset =
                unsafe.objectFieldOffset(UnsafeDemo.class.getDeclaredField("age"));
System.out.println(ageOffset); // 12
```



### Classes操作类对象

提供操作类对象、静态字段的方法。

- staticFieldOffset：获得静态字段的偏移量

  - ```java
    public native long staticFieldOffset(Field f);
    ```

示例：

```java
@Data
public class UnsafeDemo {
    private String name = "zs";
    private int age = 18;
    private static int status = 1;
}

// staticFieldOffset：获得静态字段的偏移量
Field status = UnsafeDemo.class.getDeclaredField("status");
long statusOffset = unsafe.staticFieldOffset(status);
System.out.println(statusOffset);  // 104
```



- defineClass ： 告知JVM定义一个类，但是不要做安全检查；默认情况下，类加载器和保护域来自调用者的Class。

  - ```java
    public native Class defineClass(String name, byte[] b, int off, int len,
    									ClassLoader loader,
    									ProtectionDomain protectionDomain);
    ```

- defineAnonymousClass：定义一个不被类加载器、系统感知的类。

  - ```java
    /**
    * @params hostClass 链接的上下文，访问控制，类加载器
    * @params data      字节码文件的字节数组形式
    * @params cpPatches 如果存在非空数据，则替换data中的
      829        */
    */
    public native Class defineAnonymousClass(Class hostClass, byte[] data, Object[] cpPatches);
    ```

- ensureClassInitialized： 确保给定的类已经初始化，这通常需要结合获取类的静态字段库。

  - ```java
    public native void ensureClassInitialized(Class c);
    ```



### Arrays操作数组

对数组的封装。

- arrayBaseOffset：数组对象的首元素的偏移量

  - ```java
    public native int arrayBaseOffset(Class arrayClass)
    ```

示例：

```java
int arrayBaseOffset =
                unsafe.arrayBaseOffset(new byte[]{1, 2, 3}.getClass());
System.out.println(arrayBaseOffset); // 16
```



- arrayIndexScale：寻址因子

  - ```java
    public native int arrayIndexScale(Class arrayClass);
    ```



### Synchronization操作同步字

操作同步的一些底层封装。

- monitorEnter：锁定对象， 必须通过`monitorExit`释放锁。

  - ```java
    public native void monitorEnter(Object o);
    ```

- tryMonitorEnter ： 尝试获得锁

  - ```java
    public native boolean tryMonitorEnter(Object o);
    ```

- monitorExit：释放锁

  - ```java
    public native void monitorExit(Object o);
    ```



- **compareAndSwapInt**：**CAS也是很实用的一个方法，AtomicInteger、AtomicBoolean等原子类都是通过Unsafe的CAP方法实现原子操作的。**  

  - ```java
    /**
    原子操作：修改java变量的值为x；
    如果对象o的偏移量offset（其实就是该对象的某个字段）表示的变量的值，目前是期望值expected，则将其修改为x，返回true；
    如果目前是期望值不是expected，则不操作，返回false。
    */
    public final native boolean compareAndSwapInt(Object o, long offset,
                                                int expected,
                                                int x);
    ```

示例AtomicInteger#incrementAndGet方法的实现逻辑：

java.util.concurrent.atomic.AtomicInteger#incrementAndGet

```java
public final int incrementAndGet() {
    return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
}
```

sun.misc.Unsafe#getAndAddInt

```java
public final int getAndAddInt(Object var1, long var2, int var4) {
    int var5;
    do {
        var5 = this.getIntVolatile(var1, var2);
        // 自旋+CAS
    } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));

    return var5;
}
```

- putIntVolatile： `putInt`的Volatile版本

  - ```java
    public native void    putIntVolatile(Object o, long offset, int x);
    ```

  - 

- putOrderedInt： `putIntVolatile`的有序\惰性版本

  - ```java
    public native void    putOrderedInt(Object o, long offset, int x);
    ```

### Memory操作内存

直接访问内存的方法。

- allocateMemory：分配内存空间，并返回偏移量

  - ```java
    public native long allocateMemory(long bytes);
    ```

- copyMemory：复制内存空间

  - ```java
    // @since 1.7
    public native void copyMemory(Object srcBase, long srcOffset,
        Object destBase, long destOffset,
        long bytes);
    
    public void copyMemory(long srcAddress, long destAddress, long bytes) {
    	copyMemory(null, srcAddress, null, destAddress, bytes);
    }
    ```

- freeMemory：释放内存空间

  - ```java
    public native void freeMemory(long address);
    ```

- getAddress: 获得内存地址，无符号整数long

  - ```java
    public native long getAddress(long address);
    ```

- getInt：获得指定偏移量的变量值

  - ```java
    public native int getInt(Object o, long offset);
    ```

- **putInt**：可以修改变量的值

  - ```java
    /**
    参数一：对象
    参数二：字段偏移量
    参数三：要设置的值
    */
    public native void putInt(Object o, long offset, int x);
    ```

示例:

```java
//通过魔法类修改对象实例的属性值
UnsafeDemo unsafeDemo = new UnsafeDemo();
long ageOffset =
                unsafe.objectFieldOffset(UnsafeDemo.class.getDeclaredField("age"));
unsafe.putInt(unsafeDemo, ageOffset, 30);  // 将字段age的值改为30
```



## Unsafe实现CAS

### 案例:AtomicInteger的原子操作

示例AtomicInteger#incrementAndGet方法的实现逻辑：

java.util.concurrent.atomic.AtomicInteger#incrementAndGet

```java
public final int incrementAndGet() {
    return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
}
```

sun.misc.Unsafe#getAndAddInt

```java
public final int getAndAddInt(Object var1, long var2, int var4) {
    int var5;
    do {
        var5 = this.getIntVolatile(var1, var2);
        // 自旋+CAS
    } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));

    return var5;
}
```

这里分享的是常见的一些API，Unsafe还有很多其他类似的方法，不再一一列举。