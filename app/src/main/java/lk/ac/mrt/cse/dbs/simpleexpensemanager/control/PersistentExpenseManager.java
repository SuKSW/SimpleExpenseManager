package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InMemoryTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

/**
 * Created by Subhashinie on 11/21/2016.
 */
public class PersistentExpenseManager extends ExpenseManager {

    private Context context;

    public PersistentExpenseManager(Context context)  {
        this.context = context;
        try {
            setup();
        }catch(ExpenseManagerException e){}
    }
    @Override
    public void setup() throws ExpenseManagerException {
        /*** Begin generating dummy data for In-Memory implementation ***/

        //TransactionDAO inMemoryTransactionDAO = new InMemoryTransactionDAO();
        //setTransactionsDAO(inMemoryTransactionDAO);
        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(this.context);
        setTransactionsDAO(persistentTransactionDAO);

        //AccountDAO inMemoryAccountDAO = new InMemoryAccountDAO();
        //setAccountsDAO(inMemoryAccountDAO);
        AccountDAO persistentAccountDAO = new PersistentAccountDAO();
        persistentAccountDAO.createmethod(this.context);
        setAccountsDAO(persistentAccountDAO);

        // dummy data
        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        getAccountsDAO().addAccount(dummyAcct1);
        getAccountsDAO().addAccount(dummyAcct2);

        /*** End ***/
    }


}