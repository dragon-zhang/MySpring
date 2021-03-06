package com.dragon.springframework.core.annotation.test;

import com.dragon.springframework.core.annotation.AliasFor;
import com.dragon.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author SuccessZhang
 * @date 2020/06/10
 */
public class SynthesizedAnnotationTest4 {

    @Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Test1 {
        String test1() default "test1";
    }

    @Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Test2 {
        String test2() default "test2";
    }

    @Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Test1
    @Test2
    public @interface Test3 {
        /**
         * AliasFor注解用来表示要覆盖Test2注解中的test2()属性方法，
         * annotation属性声明的注解类必须存在于该注解的元注解上
         * attribute属性声明的值必须存在于Test2注解属性方法中(即Test2注解的test2方法)
         */
        @AliasFor(annotation = Test1.class, attribute = "test1")
        @AliasFor(annotation = Test2.class, attribute = "test2")
        String test3() default "test3";
    }

    /**
     * 只有@Test3注解，但是Test3注解上组合了@Test2注解，并将该注解的test3方法值用来覆盖Test2注解中的test2方法
     * 即更低层次声明的覆盖规则，会覆盖更高层次的属性方法值，即调用高层次的注解方法值实际显示的是低层所赋的值
     * 当然也可以将组合注解作用于更高层次，如Test3组合Test2,Test2组合Test1，然后将Test3作用于元素，通过工具类获取Test1注解覆盖的属性值
     */
    @Test3(test3 = "覆盖Test1属性中的test1方法")
    public static class Element {
    }

    @Test1(test1 = "覆盖Test1属性中的test1方法")
    public static class Element2 {
    }

    public static void main(String[] args) {
        Test1 annotation = AnnotationUtils.getMergedAnnotation(Element.class, Test1.class);
        // 虽然调用了Test2注解的test2方法，但是实际显示的是Test3注解中的test3属性声明的值
        // 则说明Test2的test2属性被覆盖了
        Test3 test3 = AnnotationUtils.getMergedAnnotation(Element.class, Test3.class);
        Test1 annotation2 = AnnotationUtils.getMergedAnnotation(Element2.class, Test1.class);
        System.out.println(test3.toString());
        System.out.println(annotation.hashCode());
        System.out.println(annotation.toString());
        System.out.println(annotation.equals(annotation2));
        System.out.println(annotation.annotationType());
    }
}
