package com.example.design.interpreter.after;

/**
 * @author SuccessZhang
 * @date 2020/05/28
 */
public class SubtractionInterpreter extends AbstractInterpreter {

    public SubtractionInterpreter(Interpreter left, Interpreter right) {
        super(left, right);
    }

    @Override
    public double operate(double left, double right) {
        return left - right;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
