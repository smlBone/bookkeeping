package org.mf.bookkeeping.database;


import android.content.ContentValues;
import android.database.Cursor;

import org.mf.bookkeeping.LoginActivity;
import org.mf.bookkeeping.models.BillRecord;
import org.mf.bookkeeping.util.AppDate;
import org.mf.bookkeeping.util.Category;
import org.mf.bookkeeping.util.Number;

import java.util.ArrayList;
import java.util.List;

public class RecordTable extends TableHelper<BillRecord> {

    protected RecordTable() {}

    @Override
    public long insert(BillRecord record) {
        if (refuse()) {
            return -1L;
        }
        open();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, LoginActivity.getLoginUser().getId());
        values.put(COLUMN_YEAR, record.getDate().getYear());
        values.put(COLUMN_MONTH, record.getDate().getMonth());
        values.put(COLUMN_DAY_OF_MONTH, record.getDate().getDayOfMonth());
        values.put(COLUMN_CATEGORY, record.getCategory().type);
        values.put(COLUMN_NOTE, record.getNote());
        values.put(COLUMN_AMOUNT, record.getAmount().toString());
        long id = super.db.insert(TABLE_RECORDS, null, values);
        close();
        return id;
    }

    @Override
    public List<BillRecord> getAll() {
        open();
        List<BillRecord> records = new ArrayList<>();

        Cursor cursor = super.db.query(
                TABLE_RECORDS,
                null,
                COLUMN_USER_ID + " = ?",
                valueOfID(LoginActivity.getLoginUser().getId()),
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            records.add(buildRecord(cursor));
        }

        cursor.close();
        close();
        return records;
    }

    @Override
    public BillRecord getByID(int id) {
        open();
        BillRecord record = null;

        Cursor cursor = super.db.query(
                TABLE_RECORDS,
                null,
                COLUMN_ID + " = ? AND " + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(id), String.valueOf(LoginActivity.getLoginUser().getId())},
                null, null, null
        );
        if (cursor.moveToFirst()) {
            record = buildRecord(cursor);
        }
        cursor.close();
        close();
        return record;
    }

    @Override
    public int update(int id, BillRecord record) {
        open();
        ContentValues values = new ContentValues();
        values.put(COLUMN_YEAR, record.getDate().getYear());
        values.put(COLUMN_MONTH, record.getDate().getMonth());
        values.put(COLUMN_DAY_OF_MONTH, record.getDate().getDayOfMonth());
        values.put(COLUMN_CATEGORY, record.getCategory().type);
        values.put(COLUMN_NOTE, record.getNote());
        values.put(COLUMN_AMOUNT, record.getAmount().toString());

        int rowsAffected = super.db.update(
                TABLE_RECORDS,
                values,
                COLUMN_ID + " = ? AND " + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(id), String.valueOf(LoginActivity.getLoginUser().getId())}
        );
        close();
        return rowsAffected;
    }

    @Override
    public int delete(int id) {
        open();
        int rowsAffected = super.db.delete(
                TABLE_RECORDS,
                COLUMN_ID + " = ?",
                valueOfID(id)
        );
        close();
        return rowsAffected;
    }

    public List<BillRecord> getByMonth(int year, int month) {
        open();
        List<BillRecord> records = new ArrayList<>();

        Cursor cursor = super.db.query(
                TABLE_RECORDS,
                null,
                COLUMN_YEAR + " = ? AND " + COLUMN_MONTH + " = ? AND " + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(year),
                        String.valueOf(month),
                        String.valueOf(LoginActivity.getLoginUser().getId())},
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            records.add(buildRecord(cursor));
        }

        cursor.close();
        close();
        return records;
    }

    public List<BillRecord> getByNote(String keyword) {
        open();
        List<BillRecord> records = new ArrayList<>();

        Cursor cursor = super.db.query(
                TABLE_RECORDS,
                null,
                COLUMN_NOTE + " LIKE ? AND " + COLUMN_USER_ID + " = ?",
                new String[]{"%" + keyword + "%",
                        String.valueOf(LoginActivity.getLoginUser().getId())},
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            records.add(buildRecord(cursor));
        }

        cursor.close();
        close();
        return records;
    }

    public List<BillRecord> getByCategory(int categoryType) {
        open();
        List<BillRecord> records = new ArrayList<>();

        Cursor cursor = super.db.query(
                TABLE_RECORDS,
                null,
                COLUMN_CATEGORY + " = ? AND " + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(categoryType),
                        String.valueOf(LoginActivity.getLoginUser().getId())},
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            records.add(buildRecord(cursor));
        }

        cursor.close();
        close();
        return records;
    }

    public long insert(int year, int month, int day,
                             int categoryID,
                             String note,
                             String amountString) {
        if (refuse()) {
            return -1L;
        }
        open();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, LoginActivity.getLoginUser().getId());
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_MONTH, month);
        values.put(COLUMN_DAY_OF_MONTH, day);
        values.put(COLUMN_CATEGORY, categoryID);
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_AMOUNT, amountString);
        long id = super.db.insert(TABLE_RECORDS, null, values);
        close();
        return id;
    }

    public int update(int id, int year, int month, int day,
                             int categoryID,
                             String note,
                             String amountString) {
        open();
        ContentValues values = new ContentValues();
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_MONTH, month);
        values.put(COLUMN_DAY_OF_MONTH, day);
        values.put(COLUMN_CATEGORY, categoryID);
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_AMOUNT, amountString);

        int rowsAffected = super.db.update(
                TABLE_RECORDS,
                values,
                COLUMN_ID + " = ? AND " + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(id), String.valueOf(LoginActivity.getLoginUser().getId())}
        );
        close();
        return rowsAffected;
    }

    private static BillRecord buildRecord(Cursor cursor) {
        BillRecord record = new BillRecord();
        record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        record.setUserID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
        record.setDate(new AppDate(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YEAR)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MONTH)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DAY_OF_MONTH))
        ));
        record.setCategory(new Category(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))));
        record.setNote(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)));
        record.setAmount(new Number(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))));
        return record;
    }

    private static boolean refuse() {
        return LoginActivity.getLoginUser().getId() == -1;
    }
}
