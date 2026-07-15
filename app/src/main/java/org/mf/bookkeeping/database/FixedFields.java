package org.mf.bookkeeping.database;

public interface FixedFields {
    String TABLE_USERS = "users";
    String TABLE_RECORDS = "records";

    String COLUMN_ID = "_id";
    String COLUMN_NAME = "name";
    String COLUMN_ACCOUNT = "account";
    String COLUMN_PASSWORD = "password";
    String COLUMN_PORTRAIT = "portrait_path";

    String COLUMN_USER_ID = "user_id";
    String COLUMN_YEAR = "year";
    String COLUMN_MONTH = "month";
    String COLUMN_DAY_OF_MONTH = "day_of_month";
    String COLUMN_CATEGORY = "category";
    String COLUMN_NOTE = "note";
    String COLUMN_AMOUNT = "amount";
}
