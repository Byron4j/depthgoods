package org.byron4j.concurrent.jvm;

/**<pre>
 * java字节码文件魔数：0xCAFEBABE
 *      ——每一个java字节码文件(.class)都是以相同的4字节内容开始的——十六进制的`CAFEBABE`。
 * 使用16进制编辑器软件（或者在线的）
 * 可以打开看到HelloMagicNumber.class首个4字节的16进制数为CAFEBABE
 * </pre>
 */
public class HelloMagicNumber {
    public static void main(String[] args) {
        System.out.println("Hell, HelloMagicNumber!");
    }
}
