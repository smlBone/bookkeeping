package org.mf.bookkeeping.util;

import android.util.Log;

public class AmountBuilder {
    private static final String TAG = "Number Assembling Error";

    private final AssembleNumber left = new AssembleNumber();
    private final AssembleNumber right = new AssembleNumber();
    private State pointer = State.LEFT;
    private Boolean symbol = null;

    public boolean addDigit(int num) {
        if (num < 0 || num > 9) {
            Log.e(TAG, "添加的数字必须在[0, 9]之间: " + num);
            return false;
        }
        if (this.pointer == State.LEFT) {
            return this.left.assemble(num);
        } else if (this.pointer == State.RIGHT) {
            return this.right.assemble(num);
        } else {
            this.pointer = State.RIGHT;
            return this.right.assemble(num);
        }
    }

    public boolean addDot() {
        if (this.pointer == State.LEFT) {
            return this.left.assembleDot();
        } else if (this.pointer == State.RIGHT) {
            return this.right.assembleDot();
        }  else {
            this.pointer = State.RIGHT;
            return this.right.assembleDot();
        }
    }

    public boolean addSymbol(boolean plus) {
        if (this.pointer == State.RIGHT) {
            if (this.symbol != null) {
                if (this.symbol) {
                    this.left.plus(this.right);
                } else {
                    this.left.minus(this.right);
                }
            } else {
                Log.e(TAG, "不该出现的错误，symbol 为空");
                return false;
            }
            this.right.setZero();
        } else if (this.isSymbol()) {
            if (this.symbol != null && this.symbol == plus) {
                return false;
            }
        }
        this.pointer = plus ? State.PLUS : State.MINUS;
        this.symbol = plus;
        return true;
    }

    public Number getSum() {
        Number retVal = new Number(this.left);
        if (this.symbol != null) {
            if (this.symbol) {
                retVal.plus(this.right);
            } else {
                retVal.minus(this.right);
            }
        }
        return retVal;
    }

    public boolean backward() {
        switch (this.pointer) {
            case LEFT: {
                return this.left.disassemble();
            }
            case RIGHT: {
                if (this.right.completeZero()) {
                    this.symbol = null;
                    this.pointer = State.LEFT;
                    return true;
                } else {
                    return this.right.disassemble();
                }
            }
            case PLUS:
            case MINUS: {
                this.symbol = null;
                this.pointer = State.LEFT;
                return true;
            }
        }
        Log.e(TAG, "不该出现的错误，退格失败");
        return false;
    }

    public void clear() {
        this.pointer = State.LEFT;
        this.left.setZero();
        this.right.setZero();
        this.symbol = null;
    }

    private boolean isSymbol() {
        return this.pointer == State.PLUS || this.pointer == State.MINUS;
    }

    public String makeString() {
        if (this.symbol == null) {
            return this.left.toString();
        }
        if (this.symbol) {
            return this.left + " + " + this.right;
        } else {
            return this.left + " - " + this.right;
        }
    }

    public void setBase(Number base) {
        this.left.set(base.integer, base.dot1, base.dot2, base.positive);
    }

    private enum State {
        LEFT,
        RIGHT,
        PLUS,
        MINUS
    }
}
