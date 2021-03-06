package com.example.design.decorator.after;

/**
 * 抽象化妆师。
 *
 * @author SuccessZhang
 * @date 2020/05/11
 */
public abstract class AbstractDecorator extends AbstractWoman {

    private AbstractWoman woman;

    public AbstractDecorator(AbstractWoman woman) {
        this.woman = woman;
    }

    @Override
    public double getFaceValue() {
        return woman.getFaceValue();
    }
}
