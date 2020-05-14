package Services;

import Audit.AuditService;
import Entities.OfficeExchange;
import Exceptions.MyInputException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.io.IOException;
//The service class
public class Main {
    /*The dolar is the reference currency for this Office Exchange
         The way conversion is done from a currency A to a currency B is by converting A to dolars and then from dolars to B
         When we set currency pair it is assumed that The first currency equals to the second currency times the currency factor.
         Traditionally, when we are talking about any transactions involving currencies we need to define the quotations for currencies
         using the first four decimals in our transactions. This is also a good practice in computer science because we reduce
         the overhead for doing calculations with numbers which have an infinite number of digits after the floating point.
   */
    static void readNumberToAction(HashMap<Integer,String> numberToAction){
        try
        {
            File file=new File("Actions.txt");    //creates a new file instance
            FileReader fr=new FileReader(file);   //reads the file
            BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
            //StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters
            String line;
            while((line=br.readLine())!=null)
            {
                for(int i=0;i<line.length();i++){
                    if(line.charAt(i)=='.'){
                        int num = Integer.parseInt(line.substring(0,i));
                        String act = line.substring(i+1,line.length());
                        numberToAction.put(num,act);
                        break;
                    }
                }
            }
            fr.close();    //closes the stream and release the resources
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to the Office Exchange App");
        int checker = 0;
        boolean check = true;
        HashMap<Integer,String> numberToAction = new HashMap<Integer, String>();
        readNumberToAction(numberToAction);
        AuditService c = AuditService.getInstance();
        ArrayList<String> t=new ArrayList<>();
        ArrayList<String> dates=new ArrayList<>();
        OfficeExchange of = OfficeExchange.getInstance();
        of.addCurrency("euros"); of .addCurrency("dolars"); of .addCurrency("yens"); of .setCurrencyAmount("euros", 10000.0); of .setCurrencyAmount("dolars", 10000.0); of .setCurrencyAmount("yens", 100000000.0); of .setCurrencyPair("dolars", "euros", 0.9343); of .setCurrencyPair("dolars", "yens", 111.1095);
        of.loadAllData();
        while (check == true) {
            if (checker == 0) {
                System.out.println("You have the following options:");
                System.out.println("1.Create premium client");
                System.out.println("2.Create normal client");
                System.out.println("3.Make a normal transaction");
                System.out.println("4.Make a premium transaction");
                System.out.println("5.Change the tax for premium clients");
                System.out.println("6.Change the commission for premium clients");
                System.out.println("7.Change the commission for normal clients");
                System.out.println("8.Supplement the amount of money for a certain currency");
                System.out.println("9.Obtain total profit");
                System.out.println("10.Obtain last transaction profit");
                System.out.println("11.Obtain the profit from a certain day");
                System.out.println("12.Obtain the price of a Currency Pair");
                System.out.println("13.Obtain transaction history of a client");
                System.out.println("14.Obtain all currencies available for transactions");
                System.out.println("15.Obtain last transaction informations");
                System.out.println("16.Obtain informations about all the transactions from a certain day");
                System.out.println("17.Make a day pass");
                //System.out.println("18.Draw the history of a parity");
                System.out.println("18.Exit from the program");
                checker = 1;
            }

            Scanner sc = new Scanner(System.in);
            int option = sc.nextInt();
            try {
                if (option < 1 || option > 18) {
                    throw new MyInputException("Not a valid menu option");
                }
            } catch (MyInputException e) {
                e.printStackTrace();
            }
            double x;
            if(option>=1 && option<=18){
                t.add(numberToAction.get(option));
                LocalDateTime myObj = LocalDateTime.now();
                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDate = myObj.format(myFormatObj);
                dates.add(formattedDate);
            }
           // c.writeInAuditFile(numberToAction.get(option));
            switch (option) {
                case 1:

                    of.addPremiumClient();
                    break;
                case 2:
                    of .addNormalClient();
                    break;
                case 3:
                    of .proceedWithPremiumTransaction();
                    break;
                case 4:
                    of .proceedWithNormalTransaction();
                    break;
                case 5:
                    of .modifyPremiumTax();
                    break;
                case 6:
                    of .modifyPremiumCommission();
                    break;
                case 7:
                    of .modifyNormalCommission();
                    break;
                case 8:
                    of .supplementCurrency();
                    break;
                case 9:
                    System.out.println("The total profit is " + of .getTotalProfit());
                    break;
                case 10:
                    System.out.println("The last transaction profit is " + of .getLastTransactionProfit());
                    break;
                case 11:
                    of .getTotalProfitPerDay();
                    break;
                case 12:
                    Scanner sca = new Scanner(System.in);
                    String entry, exit;
                    System.out.println("What is the first currency?");
                    entry = sca.nextLine();
                    System.out.println("What is the second currency?");
                    exit = sca.nextLine();
                    System.out.println("One " + entry.substring(0, entry.length() - 1) + " is " + exit.substring(0, exit.length() - 1) + " " + of .obtainConversionRate(entry, exit));
                    break;
                case 13:
                    of .getTransactionHistoryOfAClient();
                    break;
                case 14:
                    of .getAllCurrencies();
                    break;
                case 15:
                    of .getLastTransactionInformations();
                    break;
                case 16:
                    of .getInformationsAboutAllTransactionsInACertainDay();
                    break;
                case 17:
                    of .incrementCurrentDay();
                    break;
                case 18:
                    System.out.println("You exited the application");
                    check = false;
                    break;
                //case 19:
                //  of.drawHistoryParity();
                //break;
            }
        }
        try {
            c.writeInAuditFile(t,dates);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            of.addAllData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Hope you had a good time, see you soon!");
    }
}