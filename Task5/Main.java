public class Main {
    public static void main(String[] args) {
        Account acc = new Account("John", 1000.0);

        acc.deposit(500.0);
        acc.withdraw(200.0);
        acc.withdraw(2000.0); // Should show insufficient funds
        acc.deposit(100.0);
        acc.withdraw(300.0);

        acc.printHistory();
    }
}
