package com.example.design.composite.after.safe;

/**
 * @author SuccessZhang
 * @date 2020/05/27
 */
public class Leaf3 extends AbstractNode2 {

    public Leaf3(int level) {
        super(level);
    }

    @Override
    public void showLevel() {
        System.out.print(super.level);
        for (int i = 0; i < super.level; i++) {
            System.out.print("-");
        }
        System.out.println("叶子");
    }
}
