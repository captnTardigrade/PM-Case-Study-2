import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

//citation: https://www.geeksforgeeks.org/sha-256-hash-in-java/ used for calculating SHA-256 hash

interface HiddenMechanism {
    boolean isCorrectPIN(String accountNumber, String PIN);

    double getBalance(String accountNumber);

    void addBalance(String accountNumber, double amount);

    void removeBalance(String accountNumber, double amount);

    void blockAccount(String accountNumber);

    void sendMessage(String account, int status);

    void changePIN(String accountNumber);
}

public class Hidden implements HiddenMechanism {
    protected int numIncorrectPINAttempts = 0;

    public boolean isCorrectPIN(String accountNumber, String PIN) {
        try {
            FileReader fr = new FileReader("EncryptedData.csv");
            BufferedReader br = new BufferedReader(fr);
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                if (toHexString(getSHA(accountNumber)).equals(currentLine.split(",")[1])
                        && toHexString(getSHA(PIN)).equals(currentLine.split(",")[0])) {
                    br.close();
                    return true;
                }
            }
            br.close();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getBalance(String accountNumber) {
        BalanceEncryptionAndDecryption balanceEncryptionAndDecryption = new BalanceEncryptionAndDecryption();
        try {
            FileReader fr = new FileReader("EncryptedData.csv");
            BufferedReader encrypted = new BufferedReader(fr);
            String currentLine;
            while ((currentLine = encrypted.readLine()) != null) {
                if (currentLine.split(",")[1].equals(toHexString(getSHA(accountNumber)))) {
                    encrypted.close();
                    return Double.parseDouble(balanceEncryptionAndDecryption.decrypt(currentLine.split(",")[3]));
                }
            }
            encrypted.close();
        } catch (IOException | NumberFormatException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addBalance(String accountNumber, double amount) {

        double updatedAmount = this.getBalance(accountNumber) + amount;

        File encryptedOld = new File("./EncryptedData.csv");
        File dataOld = new File("./Data.csv");

        BalanceEncryptionAndDecryption balanceEncryptionAndDecryption = new BalanceEncryptionAndDecryption();
        try {
            FileReader fr = new FileReader("./EncryptedData.csv");
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter("./encryptedtemp.csv");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter encryptedWriter = new PrintWriter(bw);

            FileReader fr2 = new FileReader("./Data.csv");
            BufferedReader br2 = new BufferedReader(fr2);
            FileWriter fw2 = new FileWriter("./datatemp.csv");
            BufferedWriter bw2 = new BufferedWriter(fw2);
            PrintWriter dataWriter = new PrintWriter(bw2);

            String encryptedCurrentLine;
            String dataCurrentLine;
            while ((encryptedCurrentLine = br.readLine()) != null && (dataCurrentLine = br2.readLine()) != null) {
                String[] encryptedData = encryptedCurrentLine.split(",");
                String[] dataData = dataCurrentLine.split(",");
                if (encryptedData[1].equals(toHexString(getSHA(accountNumber)))) {
                    encryptedWriter.println(encryptedData[0] + "," + encryptedData[1] + "," + encryptedData[2] + ","
                            + balanceEncryptionAndDecryption.encrypt(String.valueOf(updatedAmount)) + ","
                            + encryptedData[4]);
                    dataWriter.println(dataData[0] + "," + dataData[1] + "," + dataData[2] + "," + updatedAmount + ","
                            + dataData[4]);
                } else {
                    encryptedWriter.println(encryptedCurrentLine);
                    dataWriter.println(dataCurrentLine);
                }
            }
            encryptedWriter.flush();
            dataWriter.flush();
            encryptedWriter.close();
            dataWriter.close();
            br.close();
            br2.close();
            File encryptedDump = new File("./encryptedtemp.csv");
            encryptedOld.delete();
            encryptedDump.renameTo(encryptedOld);
            File dataDump = new File("./datatemp.csv");
            dataOld.delete();
            dataDump.renameTo(dataOld);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void removeBalance(String accountNumber, double amount) {
        double updatedAmount = this.getBalance(accountNumber) - amount;
        if (updatedAmount >= 0) {
            File encryptedOld = new File("./EncryptedData.csv");
            File dataOld = new File("./Data.csv");

            BalanceEncryptionAndDecryption balanceEncryptionAndDecryption = new BalanceEncryptionAndDecryption();
            try {
                FileReader fr = new FileReader("./EncryptedData.csv");
                BufferedReader br = new BufferedReader(fr);
                FileWriter fw = new FileWriter("./encryptedtemp.csv");
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter encryptedWriter = new PrintWriter(bw);

                FileReader fr2 = new FileReader("./Data.csv");
                BufferedReader br2 = new BufferedReader(fr2);
                FileWriter fw2 = new FileWriter("./datatemp.csv");
                BufferedWriter bw2 = new BufferedWriter(fw2);
                PrintWriter dataWriter = new PrintWriter(bw2);

                String encryptedCurrentLine;
                String dataCurrentLine;
                while ((encryptedCurrentLine = br.readLine()) != null && (dataCurrentLine = br2.readLine()) != null) {
                    String[] encryptedData = encryptedCurrentLine.split(",");
                    String[] dataData = dataCurrentLine.split(",");
                    if (encryptedData[1].equals(toHexString(getSHA(accountNumber)))) {
                        encryptedWriter.println(encryptedData[0] + "," + encryptedData[1] + "," + encryptedData[2] + ","
                                + balanceEncryptionAndDecryption.encrypt(String.valueOf(updatedAmount)) + ","
                                + encryptedData[4]);
                        dataWriter.println(dataData[0] + "," + dataData[1] + "," + dataData[2] + "," + updatedAmount
                                + "," + dataData[4]);
                    } else {
                        encryptedWriter.println(encryptedCurrentLine);
                        dataWriter.println(dataCurrentLine);
                    }
                }
                encryptedWriter.flush();
                dataWriter.flush();
                encryptedWriter.close();
                dataWriter.close();
                br.close();
                br2.close();
                File encryptedDump = new File("./encryptedtemp.csv");
                encryptedOld.delete();
                encryptedDump.renameTo(encryptedOld);
                File dataDump = new File("./datatemp.csv");
                dataOld.delete();
                dataDump.renameTo(dataOld);
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else {
            System.out
                    .println("Insufficient balance! Please enter a value less than " + this.getBalance(accountNumber));
        }
    }

    public boolean isBlocked(String accountNumber) {
        try {
            FileReader fr = new FileReader("EncryptedData.csv");
            BufferedReader encrypted = new BufferedReader(fr);
            String currentLine;
            while ((currentLine = encrypted.readLine()) != null) {
                if (currentLine.split(",")[1].equals(toHexString(getSHA(accountNumber)))) {
                    encrypted.close();
                    return currentLine.split(",")[4].equals(toHexString(getSHA(String.valueOf(true))));
                }
            }
            encrypted.close();
        } catch (IOException | NumberFormatException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void blockAccount(String accountNumber) {
        File encryptedOld = new File("./EncryptedData.csv");
        File dataOld = new File("./Data.csv");
        try {
            FileReader fr = new FileReader("./EncryptedData.csv");
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter("./encryptedtemp.csv");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter encryptedWriter = new PrintWriter(bw);

            FileReader fr2 = new FileReader("./Data.csv");
            BufferedReader br2 = new BufferedReader(fr2);
            FileWriter fw2 = new FileWriter("./datatemp.csv");
            BufferedWriter bw2 = new BufferedWriter(fw2);
            PrintWriter dataWriter = new PrintWriter(bw2);

            String encryptedCurrentLine;
            String dataCurrentLine;
            while ((encryptedCurrentLine = br.readLine()) != null && (dataCurrentLine = br2.readLine()) != null) {
                String[] encryptedData = encryptedCurrentLine.split(",");
                String[] dataData = dataCurrentLine.split(",");
                if (encryptedData[1].equals(toHexString(getSHA(String.valueOf(accountNumber))))) {
                    encryptedWriter.println(encryptedData[0] + "," + encryptedData[1] + "," + encryptedData[2] + ","
                            + encryptedData[3] + "," + toHexString(getSHA(String.valueOf(true))));
                    dataWriter.println(
                            dataData[0] + "," + dataData[1] + "," + dataData[2] + "," + dataData[3] + "," + true);
                } else {
                    encryptedWriter.println(encryptedCurrentLine);
                    dataWriter.println(dataCurrentLine);
                }
            }
            encryptedWriter.flush();
            dataWriter.flush();
            encryptedWriter.close();
            dataWriter.close();
            br.close();
            br2.close();
            File encryptedDump = new File("./encryptedtemp.csv");
            encryptedOld.delete();
            encryptedDump.renameTo(encryptedOld);
            File dataDump = new File("./datatemp.csv");
            dataOld.delete();
            dataDump.renameTo(dataOld);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String accountNumber, int status) {
        if (status == 1) {
            System.out.println("Transaction successful. Your current balance is: " + getBalance(accountNumber));
        } else if (status == 2) {
            System.out.println("Transaction unsuccesful. No amount has been debited. Your current balance is: "
                    + getBalance(accountNumber));
        } else if (status == 3) {
            System.out.println(
                    "Your account has been blocked for security reasons. Please visit your nearest branch to unblock.");
        } else if (status == 4) {
            System.out.println(
                    "Your ATM PIN has been successfully changed. If this wasn't you, please send IT WASN'T ME to +91 1234567890 ASAP.");
        }
    }

    public byte[] getSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public void changePIN(String accountNumber) {
        String generatedOTP = String.format("%05d", ThreadLocalRandom.current().nextInt(0, 10000));
        Pattern pinPattern = Pattern.compile("[0-9]{5}");
        System.out.println("Your OTP is: " + generatedOTP); // this is sent as a message and not actually displayed on
                                                            // the screen
        System.out.print("Please enter the OTP: ");
        Scanner scanner = new Scanner(System.in);
        String OTP = scanner.next();
        if (OTP.equals(generatedOTP)) {
            System.out.print("Enter the new PIN containing 5 digits: ");
            String newPIN = scanner.next();
            while(!pinPattern.matcher(newPIN).matches()){
                System.out.print("Please enter a PIN containing 5 digits: ");
                newPIN = scanner.next();
            }
            System.out.print("Please re-enter your new PIN: ");
            String newPINTwo = scanner.next();
            while (!newPIN.equals(newPINTwo)) {
                System.out.println("The PIN's don't match. Please confirm again.");
                System.out.print("Enter the new PIN containing 5 digits: ");
                newPIN = scanner.next();
                System.out.print("Please re-enter your new PIN");
                newPINTwo = scanner.next();
            }
            File encryptedOld = new File("./EncryptedData.csv");
            File dataOld = new File("./Data.csv");
            try {
                FileReader fr = new FileReader("./EncryptedData.csv");
                BufferedReader br = new BufferedReader(fr);
                FileWriter fw = new FileWriter("./encryptedtemp.csv");
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter encryptedWriter = new PrintWriter(bw);

                FileReader fr2 = new FileReader("./Data.csv");
                BufferedReader br2 = new BufferedReader(fr2);
                FileWriter fw2 = new FileWriter("./datatemp.csv");
                BufferedWriter bw2 = new BufferedWriter(fw2);
                PrintWriter dataWriter = new PrintWriter(bw2);

                String encryptedCurrentLine;
                String dataCurrentLine;
                while ((encryptedCurrentLine = br.readLine()) != null && (dataCurrentLine = br2.readLine()) != null) {
                    String[] encryptedData = encryptedCurrentLine.split(",");
                    String[] dataData = dataCurrentLine.split(",");
                    if (encryptedData[1].equals(toHexString(getSHA(String.valueOf(accountNumber))))) {
                        encryptedWriter
                                .println(toHexString(getSHA(newPIN)) + "," + encryptedData[1] + "," + encryptedData[2]
                                        + "," + encryptedData[3] + "," + toHexString(getSHA(String.valueOf(false))));
                        dataWriter.println(
                                newPIN + "," + dataData[1] + "," + dataData[2] + "," + dataData[3] + "," + false);
                    } else {
                        encryptedWriter.println(encryptedCurrentLine);
                        dataWriter.println(dataCurrentLine);
                    }
                }
                encryptedWriter.flush();
                dataWriter.flush();
                encryptedWriter.close();
                dataWriter.close();
                br.close();
                br2.close();
                File encryptedDump = new File("./encryptedtemp.csv");
                encryptedOld.delete();
                encryptedDump.renameTo(encryptedOld);
                File dataDump = new File("./datatemp.csv");
                dataOld.delete();
                dataDump.renameTo(dataOld);
                System.out.println("PIN changed succesfully");
                sendMessage(accountNumber, 4);
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

}
