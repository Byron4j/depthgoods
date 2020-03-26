package org.byron4j.concurrent.jvm;


/**
 * <pre>java线程栈示例
 *  1. 编译后，查看其class文件，反汇编得到可以阅读的内容
 *  2. 结合JMM模型的线程栈结构，了解其执行过程
 *  3. JMM的线程栈
 *  </pre>
   <table border=1>
      	<th>JAVA线程栈</th>
      	<tr>
      		<td>
      			<table bgcolor="#E2FFE2">
      				<th>栈帧</th>
      				<tr><td>局部变量表</td></tr>
      				<tr><td>操作数栈</td></tr>
      				<tr><td>动态链接</td></tr>
      				<tr><td>方法出口</td></tr>
      			</table>
      		</td>
      	</tr>
      	<tr>
      		<td>
      			<table bgcolor="#A0ADB9">
      				<th>栈帧</th>
      				<tr><td>局部变量表</td></tr>
      				<tr><td>操作数栈</td></tr>
      				<tr><td>动态链接</td></tr>
      				<tr><td>方法出口</td></tr>
      			</table>
      		</td>
      	</tr>
 </table>
 */
public class JavaThreadStack {

    private int cal(){
        int a = 10;
        int b = 20;
        int c = (a+b) * 10;
        return c;
    }

    public static void main(String[] args) {
        JavaThreadStack jts = new JavaThreadStack();
        jts.cal();
        System.out.println("Bye.");
    }
}
