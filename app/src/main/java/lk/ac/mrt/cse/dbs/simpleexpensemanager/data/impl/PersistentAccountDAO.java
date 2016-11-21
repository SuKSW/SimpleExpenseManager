package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Subhashinie on 11/19/2016.
 */
public class PersistentAccountDAO implements AccountDAO {

    //private final Map<String, Account> accounts;
    //Serializable {
        private transient Context context;
        protected transient SQLiteDatabase database;
        private transient DatabaseHelper dbHelper;
    //}

    public void createmethod(Context context) throws SQLException{
        //this.accounts = new HashMap<>();
        this.context = context;
        dbHelper = DatabaseHelper.getHelper(context);
        if(dbHelper == null)
            dbHelper = DatabaseHelper.getHelper(context);
        database = dbHelper.getWritableDatabase();
    }
    //public PersistentAccountDAO(){}


    @Override
    public List<String> getAccountNumbersList() {
        List<String>  accountNumbersList = new LinkedList();
        Log.d("me","before dbhelper pointer");
        String listquery = "select "+DatabaseHelper.A_COL_1 +" from "+DatabaseHelper.ACCCOUNTS_TABLE+";";
        Log.d("me","before database pointer");
        Cursor listcursor = database.rawQuery(listquery, null);
        Log.d("me","after database pointer but before listcursor pointer");
        while (listcursor.moveToNext()) {
            accountNumbersList.add(listcursor.getString(0));
        }
        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList()  {
        List<Account> fullList = new ArrayList<>();
        String listquery = "select "+DatabaseHelper.A_COL_1 +" from "+DatabaseHelper.ACCCOUNTS_TABLE+";";
        Cursor listcursor = database.rawQuery(listquery, null);
        while (listcursor.moveToNext()) {
            try {
                Account oneAccount = getAccount(listcursor.getString(0));
                fullList.add(oneAccount);
            }catch (InvalidAccountException e){
                String msg = "previous execution is invalid.";
            }
        }
        return fullList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        int isthere0 = (int)DatabaseUtils.longForQuery(database,
                "SELECT COUNT(*) FROM " + DatabaseHelper.ACCCOUNTS_TABLE+" WHERE "+ DatabaseHelper.A_COL_1+"= '"+accountNo+"' ;", null);
        if (isthere0 <=0) {
            Account getaccount=new Account();
            String getquery = "select * from "+DatabaseHelper.ACCCOUNTS_TABLE+ " WHERE "+ DatabaseHelper.A_COL_1+" = '"+accountNo+"' ;";
            Cursor getcursor = database.rawQuery(getquery, null);
            getaccount.setAccountNo(getcursor.getString(0));
            getaccount.setBankName(getcursor.getString(1));
            getaccount.setAccountHolderName(getcursor.getString(2));
            getaccount.setBalance(getcursor.getDouble(3));
            return getaccount;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        //accounts.put(account.getAccountNo(), account);
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseHelper.A_COL_1, account.getAccountNo());
            values.put(DatabaseHelper.A_COL_2, account.getBankName());
            values.put(DatabaseHelper.A_COL_3, account.getAccountHolderName());
            values.put(DatabaseHelper.A_COL_4, account.getBalance());
            database.insertOrThrow(DatabaseHelper.ACCCOUNTS_TABLE, null, values);
        }catch(SQLiteConstraintException e){}
    }

    private static final String WHERE_ID_EQUALS = DatabaseHelper.A_COL_1 + " =?";
    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        int isthere1 = (int)DatabaseUtils.longForQuery(database,
                "SELECT COUNT(*) FROM " + DatabaseHelper.ACCCOUNTS_TABLE+" WHERE "+ DatabaseHelper.A_COL_1+" = '"+accountNo+"' ;", null);
        if (isthere1 <=0) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        database.delete(DatabaseHelper.ACCCOUNTS_TABLE, WHERE_ID_EQUALS,
                new String[] { accountNo + "" });
        //accounts.remove(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        int isthere2 = (int)DatabaseUtils.longForQuery(database,
                "SELECT COUNT(*) FROM " + DatabaseHelper.ACCCOUNTS_TABLE+" WHERE "+ DatabaseHelper.A_COL_1+" = '"+accountNo+"' ;", null);
        if (isthere2 <=0 ) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        String query = "select * from "+DatabaseHelper.ACCCOUNTS_TABLE+ " WHERE "+ DatabaseHelper.A_COL_1+" = '"+accountNo+"' ;";
        Cursor cursor = database.rawQuery(query, null);
        // specific implementation based on the transaction type
        Double updatedAmount=0.0;
        switch (expenseType) {
            case EXPENSE:
                updatedAmount = cursor.getColumnIndex(DatabaseHelper.A_COL_4) - amount;
                break;
            case INCOME:
                updatedAmount = cursor.getColumnIndex(DatabaseHelper.A_COL_4) + amount;
                break;
        }
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.A_COL_4,updatedAmount);
        database.update(DatabaseHelper.ACCCOUNTS_TABLE, cv, DatabaseHelper.A_COL_1+" = '"+accountNo+"'", null);
    }
}
