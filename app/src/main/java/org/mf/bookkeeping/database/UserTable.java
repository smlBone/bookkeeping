package org.mf.bookkeeping.database;


import android.content.ContentValues;
import android.database.Cursor;

import org.mf.bookkeeping.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserTable extends TableHelper<User> {

    protected UserTable() {}

    @Override
    public long insert(User user) {
        open();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_ACCOUNT, user.getAccount());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_PORTRAIT, user.getPortraitPath());

        long id = super.db.insert(TABLE_USERS, null, values);
        close();
        return id;
    }

    public long insert(String name, int account, String password, String portraitPath) {
        open();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ACCOUNT, account);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PORTRAIT, portraitPath);

        long id = super.db.insert(TABLE_USERS, null, values);
        close();
        return id;
    }

    @Override
    public List<User> getAll() {
        open();
        List<User> userList = new ArrayList<>();

        Cursor cursor = super.db.query(
                TABLE_USERS,// 表名
                null,       // 要查询的列，null表示全部
                null,       // WHERE 条件
                null,       // WHERE 参数
                null,       // GROUP BY
                null,       // HAVING
                null        // ORDER BY
        );

        while (cursor.moveToNext()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setAccount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setPortraitPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PORTRAIT)));
            userList.add(user);
        }

        cursor.close();
        close();
        return userList;
    }

    @Override
    public User getByID(int id) {
        open();
        User user = null;

        Cursor cursor = super.db.query(
                TABLE_USERS,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setAccount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setPortraitPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PORTRAIT)));
        }

        cursor.close();
        close();
        return user;
    }

    @Override
    public int update(int id, User user) {
        open();
        ContentValues values = new ContentValues();

        if (user.getName() != null) {
            values.put(COLUMN_NAME, user.getName());
        }
        if (user.getAccount() != 0) {
            values.put(COLUMN_ACCOUNT, user.getAccount());
        }
        if (user.getPassword() != null) {
            values.put(COLUMN_PASSWORD, user.getPassword());
        }
        values.put(COLUMN_PORTRAIT, user.getPortraitPath());

        int rowsAffected = super.db.update(
                TABLE_USERS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        close();
        return rowsAffected;
    }

    public int update(int id, String name, int account, String password, String portraitPath) {
        open();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ACCOUNT, account);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PORTRAIT, portraitPath);

        int rowsAffected = super.db.update(
                TABLE_USERS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        close();
        return rowsAffected;
    }

    @Override
    public int delete(int id) {
        open();
        int rowsAffected = super.db.delete(
                TABLE_USERS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        close();
        return rowsAffected;
    }

    public User getByPassword(int account, String password) {
        open();
        User user = null;

        Cursor cursor = super.db.query(
                TABLE_USERS,
                null,
                COLUMN_ACCOUNT + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{String.valueOf(account), password},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setAccount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setPortraitPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PORTRAIT)));
        }

        cursor.close();
        close();
        return user;
    }

    public User getByAccount(int account) {
        open();
        User user = null;

        Cursor cursor = super.db.query(
                TABLE_USERS,
                null,
                COLUMN_ACCOUNT + " = ?",
                new String[]{String.valueOf(account)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setAccount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setPortraitPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PORTRAIT)));
        }

        cursor.close();
        close();
        return user;
    }
}
