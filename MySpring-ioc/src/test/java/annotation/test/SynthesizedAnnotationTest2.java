package annotation.test;

import annotation.AliasFor;
import annotation.AnnotatedElementUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author meilin.huang
 * @version 1.0
 * @date 2019-06-06 21:11
 */
public class SynthesizedAnnotationTest2 {

    @Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Test1 {

        @AliasFor(annotation = Test1.class, attribute = "test12")
        String test1() default "test1";

        @AliasFor(annotation = Test1.class, attribute = "test1")
        String test12() default "test1";
    }

    /**
     * 只有@Test3注解，但是Test3注解上组合了@Test2注解，并将该注解的test3方法值用来覆盖Test2注解中的test2方法
     * 即更低层次声明的覆盖规则，会覆盖更高层次的属性方法值，即调用高层次的注解方法值实际显示的是低层所赋的值
     * 当然也可以将组合注解作用于更高层次，如Test3组合Test2,Test2组合Test1，然后将Test3作用于元素，通过工具类获取Test1注解覆盖的属性值
     */
    @Test1(test1 = "覆盖Test1属性中的test1方法")
    public static class Element2 {
    }

    public static void main(String[] args) {
        Test1 annotation2 = AnnotatedElementUtils.getMergedAnnotation(Element2.class, Test1.class);
        System.out.println("test1->" + annotation2.test1());
        System.out.println("test12->" + annotation2.test12());
    }
}
