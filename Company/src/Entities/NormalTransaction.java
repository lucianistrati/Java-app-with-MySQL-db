package Entities;

import java.time.LocalDateTime;
import java.io.*;
import java.util.*;
public class NormalTransaction extends Transaction implements Serializable {
    private static double NormalTransactionsProfit = 0.0;
    public NormalTransaction(double es, String ec, String xc, int day, double conversionRate, double normCommission, double currParity, LocalDateTime local,int id) {
        super(es, ec, xc, day, local,id);
        double x = Transaction.roundAvoid(conversionRate, 4);
        this.TransactionProfit = Transaction.roundAvoid(normCommission * es * x, 4); // profit obtained in exit currency
        this.ExitSum = Transaction.roundAvoid(es * x - this.TransactionProfit, 4); // exit sum in exit currency
        if (xc.equals("dolars") == false) {
            this.TransactionProfit = Transaction.roundAvoid(currParity * this.TransactionProfit, 4);
        }
        NormalTransactionsProfit += this.TransactionProfit;
    }
    double getNormalTransactionsProfit() {
        return NormalTransactionsProfit;
    }
    void setNormalTransactionsProfit(double val) {
        NormalTransactionsProfit = val;
    }
    public String toString() {
        return super.toString();
    }
}