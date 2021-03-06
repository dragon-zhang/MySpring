package com.dragon.springframework.test;

import com.dragon.springframework.context.context.support.GenericXmlApplicationContext;
import com.dragon.springframework.test.pojo.Type;
import com.dragon.springframework.test.service.TestService;
import com.dragon.springframework.test.service.UserService;

/**
 * @author SuccessZhang
 * @date 2020/07/01
 */
public class SimpleTest6 {
    public static void main(String[] args) throws Exception {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:applicationContext.xml");
        TestService testService = context.getBean(TestService.class);
        if (testService != null) {
            testService.queryById("1");
            System.out.println();
            testService.setType("2", new Type("vip"));
        }
        System.out.println();
        UserService userService = context.getBean(UserService.class);
        if (userService != null) {
            userService.queryById("3");
            System.out.println();
            userService.setType("4", new Type("vip2"));
        }
    }
}
