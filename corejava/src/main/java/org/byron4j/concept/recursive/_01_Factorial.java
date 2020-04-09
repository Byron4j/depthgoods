package org.byron4j.concept.recursive;

/**
 * 阶乘示例---理解递归思想
 */
public class _01_Factorial {

    private static int factorial(int i){
        if( i <= 1 ){
            return 1;
        }else{
            return i * factorial(i-1);
        }
    }

    private static int factorial2(int i, int result){
        if( i <= 1 ){
            return result;
        }else{
            return factorial2(i-1, i*result);
        }
    }

    public static void main(String[] args) {
        System.out.println(_01_Factorial.factorial(0));
        System.out.println(_01_Factorial.factorial(1));
        System.out.println(_01_Factorial.factorial(2));
        System.out.println(_01_Factorial.factorial(3));
        System.out.println(_01_Factorial.factorial(4));
    }
}
