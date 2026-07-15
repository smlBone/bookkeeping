package org.mf.bookkeeping.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper implements FixedFields {

    private static DatabaseHelper instance;
    private static RecordTable recordTable;
    private static UserTable userTable;

    public static void initialize(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
            recordTable = new RecordTable();
            userTable = new UserTable();
        }
    }

    protected static SQLiteDatabase getDatabase() {
        if (instance == null) {
            throw new NullPointerException("DataBaseHelper 尚未初始化");
        }
        return instance.getWritableDatabase();
    }

    public static RecordTable getRecordHelper() {
        if (instance == null) {
            throw new NullPointerException("DataBaseHelper 尚未初始化");
        }
        return recordTable;
    }

    public static UserTable getUserHelper() {
        if (instance == null) {
            throw new NullPointerException("DataBaseHelper 尚未初始化");
        }
        return userTable;
    }

    private DatabaseHelper(Context context) {
        super(context, "bookkeeping.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_ACCOUNT + " INTEGER NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_PORTRAIT + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_RECORDS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_YEAR + " INTEGER NOT NULL, " +
                COLUMN_MONTH + " INTEGER NOT NULL, " +
                COLUMN_DAY_OF_MONTH + " INTEGER NOT NULL, " +
                COLUMN_CATEGORY + " INTEGER NOT NULL, " +
                COLUMN_NOTE + " TEXT, " +
                COLUMN_AMOUNT + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // 第一次更新，加入字段 portrait_path
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_PORTRAIT + " TEXT");
        }
    }
}
