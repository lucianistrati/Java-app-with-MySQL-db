package Entities;

import Entities.Client;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.io.*;
public class PremiumClient extends Client implements Serializable {
    private ArrayList <PremiumTransaction> MyPremiumTransactions;
    public PremiumClient(String myName, int day, LocalDateTime local) {
        super(myName, day, local);
        MyPremiumTransactions = new ArrayList <PremiumTransaction> ();
    }
    ArrayList <PremiumTransaction> getMyPremiumTransactions() {
        return MyPremiumTransactions;
    }
    @Override
    void addTransaction(Transaction x, int a, double b, double c, double d, LocalDateTime l, int f) {
        PremiumTransaction y = new PremiumTransaction(x.EntrySum, x.EntryCurrency, x.ExitCurrency, a, b, c, d, l, f);
        this.MyPremiumTransactions.add(y);
    }
}