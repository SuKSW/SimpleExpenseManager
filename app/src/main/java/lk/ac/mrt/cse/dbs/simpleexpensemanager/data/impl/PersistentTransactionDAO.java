package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by Subhashinie on 11/19/2016.
 */
public class PersistentTransactionDAO implements TransactionDAO  {

    private  Context context;
    protected SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private final List<Transaction> transactions;
    private DateFormat df;


    public PersistentTransactionDAO(Context context) {
        transactions = new LinkedList<>();
        this.context = context;
        dbHelper = DatabaseHelper.getHelper(context);
        df = new SimpleDateFormat("dd-MM-/yyyy");
        open();

    }
    public void open() throws SQLException {
        if(dbHelper == null)
            dbHelper = DatabaseHelper.getHelper(context);
        database = dbHelper.getWritableDatabase();
    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        transactions.add(transaction);
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.T_COL_1, df.format(transaction.getDate()));
        values.put(DatabaseHelper.T_COL_2, String.valueOf(transaction.getAccountNo()));
        if (transaction.getExpenseType().equals(ExpenseType.INCOME)) {
            values.put(DatabaseHelper.T_COL_3, 0);//0--->income
        }else if (transaction.getExpenseType().equals(ExpenseType.EXPENSE)){
            values.put(DatabaseHelper.T_COL_3, 1);
        }
        values.put(DatabaseHelper.T_COL_4, transaction.getAmount());
        database.insertOrThrow(DatabaseHelper.TRANSACTIONS_TABLE, null, values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        LinkedList<Transaction> allTransactions = new LinkedList<Transaction>();
        String query = "select * from "+DatabaseHelper.TRANSACTIONS_TABLE;
        Cursor cursor = database.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Transaction outTransaction = new Transaction();
            //date
            try {
                outTransaction.setDate(df.parse(cursor.getString(0)));
            } catch (ParseException e) {
                //outTransaction.setDate(null);
                outTransaction.setDate(new Date());
            }
            //accountNo
            outTransaction.setAccountNo(cursor.getString(1));
            //ExpenceType
            if (cursor.getInt(2)==0){
                outTransaction.setExpenseType(ExpenseType.INCOME);
            }else if (cursor.getInt(2)==1){
                outTransaction.setExpenseType(ExpenseType.EXPENSE);
            }
            //amount
            outTransaction.setAmount(cursor.getDouble(3));


            allTransactions.add(outTransaction);
        }

        return allTransactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();

        if (limit <= size) {
            return transactions.subList(size - limit, size);
        }

        List<Transaction> paginatedTransactionLogs =getAllTransactionLogs();
        int wholeSize= paginatedTransactionLogs.size();
                //(int)DatabaseUtils.longForQuery(database,
           //     "SELECT COUNT(*) FROM " + dbHelper.TRANSACTIONS_TABLE, null);
        if(limit <= wholeSize) {
            return paginatedTransactionLogs.subList(wholeSize - limit, wholeSize);
        }
        return paginatedTransactionLogs;
    }



}
