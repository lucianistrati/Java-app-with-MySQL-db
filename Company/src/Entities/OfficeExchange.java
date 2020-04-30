package Entities;
import Services.NormalClientCSVReader;
import Services.NormalTransactionCSVReader;
import Services.PremiumClientCSVReader;
import Services.PremiumTransactionCSVReader;
import Exceptions.MyInputException;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDateTime;

import java.io.IOException;
import java.util.ArrayList;
//import au.com.bytecode.opencsv.CSVReader;


public class OfficeExchange { //This class is implemented as a SingleTon Class
    private static OfficeExchange single_instance = new OfficeExchange(); // Used for declaring singleton class
    private int CurrentDay = 0;
    private double PremiumTax = 100.0; // 100 dolars
    private double PremiumCommission = 0.005; // 0.5% commission for premium clients
    private double NormalCommission = 0.01; //1% commission for normal clients
    private double TotalProfit = 0.0;
    private HashMap < String, Double > CurrenciesAmounts;
    private HashMap < String, Double > CurrenciesParities;
    private ArrayList < HashMap < String, Double >> CurrenciesParitiesHistory;
    private Set < String > AllCurrencies;
    private ArrayList <Client> AllClients;
    private ArrayList <Transaction> AllTransactions;
    private NormalClientCSVReader nc;
    private PremiumClientCSVReader pc;
    private NormalTransactionCSVReader nt;
    private PremiumTransactionCSVReader pt;
    private enum TransactionType {
        Normal,
        Premium
    }
    private OfficeExchange() {
        this.AllClients = new ArrayList <Client> ();
        this.AllTransactions = new ArrayList <Transaction> ();
        this.CurrenciesAmounts = new HashMap < String, Double > ();
        this.CurrenciesParities = new HashMap < String, Double > ();
        this.CurrenciesParitiesHistory = new ArrayList < HashMap < String, Double >> ();
        this.AllCurrencies = new HashSet < String > ();
        this.nc = NormalClientCSVReader.getInstance();
        this.pc = PremiumClientCSVReader.getInstance();
        this.nt = NormalTransactionCSVReader.getInstance();
        this.pt = PremiumTransactionCSVReader.getInstance();

    }
    public static OfficeExchange getInstance() {
        return single_instance;
    }

    public static double roundAvoid(double value, int places) {
  /*This static method is truncating every double value that is passed to it
  to the first #places decimals, in this case, we will want to truncate to the first 4 decimals because that is the practice
  in the financial sector.
  */
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
    void addToTotalProfit(double sum) {
        this.TotalProfit += sum;
    }

    public double getTotalProfit() {
        return this.TotalProfit;
    }
    void setTotalProfit(double val) {
        this.TotalProfit = val;
    }

    int getCurrentDay() {
        return this.CurrentDay;
    }
    void setCurrentDay(int day) {
        this.CurrentDay = day;
    }

    double getPremiumTax() {
        return PremiumTax;
    }
    void setPremiumTax(double val) {
        this.PremiumTax = val;
    }

    double getPremiumCommission() {
        return this.PremiumCommission;
    }
    void setPremiumCommission(double val) {
        this.PremiumCommission = val;
    }

    double getNormalCommission() {
        return this.NormalCommission;
    }
    void setNormalCommission(double val) {
        this.NormalCommission = val;
    }
    public void loadAllData() throws IOException {
        ArrayList<String> v=new ArrayList<>();
        try {
            v.add("NormalClient.csv");
            v.add("PremiumClient.csv");
            v.add("NormalTransaction.csv");
            v.add("PremiumTransaction.csv");
        } catch (Exception e){
            e.printStackTrace();
        }
        for(String filename: v){
            if(filename.equals("NormalClient.csv")){
                nc.readFileData(filename,this.getNormalCommission());
                for(NormalClient x:nc.getReadNc()){
                    Client c = x;
                    AllClients.add(c);
                }
            }
            if(filename.equals("PremiumClient.csv")){
                pc.readFileData(filename,this.getPremiumCommission());
                for(PremiumClient x:pc.getReadPc()){
                    Client c = x;
                    AllClients.add(c);
                }
            }
            if(filename.equals("NormalTransaction.csv")){
                nt.readFileData(filename,this.getNormalCommission());
                for(NormalTransaction x:nt.getReadNt()){
                    Transaction c = x;
                    AllTransactions.add(c);
                }
            }
            if(filename.equals("PremiumTransaction.csv")){
                pt.readFileData(filename, this.getPremiumCommission());
                for(PremiumTransaction x:pt.getReadPt()){
                    Transaction c = x;
                    AllTransactions.add(c);
                }
            }
        }
        linkTransactionsToClients();
        System.out.println("All data was loaded from our database");
  }
    public void linkTransactionsToClients(){
        /*
        In this function we are going to iterate through all the normal transactions, for their id we will find the normal client
        with that id and then perform "Entities.Client.get(id).addTransaction(t)" the same operation will be performed for premium transactions
        and premium clients, after this operation is performed the all our csv data will be loaded
         */
        for(int i=0;i<AllClients.size();i++){
            Client c = AllClients.get(i);
            if(c instanceof PremiumClient){
                for(Transaction t:AllTransactions) {
                    if(c.getClientID()==t.getClientID()){
                        Transaction d = t;
                        AllClients.get(i).addTransaction(d,t.getTransactionDay(),this.getCurrenciesParities(t.getEntryCurrency(),t.getExitCurrency()),this.PremiumCommission,this.getCurrenciesParities(t.getEntryCurrency(),t.getExitCurrency()),t.getLocalTime(),this.AllClients.get(i).getClientID());
                    }
                }
            }else if(c instanceof NormalClient) {
                for(Transaction t:AllTransactions) {
                    if(c.getClientID()==t.getClientID()){
                        Transaction d = t;
                        AllClients.get(i).addTransaction(d,t.getTransactionDay(),this.getCurrenciesParities(t.getEntryCurrency(),t.getExitCurrency()),this.PremiumCommission,this.getCurrenciesParities(t.getEntryCurrency(),t.getExitCurrency()),t.getLocalTime(),this.AllClients.get(i).getClientID());
                    }
                }
            }
        }
    }
    public void addPremiumClient(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the premium client");
        String myName = sc.nextLine();
        LocalDateTime myObj = LocalDateTime.now();
        PremiumClient c = new PremiumClient(myName, this.CurrentDay, myObj);
        this.addToTotalProfit(this.PremiumTax);
        this.AllClients.add(c);
        System.out.println(this.AllClients.get(AllClients.size() - 1));
    }
    public void addAllData(){
        ArrayList< ArrayList<String> > premiumRecords = new ArrayList< ArrayList<String> >();
        ArrayList< ArrayList<String> > normalRecords = new ArrayList< ArrayList<String> >();
        String premiumPath = "Entities.PremiumClient.csv",normalPath="Entities.NormalClient.csv";
        try {
        for(Client c:AllClients) {
            if(c instanceof PremiumClient) {
                    ArrayList<String> rec = new ArrayList<>();
                    String[] record = new String[4];
                    record[0] = Integer.toString(c.ClientID);
                    record[1] = c.name;
                    record[2] = Integer.toString(c.ClientDay);
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = c.localTime.format(myFormatObj);
                    record[3] = formattedDate;
                    for(String r:record){
                        rec.add(r);
                    }
                    premiumRecords.add(rec);

            }else if(c instanceof NormalClient){
                ArrayList<String> rec = new ArrayList<>();
                String[] record = new String[4];
                record[0] = Integer.toString(c.ClientID);
                record[1] = c.name;
                record[2] = Integer.toString(c.ClientDay);
                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDate = c.localTime.format(myFormatObj);
                record[3] = formattedDate;
                for(String r:record){
                    rec.add(r);
                }
                normalRecords.add(rec);
            }
        }
        pc.writeFileData(premiumPath, premiumRecords);
        nc.writeFileData(normalPath, normalRecords);
        } catch (IOException e) {
            e.printStackTrace();
        }
        premiumRecords.clear();
        normalRecords.clear();
        premiumRecords = new ArrayList< ArrayList<String> >();
        normalRecords = new ArrayList< ArrayList<String> >();
        premiumPath = "Entities.PremiumTransaction.csv";
        normalPath="Entities.NormalTransaction.csv";
        try {
            for(Transaction c:AllTransactions) {
                boolean isItPremium = false;
                for(Client f: AllClients){
                    if(c.ClientID==f.ClientID){
                        if(f instanceof PremiumClient){
                            isItPremium = true;
                        }
                        if(f instanceof NormalClient){
                            isItPremium = false;
                        }
                    }
                }
                if(isItPremium==true) {
                    ArrayList<String> rec = new ArrayList<>();
                    rec.add(Double.toString(c.EntrySum));
                    rec.add(c.EntryCurrency);
                    rec.add(Double.toString(c.ExitSum));
                    rec.add(c.ExitCurrency);
                    rec.add(Double.toString(c.TransactionProfit));
                    rec.add(Integer.toString(c.TransactionDay));
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = c.localTime.format(myFormatObj);
                    rec.add(formattedDate);
                    rec.add(Integer.toString(c.ClientID));
                    premiumRecords.add(rec);
                }else
                    {
                    ArrayList<String> rec = new ArrayList<>();
                    rec.add(Double.toString(c.EntrySum));
                    rec.add(c.EntryCurrency);
                    rec.add(Double.toString(c.ExitSum));
                    rec.add(c.ExitCurrency);
                    rec.add(Double.toString(c.TransactionProfit));
                    rec.add(Integer.toString(c.TransactionDay));
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = c.localTime.format(myFormatObj);
                    rec.add(formattedDate);
                    rec.add(Integer.toString(c.ClientID));
                    normalRecords.add(rec);
                }
            }
            pt.writeFileData(premiumPath, premiumRecords);
            nt.writeFileData(normalPath, normalRecords);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("All data was loaded back in our database");
    }
    public void addNormalClient() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the normal client");
        String myName = sc.nextLine();
        LocalDateTime myObj = LocalDateTime.now();
        NormalClient d = new NormalClient(myName, this.CurrentDay, myObj);
        this.AllClients.add(d);
        System.out.println(this.AllClients.get(AllClients.size() - 1));
    }
    public void proceedWithPremiumTransaction() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the premium client");
        String myName = sc.nextLine();
        System.out.println("Enter the initial currency");
        String myInitialCurrency = sc.nextLine();
        System.out.println("Enter the final currency");
        String myFinalCurrency = sc.nextLine();
        try {
            if (AllCurrencies.contains(myInitialCurrency) == false || AllCurrencies.contains(myFinalCurrency) == false) {
                throw new MyInputException("One of your selected currencies does not exist in our DataBase");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        System.out.println("Enter the sum of money");
        double mySumOfMoney = Double.parseDouble(sc.nextLine());
        boolean foundClient = false;
        int positionFound = -1;
        for (int i = 0; i < this.AllClients.size(); i++) {
            if (this.AllClients.get(i).getName().equals(myName)) {
                foundClient = true;
                positionFound = i;
                break;
            }
        }
        try {
            if (foundClient == false) {
                throw new MyInputException("The normal client does not exist try to create it");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        double x, conv = this.obtainConversionRate(myInitialCurrency, myFinalCurrency);
        LocalDateTime myObj = LocalDateTime.now();
        if (this.CurrenciesAmounts.get(myFinalCurrency) > conv * mySumOfMoney) {
            NormalTransaction t = new NormalTransaction(mySumOfMoney, myInitialCurrency, myFinalCurrency, this.getCurrentDay(), conv, this.PremiumCommission, this.getCurrenciesParities(myInitialCurrency, myFinalCurrency), myObj,  this.AllClients.get(positionFound).getClientID());
            this.TotalProfit += t.getTransactionProfit();
            Transaction u = new Transaction(mySumOfMoney, myInitialCurrency, myFinalCurrency, this.getCurrentDay(), myObj, this.AllClients.get(positionFound).getClientID());
            this.AllClients.get(positionFound).addTransaction(u, this.getCurrentDay(), conv, this.PremiumCommission, this.getCurrenciesParities(myInitialCurrency, myFinalCurrency), myObj, this.AllClients.get(positionFound).getClientID());
            this.AllTransactions.add(t);
            x = t.getExitSum();
            System.out.println("Your money are " + x + " " + myFinalCurrency);
            this.setCurrencyAmount(myInitialCurrency, this.CurrenciesAmounts.get(myInitialCurrency) + mySumOfMoney); //change the volume on entry currency
            this.setCurrencyAmount(myFinalCurrency, this.CurrenciesAmounts.get(myFinalCurrency) - t.getExitSum()); //change the volume on exit currency
           // your_string= your_string.replaceAll("\\s*,\\s*", ",");
        } else {
            System.out.println("Insufficient currency in the HOUSE for your transaction, sorry");
        }

    }


    public void proceedWithNormalTransaction() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the normal client");
        String myName = sc.nextLine();
        System.out.println("Enter the initial currency");
        String myInitialCurrency = sc.nextLine();
        System.out.println("Enter the final currency");
        String myFinalCurrency = sc.nextLine();
        try {
            if (AllCurrencies.contains(myInitialCurrency) == false || AllCurrencies.contains(myFinalCurrency) == false) {
                throw new MyInputException("One of your selected currencies does not exist in our DataBase");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        System.out.println("Enter the sum of money");
        double mySumOfMoney = Double.parseDouble(sc.nextLine());
        boolean foundClient = false;
        int positionFound = -1;
        for (int i = 0; i < this.AllClients.size(); i++) {
            if (AllClients.get(i).getName().equals(myName)) {
                foundClient = true;
                positionFound = i;
                break;
            }
        }
        try {
            if (foundClient == false) {
                throw new MyInputException("The normal client does not exist try to create it");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        double x, conv = this.obtainConversionRate(myInitialCurrency, myFinalCurrency);
        LocalDateTime myObj = LocalDateTime.now();
        if (this.CurrenciesAmounts.get(myFinalCurrency) > conv * mySumOfMoney) {
            NormalTransaction t = new NormalTransaction(mySumOfMoney, myInitialCurrency, myFinalCurrency, this.getCurrentDay(), conv, this.NormalCommission, this.getCurrenciesParities(myInitialCurrency, myFinalCurrency), myObj,  this.AllClients.get(positionFound).getClientID());
            this.TotalProfit += t.getTransactionProfit();
            Transaction u = new Transaction(mySumOfMoney, myInitialCurrency, myFinalCurrency, this.getCurrentDay(), myObj, this.AllClients.get(positionFound).getClientID());
            this.AllClients.get(positionFound).addTransaction(u, this.getCurrentDay(), conv, this.NormalCommission, this.getCurrenciesParities(myInitialCurrency, myFinalCurrency), myObj, this.AllClients.get(positionFound).getClientID());
            this.AllTransactions.add(t);
            x = t.getExitSum();
            System.out.println("Your money are " + x + " " + myFinalCurrency);
            this.setCurrencyAmount(myInitialCurrency, this.CurrenciesAmounts.get(myInitialCurrency) + mySumOfMoney); //change the volume on entry currency
            this.setCurrencyAmount(myFinalCurrency, this.CurrenciesAmounts.get(myFinalCurrency) - t.getExitSum()); //change the volume on exit currency
            //your_string= your_string.replaceAll("\\s*,\\s*", ",");
        } else {
            System.out.println("Insufficient currency in the HOUSE for your transaction, sorry");
        }

    }
    public void supplementCurrency() {
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the currency?");
        String entry = sc.nextLine();
        try {
            if (AllCurrencies.contains(entry) == false) {
                throw new MyInputException("The currency is not registered in our Database");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        System.out.println("What is the amount you would like to deposit?");
        double x = Double.parseDouble(sc.nextLine()), u = this.CurrenciesAmounts.get(entry);
        try {
            if (x <= 0.0) {
                throw new MyInputException("The amount of money to deposit can not be negative or zero");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        this.CurrenciesAmounts.put(entry, u + x);

    }

    public double getLastTransactionProfit() {
        return this.AllTransactions.get(AllTransactions.size() - 1).TransactionProfit;
    }
    public void incrementCurrentDay() {
        this.CurrentDay += 1;
        CurrenciesParitiesHistory.add(CurrenciesParities);
        this.actualizeCurrencies();
        System.out.println("One day passed. The new exchange is:");
        for (String s: this.CurrenciesParities.keySet()) {
            System.out.println(s + ":" + this.CurrenciesParities.get(s));
        }
    }
    public void addCurrency(String s) {
        this.AllCurrencies.add(s);
    }

    public double obtainConversionRate(String entry, String exit) {
        double first, second;
        String a, b;
        if (entry.equals("dolars") || exit.equals("dolars")) {
            return this.CurrenciesParities.get(entry + "-->" + exit);
        } else {
            a = new String(entry + "-->dolars");
            b = new String("dolars-->" + exit);
        }
        first = this.CurrenciesParities.get(a);
        second = this.CurrenciesParities.get(b);
        return roundAvoid(first * second, 4);
    }

    public void modifyPremiumTax() {
        double x;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the new tax for premium clients");
        x = Double.parseDouble(sc.nextLine());
        try {
            if (x <= 0.0) {
                throw new MyInputException("The tax for premimum clients cannot be zero or less than zero");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        this.setPremiumTax(x);
        System.out.println("The new tax for premium clients is " + x + "$");
    }
    public void modifyNormalCommission() {
        double x;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the new commission for premium clients");
        x = Double.parseDouble(sc.nextLine());
        try {
            if (x < 0.0 || x >= 1.0) {
                throw new MyInputException("The normal commision has to be between 0.01 and 1.00");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        this.setPremiumCommission(x);
        System.out.println("The new commission for normal clients is " + x * 100 + "%");
    }
    public void modifyPremiumCommission() {
        double x;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the new commission for normal clients");
        x = Double.parseDouble(sc.nextLine());
        try {
            if (x < 0.0 || x >= 1.0) {
                throw new MyInputException("The premium commision has to be between 0.01 and 1.00");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        this.setPremiumCommission(x);
        System.out.println("The new commission for premium clients is " + x * 100 + "%");
    }
    public void getTotalProfitPerDay() {
        double total = 0;
        System.out.println("What is the day for which you want to know the profit?");
        Scanner sc = new Scanner(System.in);
        int day = sc.nextInt();
        for (int i = 0; i < AllTransactions.size(); i++) {
            if (AllTransactions.get(i).getTransactionDay() == day) {
                total += AllTransactions.get(i).getTransactionProfit();
            }
        }
        System.out.println("The total profit in the day  " + day + " is " + roundAvoid(total, 4));
    }
    public void getTransactionHistoryOfAClient() {
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the name of the client?");
        String myName = sc.nextLine();
        for (int i = 0; i < AllClients.size(); i++) {
            if (AllClients.get(i).name.equals((myName))) {
                if (AllClients.get(i) instanceof NormalClient) {
                    NormalClient A = ((NormalClient) AllClients.get(i));
                    System.out.println(A);
                    System.out.println(myName + " transactions are: ");
                    ArrayList <NormalTransaction> B = A.getMyNormalTransactions();
                    for (int j = 0; j < B.size(); j++) {
                        System.out.println(B.get(j));
                    }
                } else if(AllClients.get(i) instanceof PremiumClient){
                    PremiumClient A = ((PremiumClient) AllClients.get(i));
                    System.out.println(A);
                    System.out.println(myName + " transactions are: ");
                    ArrayList <PremiumTransaction> B = A.getMyPremiumTransactions();
                    for (int j = 0; j < B.size(); j++) {
                        System.out.println(B.get(j));
                    }
                }
    /*
    for(int j=0;j<AllClients.get(i). ;j++){

    }*/
                break;
            }
        }
    }
    public void getAllCurrencies() {
        System.out.println("All currencies are: ");
        for (String key: this.AllCurrencies) {
            System.out.println(key + " with the quantity: " + this.CurrenciesAmounts.get(key));
        }
    }
    public void getLastTransactionInformations() {
        System.out.println(this.AllTransactions.get(AllTransactions.size() - 1));
    }
    public void getInformationsAboutAllTransactionsInACertainDay() {
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the day for which you want to know the informations?");
        int day = sc.nextInt();
        for (int i = 0; i < AllTransactions.size(); i++) {
            if (AllTransactions.get(i).getTransactionDay() == day) {
                System.out.println(AllTransactions.get(i));
            }
        }
    }
    public double getCurrenciesParities(String xc, String s) {
        if (xc.equals("dolars"))
            return CurrenciesParities.get(s + "-->dolars");
        return CurrenciesParities.get(xc + "-->dolars");
    }

    public double getCurrencyAmount(String currency) {
        return this.CurrenciesAmounts.get(currency);
    }
    public void setCurrencyAmount(String currency, Double amount) {
        CurrenciesAmounts.put(currency, amount);
    }

    public void setCurrencyPair(String firstCurrency, String secondCurrency, Double exchangeCoefficient) {
        String oneWay = firstCurrency + "-->" + secondCurrency;
        this.CurrenciesParities.put(oneWay, roundAvoid(exchangeCoefficient, 4));
        String reverseWay = secondCurrency + "-->" + firstCurrency;
        this.CurrenciesParities.put(reverseWay, roundAvoid(1 / exchangeCoefficient, 4));
    }

    static double generateRandomModificationCoefficient(double number) {
        double min = number - number * 0.05, max = number + number * 0.05;
        Random r = new Random();
        double randomValue;
        do {
            randomValue = min + (max - min) * r.nextDouble();
        } while (randomValue <= 0);
        return randomValue;
    }

    public void actualizeCurrencies() {
  /*
  This function generates for a currency pair a fluctuation of at most 5%(either on plus or on minus) in order to make
  the currencies flow. This happens after each day and a day passes after 3 made operations, any operations are considered
  to have the same length of time, from adding a new client to a new transaction for a client we already have.
   */
        Set < String > keys = CurrenciesParities.keySet();
        String prefix = "dolars-->";
        for (String key: keys) {
            if (key.startsWith(prefix) == true) {
                double newCoefficient = generateRandomModificationCoefficient(CurrenciesParities.get(key));
                CurrenciesParities.put(key, roundAvoid(newCoefficient, 4));
                int firstDelimitation = key.indexOf("-");
                int lastDelimitation = key.indexOf(">");
                String newKey = key.substring(lastDelimitation + 1) + "-->" + key.substring(0, firstDelimitation);
                CurrenciesParities.put(newKey, roundAvoid(1 / newCoefficient, 4));
            }
        }
    }
    /*
    void drawHistoryParity(){
        String entry,exit;
        Scanner sc=new Scanner(System.in);
        System.out.println("What is your first currency?");
        entry=sc.nextLine();
        System.out.println("What is your second currency?");
        exit=sc.nextLine();
    }
    */
    public void saveAllDataToFile() {
        try {
            FileOutputStream f = new FileOutputStream("HouseHistory.txt");
            ObjectOutputStream o = new ObjectOutputStream(f);
            for (int i = 0; i < AllClients.size(); i++) {
                if (AllClients.get(i) instanceof NormalClient) {
                    NormalClient A = ((NormalClient) AllClients.get(i));
                    o.writeObject(A);
                    //o.writeObject(AllClients.get(i).getName() + " transactions are: ");
                    ArrayList <NormalTransaction> B = A.getMyNormalTransactions();
                    for (int j = 0; j < B.size(); j++) {
                        o.writeObject(B.get(i));
                    }
                } else {
                    PremiumClient A = ((PremiumClient) AllClients.get(i));
                    o.writeObject(A);
                    //o.writeObject(AllClients.get(i).getName() + " transactions are: ");
                    ArrayList <PremiumTransaction> B = A.getMyPremiumTransactions();
                    for (int j = 0; j < B.size(); j++) {
                        o.writeObject(B.get(i));
                    }
                }
            }
            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } finally {
            System.out.println("All the details were written successfully in the HouseHistory.txt file");
        }
    }
}