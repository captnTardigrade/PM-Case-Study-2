import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

interface Options {
    void enterPIN();

    void showBalance();

    void withdrawCash(double amount);

    void depositCash(double amount);

    void moneyTransfer(double amount, String sourceAccount, String destAccount);
}

public class OptionsScreen {
    private static String accountNumber;

    public void main(String[] args) {
        OptionsScreen optionsScreen = new OptionsScreen();
        Hidden hidden = new Hidden();
        Scanner scanner = new Scanner(System.in);
        int key = 0;
        while (key == 0) {
            optionsScreen.showIdleMessage();
            try {
                key = System.in.read();
                if (optionsScreen.authenticationScreen()) {
                    optionsScreen.showOptions();
                    int option = scanner.nextInt();
                    while (option == 1 || option == 2 || option == 3 || option == 4 || option == 5) {
                        if (option == 1) {
                            System.out.println("Your current balance is: " + hidden.getBalance(this.accountNumber));
                        } else if (option == 2) {
                            System.out.print("Enter the amount to be withdrawn: ");
                            double amount = scanner.nextDouble();
                            hidden.removeBalance(accountNumber, amount);
                        } else if (option == 3) {
                            System.out.print("Enter the amount to be deposited: ");
                            double amount = scanner.nextDouble();
                            hidden.addBalance(accountNumber, amount);
                        } else if (option == 4) {
                            System.out.print("Please enter the reciever's account number: ");
                            String destAccountNumberOne = scanner.next();
                            System.out.println("Please re-enter the reciever's account number: ");
                            String destAccountNumberTwo = scanner.next();
                            while (destAccountNumberOne != destAccountNumberTwo) {
                                System.out.println("Reciever's account number don't match! Please confirm again.");
                                System.out.print("Please enter the reciever's account number: ");
                                destAccountNumberOne = scanner.next();
                                System.out.println("Please re-enter the reciever's account number: ");
                                destAccountNumberTwo = scanner.next();
                            }
                            if (destAccountNumberOne == destAccountNumberTwo) {
                                System.out.print("Enter the amount to be transferred: ");
                                double amount = scanner.nextDouble();
                                hidden.addBalance(destAccountNumberTwo, amount);
                            }
                        }
                        optionsScreen.showOptions();
                        option = scanner.nextInt();
                    }
                }
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showIdleMessage() { // showing idle messages until a key press
        System.out.println("Advertisement 1");
        System.out.println("Advertisement 2");
        System.out.println("Press enter to continue");
    }

    private boolean authenticationScreen() {
        Scanner scanner = new Scanner(System.in);
        Pattern accountPattern = Pattern.compile("[0-9]{9,18}"); // identifies as a valid account number if it has 9 to
                                                                 // 18 digits
        Pattern pinPattern = Pattern.compile("[0-9]{5}"); // identifies as valid a pin format if it has 5 digits
        System.out.print("Please enter your account number: ");
        String accountNumber = scanner.next();
        while (!accountPattern.matcher(accountNumber).matches()) {
            System.out.print("Please enter a valid account number:");
            accountNumber = scanner.next();
        }
        this.accountNumber = accountNumber;
        System.out.print("Please enter your PIN: ");
        String PIN = scanner.next();
        while (!pinPattern.matcher(String.valueOf(PIN)).matches()) {
            System.out.print("Please enter a valid PIN containing 5 digits: ");
            PIN = scanner.next();
        }
        Hidden hidden = new Hidden();
        while (!hidden.isCorrectPIN(accountNumber, PIN)) {
            hidden.numIncorrectPINAttempts += 1;
            if (hidden.numIncorrectPINAttempts > 3) {
                System.out
                        .println("Incorrect PIN. " + (5 - hidden.numIncorrectPINAttempts + 1) + " attempts remaining");
                System.out.print("Please enter your PIN: ");
                PIN = scanner.next();
            }
            if (hidden.numIncorrectPINAttempts == 5) {
                hidden.blockAccount(accountNumber);
                System.out.println("The account has been blocked. Please visit your nearest bank to unblock.");
                hidden.sendMessage(accountNumber, 404);
                return false;
            }
        }
        return true;
    }

    private void showOptions() {
        System.out.println("Enter 1 to show account balance");
        System.out.println("Enter 2 to withdraw cash");
        System.out.println("Enter 3 to deposit cash");
        System.out.println("Enter 4 to transfer money");
        System.out.println("Click any other button to exit");
    }
}
