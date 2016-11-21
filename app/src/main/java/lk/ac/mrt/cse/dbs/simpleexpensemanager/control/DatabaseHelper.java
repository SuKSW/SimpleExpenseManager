package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Subhashinie on 11/21/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "140672X.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TRANSACTIONS_TABLE = "Transactions";
    public static final String ACCCOUNTS_TABLE = "Accounts";

    public static final String T_COL_1 = "date";
    public static final String T_COL_2 = "accountNo";
    public static final String T_COL_3 = "expenceType";
    public static final String T_COL_4 = "amount";

    public static final String A_COL_1 = "accountNo";
    public static final String A_COL_2 = "bankName";
    public static final String A_COL_3 = "accountHolder";
    public static final String A_COL_4 = "balance";

    public static final String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TRANSACTIONS_TABLE + "(" + T_COL_1 + " TEXT , "
            + T_COL_2 + " TEXT, " + T_COL_3 + " INTEGER, "
            + T_COL_4 + " REAL, "
            + "FOREIGN KEY(" + T_COL_2 + ") REFERENCES "
            + ACCCOUNTS_TABLE + "(accountNo) " + ");";

    public static final String CREATE_ACCCOUNTS_TABLE = "CREATE TABLE IF NOT EXISTS "
            + ACCCOUNTS_TABLE + "(" + A_COL_1 + " TEXT PRIMARY KEY, "
            + A_COL_2 + " TEXT, " + A_COL_3 + " TEXT, "
            + A_COL_4 + " REAL " + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TRANSACTIONS_TABLE );
        db.execSQL(CREATE_ACCCOUNTS_TABLE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private static DatabaseHelper instance;
    public static synchronized DatabaseHelper getHelper(Context context) {
        if (instance == null)
            instance = new DatabaseHelper(context);
        return instance;
    }
}
