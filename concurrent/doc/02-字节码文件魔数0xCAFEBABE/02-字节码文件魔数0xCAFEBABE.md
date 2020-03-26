# 字节码文件魔数0xCAFEBABE

每一个java字节码文件(.class)都是以相同的4字节内容开始的——十六进制的`CAFEBABE`。

咖啡宝贝......因缺思厅。。。

```java
public class HelloMagicNumber {
    public static void main(String[] args) {
        System.out.println("Hell, HelloMagicNumber!");
    }
}
```



这个魔数用来在类装载阶段时检查该文件是不是标准的java字节码文件（当然这仅仅是第一关）。



关于这个魔数，詹姆斯高司令有这样的解释，可以查看http://radio-weblogs.com/0100490/2003/01/28.html：

>我们过去常常去一个叫圣迈克尔巷的地方吃午饭。根据当地传说，在黑暗的过去，感恩的死者在成名前曾在那里表演。这是一个非常时髦的地方，绝对是一个感恩死亡的地方。杰瑞死后，他们甚至建起了一座佛教风格的小神龛。我们过去常去那里，我们把那地方称为死亡咖啡馆。沿着这条线的某个地方，人们注意到这是一个十六进制数。我在重写一些文件格式代码，需要几个神奇的数字:一个用于持久对象文件，一个用于类。我使用CAFEDEAD作为目标文件格式，并在“CAFE”(这似乎是一个很好的主题)之后添加了4个字符的十六进制单词，我找到了BABE并决定使用它。在那个时候，除了历史的垃圾桶之外，它似乎并不十分重要或者注定要去任何地方。因此CAFEBABE成为了类文件格式，CAFEDEAD成为了持久对象格式。但是持久对象工具消失了，随之而来的是CAFEDEAD的使用——它最终被RMI所取代。



`0xCAFEBABE` 的数值表示 `3405691582` 。如果我们对其所有的数字求和得到 `43`。

只比 `42`大1；

关于42的一个传说，代表生命、宇宙和一切：*Ultimate Answer to the Life, the Universe, and Everything*；最初来自于一位英国作家的小说。

最近，MIT也把42拆解出了3个数的3次方（这是100以内的最后一位拆解破解了。。。）。

# 附录

- 参考资料： http://mishadoff.com/blog/java-magic-part-2-0xcafebabe/