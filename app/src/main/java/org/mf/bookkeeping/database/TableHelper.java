package org.mf.bookkeeping.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public abstract class TableHelper<T> implements FixedFields {

    protected SQLiteDatabase db;

    protected TableHelper() {}

    protected void open() {
        this.db = DatabaseHelper.getDatabase();
    }

    protected void close() {
        if (this.db != null && this.db.isOpen()) {
            this.db.close();
        }
    }

    public abstract long insert(T obj);

    public abstract List<T> getAll();

    public abstract T getByID(int id);

    public abstract int update(int id, T obj);

    public abstract int delete(int id);

    protected static String[] valueOfID(int id) {
        return new String[] {String.valueOf(id)};
    }
}
