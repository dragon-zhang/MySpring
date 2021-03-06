package com.example.design.factory.after.af;

import com.example.design.factory.after.af.iphone.IPhone;
import com.example.design.factory.after.af.mac.MacBook;

/**
 * @author SuccessZhang
 * @date 2020/05/08
 */
public class Client {
    public static void main(String[] args) {
        ChineseAppleFactory chineseFactory = new ChineseAppleFactory();
        IPhone iPhone = chineseFactory.createIPhone();
        iPhone.open();
        MacBook macBook = chineseFactory.createMacBook();
        macBook.open();
        System.out.println(iPhone.getBrand());
        System.out.println(macBook.getBrand());

        AmericanAppleFactory americanFactory = new AmericanAppleFactory();
        iPhone = americanFactory.createIPhone();
        iPhone.open();
        macBook = americanFactory.createMacBook();
        macBook.open();
        System.out.println(iPhone.getBrand());
        System.out.println(macBook.getBrand());
    }
}
