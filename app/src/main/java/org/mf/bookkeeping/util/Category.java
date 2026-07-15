package org.mf.bookkeeping.util;

import androidx.annotation.NonNull;

public class Category implements java.io.Serializable {
    private static final long serialVersionUID = 6874435875434L;

    public final int type;
    public final boolean income;

    public static final int MEAL = 0;
    public static final int SNACK = 1;
    public static final int CLOTHING = 2;
    public static final int TRANSPORTATION = 3;
    public static final int DAILY_NECESSARIES = 4;
    public static final int WATER_ELE_GAS = 5;
    public static final int INTERNET = 6;
    public static final int ENTERTAINMENT = 7;
    public static final int MEDICAL_TREATMENT = 8;
    public static final int OTHER_OUT = 9;

    public static final int SALARY = -1;
    public static final int LIVING_EXPENSES = -2;
    public static final int RED_ENVELOPES = -3;
    public static final int PERK = -4;
    public static final int OTHER_IN = -5;

    public Category(int type) {
        if (type < -5 || type > 9) {
            throw new ArrayIndexOutOfBoundsException("无效的类型: " + type);
        }
        this.type = type;
        this.income = type < 0;
    }

    public Category(String from) {
        this(fromString(from));
    }

    public Category() {
        this(0);
    }

    @NonNull
    @Override
    public String toString() {
        switch (this.type) {
            case MEAL: return "三餐";
            case SNACK: return "零食";
            case CLOTHING: return "衣物";
            case TRANSPORTATION: return "交通";
            case DAILY_NECESSARIES: return "日用品";
            case WATER_ELE_GAS: return "水电煤";
            case INTERNET: return "网费话费";
            case ENTERTAINMENT: return "娱乐";
            case MEDICAL_TREATMENT: return "医疗";
            case OTHER_OUT: return "其他支出";

            case SALARY: return "工资";
            case LIVING_EXPENSES: return "生活费";
            case RED_ENVELOPES: return "红包";
            case PERK: return "外快";
            case OTHER_IN: return "其他收入";
        }
        return "{error: 未知类型}";
    }

    private static int fromString(String from) {
        switch (from) {
            case "三餐": return MEAL;
            case "零食": return SNACK;
            case "衣物": return CLOTHING;
            case "交通": return TRANSPORTATION;
            case "日用品": return DAILY_NECESSARIES;
            case "水电煤": return WATER_ELE_GAS;
            case "网费话费": return INTERNET;
            case "娱乐": return ENTERTAINMENT;
            case "医疗": return MEDICAL_TREATMENT;
            case "其他支出": return OTHER_OUT;

            case "工资": return SALARY;
            case "生活费": return LIVING_EXPENSES;
            case "红包": return RED_ENVELOPES;
            case "外快": return PERK;
            case "其他收入": return OTHER_IN;
        }
        throw new ArrayIndexOutOfBoundsException("无效的类型: " + from);
    }
}
