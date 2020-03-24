package org.byron4j.concurrent.magic;

import lombok.Data;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 魔法类Unsafe
 */
@Data
public class UnsafeDemo {
    private String name = "zs";
    private int age = 18;
    private static int status = 1;
    // 进入魔法类查看，构造器时私有的，提供的静态方法只有JVM引导类加载器才能加载该类
    /*
    public static Unsafe getUnsafe() {
        Class var0 = Reflection.getCallerClass();
        if (!VM.isSystemDomainLoader(var0.getClassLoader())) {
            throw new SecurityException("Unsafe");
        } else {
            return theUnsafe;
        }
    }
     */

    public static void main(String[] args) throws Exception{

        /*
        运行时添加 VM options
        -Xbootclasspath/a:D:\framework\concurrent\target\concurrent-1.0-SNAPSHOT.jar
         */
        // 太难了...少用
//        Unsafe unsafe0 = Unsafe.getUnsafe();
//        System.out.println("-Xbootclasspath/a:添加jar包：" + unsafe0);


        // 所以，选择反射获取
        // 因为内部存在一个单例实例：Unsafe theUnsafe;
        Class clazz = Unsafe.class;
        Field field = clazz.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe)field.get(null);

        // API
        // 1. 通过魔法类修改对象实例的属性值
        UnsafeDemo unsafeDemo = new UnsafeDemo();
        System.out.println("unsafeDemo:" +unsafeDemo);
        UnsafeDemo unsafeDemo2 = new UnsafeDemo();
        System.out.println("unsafeDemo2:" + unsafeDemo2);
        long ageOffset =
                unsafe.objectFieldOffset(UnsafeDemo.class.getDeclaredField("age"));
        //System.out.println(ageOffset); // 12
        unsafe.putInt(unsafeDemo, ageOffset, 30);
        System.out.println("unsafeDemo:" +unsafeDemo);  // unsafeDemo被处理，age=30，被改变了内存中的值
        System.out.println("unsafeDemo2:" +unsafeDemo2); // unsafeDemo2没有处理，age还是18



        // info
        System.out.println(unsafe.pageSize());  // 4096
        System.out.println(unsafe.addressSize());  // 8


        // objects
        UnsafeDemo unsafeDemo3 = (UnsafeDemo)unsafe.allocateInstance(UnsafeDemo.class);
        System.out.println(unsafeDemo3);  // UnsafeDemo(name=null, age=0)
        UnsafeDemo unsafeDemo1 = new UnsafeDemo();
        System.out.println(unsafeDemo1);   // UnsafeDemo(name=zs, age=18)

        // classes
        Field status = UnsafeDemo.class.getDeclaredField("status");
        long statusOffset = unsafe.staticFieldOffset(status);
        System.out.println(statusOffset);  // 104

        // array
        int arrayBaseOffset =
                unsafe.arrayBaseOffset(new byte[]{1, 2, 3}.getClass());
        System.out.println(arrayBaseOffset); // 16

    }
}
