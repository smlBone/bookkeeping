package org.mf.bookkeeping.models;


import org.mf.bookkeeping.util.AppDate;
import org.mf.bookkeeping.util.Category;
import org.mf.bookkeeping.util.Number;

public class BillRecord implements java.io.Serializable {
    private static final long serialVersionUID = 31415002423L;

    private int id;
    private int userID;
    private AppDate date;
    private Category category;
    private String note;
    private Number amount;

    /**
     * @param date 日期
     * @param category 分类
     * @param note 备注
     * @param amount 支出/收入，不能为负数
     */
    public BillRecord(AppDate date, Category category, String note, Number amount) {
        this.date = date;
        this.category = category;
        this.note = note;
        this.amount = amount;
    }

    public BillRecord(int year, int month, int day,
                      int categoryID,
                      String note,
                      String amountString) {
        this.date = new AppDate(year, month, day);
        this.category = new Category(categoryID);
        this.note = note;
        this.amount = new Number(amountString);
    }

    public BillRecord() {}

    public boolean isIncome() {
        return this.category.income;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getNote() {
        return this.note;
    }

    public String getSimpleNote() {
        if (this.note.length() > 10) {
            return this.note.substring(0, 10) + " ...";
        }
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Number getAmount() {
        return new Number(this.amount);
    }

    public void setAmount(Number amount) {
        this.amount = amount;
    }

    public AppDate getDate() {
        return this.date;
    }

    public void setDate(AppDate date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String toCSVRow() {
        return this.amount.toString() + "," +
                this.category.toString() + "," +
                this.date.simpleString() + "," +
                this.note.replace(',', '，');
    }
}
