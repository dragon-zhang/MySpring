package com.example.design.principle.dip.follow;

/**
 * @author SuccessZhang
 * @date 2020/05/05
 */
@SuppressWarnings("unused")
public class French implements Language {
    @Override
    public void hello() {
        System.out.println("Bonjour.");
    }
}
