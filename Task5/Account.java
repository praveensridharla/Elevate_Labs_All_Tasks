import java.util.ArrayList;

public class Account {
    private String owner;
    private double balance;
    private ArrayList<String> history = new ArrayList<>();

    public Account(String owner, double initialBalance) {
        this.owner = owner;
        this.balance = initialBalance;
        history.add("Account opened with balance: $" + initialBalance);
    }

    public void deposit(double amount) {
        balance += amount;
        history.add("Deposited: $" + amount + " | Balance: $" + balance);
        System.out.println("Deposited $" + amount + ". New balance: $" + balance);
    }

    public void withdraw(double amount) {
        if (amount > balance) {
            System.out.println("Insufficient funds!");
        } else {
            balance -= amount;
            history.add("Withdrew: $" + amount + " | Balance: $" + balance);
            System.out.println("Withdrew $" + amount + ". New balance: $" + balance);
        }
    }

    public void printHistory() {
        System.out.println("\n--- Account Statement for " + owner + " ---");
        for (String record : history) {
            System.out.println(record);
        }
        System.out.println("Current Balance: $" + balance);
    }
}
