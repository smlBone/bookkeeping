package org.mf.bookkeeping.util;

/**
 * 数字类，存不了太大的数
 * 构造参数不允许负数，正负性会额外处理
 */
public class Number implements java.io.Serializable, Comparable<Number> {
    private static final long serialVersionUID = 991341534458L;

    protected int integer;
    protected int dot1;
    protected int dot2;
    protected boolean positive;

    public static final Number ZERO = new Number();

    public Number(int integer, int dot1, int dot2, boolean positive) {
        this.set(integer, dot1, dot2, positive);
    }

    public Number(int integer, int dot1, int dot2) {
        this(integer, dot1, dot2, true);
    }

    public Number(int integer, boolean positive) {
        this(integer, 0, 0, positive);
    }

    public Number(int integer) {
        this(integer, 0, 0, true);
    }

    public Number() {
        this.setZero();
    }

    public Number(Number number) {
        if (number != null) {
            this.integer = number.integer;
            this.dot1 = number.dot1;
            this.dot2 = number.dot2;
            this.positive = number.positive;
        } else {
            this.setZero();
        }
    }

    public Number(String value) throws NumberFormatException {
        if (value == null || value.isEmpty()) {
            this.setZero();
            return;
        }
        String[] split = value.split("\\.");
        int i = Integer.parseInt(split[0]);
        this.integer = Math.abs(i);
        if (split.length >= 2) {
            this.positive = !value.startsWith("-");
            this.dot1 = split[1].charAt(0) - '0';
            if (split[1].length() > 1) {
                this.dot2 = split[1].charAt(1) - '0';
            } else {
                this.dot2 = 0;
            }
        } else {
            this.positive = i >= 0;
            this.dot1 = 0;
            this.dot2 = 0;
        }
    }

    public void set(int integer, int dot1, int dot2, boolean positive) {
        if (integer >= 0) {
            this.integer = integer;
        } else {
            throw new ArrayIndexOutOfBoundsException("整数不能为负数: " + integer);
        }
        if (isValid(dot1)) {
            this.dot1 = dot1;
        } else {
            throw new ArrayIndexOutOfBoundsException("小数点后1位必须为[0,9]: " + dot1);
        }
        if (isValid(dot2)) {
            this.dot2 = dot2;
        } else {
            throw new ArrayIndexOutOfBoundsException("小数点后2位必须为[0,9]: " + dot2);
        }
        if (integer == 0 && dot1 == 0 && dot2 == 0) {
            this.positive = true;
        } else {
            this.positive = positive;
        }
    }

    public void setZero() {
        this.integer = 0;
        this.dot1 = 0;
        this.dot2 = 0;
        this.positive = true;
    }

    public boolean isZero() {
        return this.integer == 0 && this.dot1 == 0 && this.dot2 == 0;
    }

    public int getInt() {
        return this.integer;
    }

    public Number abs() {
        this.positive = true;
        return this;
    }

    public float toFloat() {
        float num = this.integer + (this.dot1 * 0.1f) + (this.dot2 * 0.01f);
        if (!this.positive) {
            num = -num;
        }
        return num;
    }

    public Number plus(Number number) {
        this.fromCent(this.toCent() + number.toCent());
        return this;
    }

    public Number minus(Number number) {
        this.fromCent(this.toCent() - number.toCent());
        return this;
    }

    public Number reverse() {
        if (this.isZero()) {
            return this;
        }
        this.positive = !this.positive;
        return this;
    }

    private long toCent() {
        long val = this.integer * 100L + this.dot1 * 10L + this.dot2;
        return this.positive ? val : -val;
    }

    private void fromCent(long cents) {
        if (cents == 0) {
            this.positive = true;
            this.integer = 0;
            this.dot1 = 0;
            this.dot2 = 0;
            return;
        }
        long isOverFlow = cents / 100L;
        if (isOverFlow > 2147483647L || isOverFlow < -2147483648L) {
            throw new ArrayIndexOutOfBoundsException("数值溢出");
        }
        long abs = Math.abs(cents);
        this.integer = (int)(abs / 100);
        this.dot1 = (int)((abs % 100) / 10);
        this.dot2 = (int)(abs % 10);
        this.positive = cents > 0;
    }

    protected static boolean isValid(int num) {
        return num >= 0 && num < 10;
    }

    /** @noinspection NullableProblems*/
    @Override
    public String toString() {
        if (this.positive) {
            return this.integer + "." + this.dot1 + this.dot2;
        } else {
            return "-" + this.integer + "." + this.dot1 + this.dot2;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Number) {
            Number number = (Number) obj;
            return this.integer == number.integer &&
                    this.dot1 == number.dot1 &&
                    this.dot2 == number.dot2 &&
                    this.positive == number.positive;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = this.integer * 7 + this.dot1 * 31 - this.dot2 * 37;
        return this.positive ? hash : -hash;
    }

    @Override
    public int compareTo(Number o) {
        long l = this.toCent() - o.toCent();
        if (l > 0) {
            return 1;
        } else if (l == 0) {
            return 0;
        } else {
            return -1;
        }
    }
}
