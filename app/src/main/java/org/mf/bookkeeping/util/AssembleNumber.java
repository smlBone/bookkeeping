package org.mf.bookkeeping.util;


public class AssembleNumber extends Number {
    private static final int MAX = 99_999_999;

    private int pointer;

    public AssembleNumber(int integer, int dot1, int dot2, boolean positive) {
        super(integer, dot1, dot2, positive);
    }

    public AssembleNumber(int integer, int dot1, int dot2) {
        super(integer, dot1, dot2);
    }

    public AssembleNumber(int integer, boolean positive) {
        super(integer, positive);
    }

    public AssembleNumber(Number number) {
        super(number);
    }

    public AssembleNumber(int integer) {
        super(integer);
        this.pointer = -1;
    }

    public AssembleNumber(String value) {
        super(value);
        this.updatePointer();
    }

    public AssembleNumber() {
        super();
        this.pointer = -1;
    }

    @Override
    public Number plus(Number number) {
        Number plus = super.plus(number);
        this.updatePointer();
        return plus;
    }

    @Override
    public Number minus(Number number) {
        Number minus = super.minus(number);
        this.updatePointer();
        return minus;
    }

    @Override
    public void set(int integer, int dot1, int dot2, boolean positive) {
        super.set(integer, dot1, dot2, positive);
        this.updatePointer();
    }

    @Override
    public void setZero() {
        super.setZero();
        this.pointer = -1;
    }

    /** @noinspection NullableProblems*/
    @Override
    public String toString() {
        switch (this.pointer) {
            case -1: {
                return String.valueOf(this.integer);
            }
            case 0: {
                return this.integer + ".";
            }
            case 1: {
                return this.integer + "." + this.dot1;
            }
            case 2: {
                return this.integer + "." + this.dot1 + this.dot2;
            }
        }
        throw new IllegalStateException("不该出现的错误，指针异常指向: " + this.pointer);
    }

    public boolean completeZero() {
        return super.isZero() && this.pointer == -1;
    }

    public boolean disassemble() {
        switch (this.pointer) {
            case -1: {
                if (this.integer == 0) {
                    return false;
                }
                this.integer = this.integer / 10;
                if (this.integer == 0) {
                    this.positive = true;
                }
                return true;
            }
            case 0: {
                this.pointer = -1;
                return true;
            }
            case 1: {
                this.dot1 = 0;
                this.pointer = 0;
                return true;
            }
            case 2: {
                this.dot2 = 0;
                this.pointer = 1;
                return true;
            }
        }
        throw new IllegalStateException("不该出现的错误，指针异常指向: " + this.pointer);
    }

    public boolean assemble(int num) {
        if (isValid(num)) {
            switch (this.pointer) {
                case -1: {
                    if (this.integer > MAX) {
                        return false;
                    }
                    this.integer = Math.min(this.integer * 10 + num, MAX);
                    break;
                }
                case 0: {
                    this.dot1 = num;
                    this.pointer = 1;
                    break;
                }
                case 1: {
                    this.dot2 = num;
                    this.pointer = 2;
                    break;
                }
                case 2: {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean assembleDot() {
        if (this.pointer == -1) {
            this.pointer = 0;
            return true;
        }
        return false;
    }

    private void updatePointer() {
        if (this.dot2 != 0) {
            this.pointer = 2;
        } else if (this.dot1 != 0) {
            this.pointer = 1;
        } else {
            this.pointer = -1;
        }
    }
}
