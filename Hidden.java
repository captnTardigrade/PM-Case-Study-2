import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

interface HiddenMechanism {
    boolean isCorrectPIN(String accountNumber, String PIN);

    double getBalance(String accountNumber);

    void addBalance(String accountNumber, double amount);

    void removeBalance(String accountNumber, double amount);

    void blockAccount(String accountNumber);

    void sendMessage(String account, int status);

    double deCryptBalance(String key);
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

    public String getData(String accountNumber) {
        try {
            FileReader fr = new FileReader("EncryptedData.csv");
            BufferedReader encrypted = new BufferedReader(fr);
            String currentLine;
            while ((currentLine = encrypted.readLine()) != null) {
                if (currentLine.split(",")[1].equals(toHexString(getSHA(accountNumber)))) {
                    encrypted.close();
                    return currentLine;
                }
            }
            encrypted.close();
        } catch (IOException | NumberFormatException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
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
            PrintWriter encryptedReader = new PrintWriter(bw);

            FileReader fr2 = new FileReader("./Data.csv");
            BufferedReader br2 = new BufferedReader(fr2);
            FileWriter fw2 = new FileWriter("./datatemp.csv");
            BufferedWriter bw2 = new BufferedWriter(fw2);
            PrintWriter dataReader = new PrintWriter(bw2);
            
            String encryptedCurrentLine;
            String dataCurrentLine;
            while ((encryptedCurrentLine = br.readLine()) != null && (dataCurrentLine = br2.readLine()) != null) {
                String[] encryptedData = encryptedCurrentLine.split(",");
                String[] dataData = dataCurrentLine.split(",");
                if (encryptedData[1].equals(toHexString(getSHA(accountNumber)))) {
                    encryptedReader.println(encryptedData[0] + "," + encryptedData[1] + "," + encryptedData[2] + ","
                            + balanceEncryptionAndDecryption.encrypt(String.valueOf(updatedAmount)));
                    dataReader.println(dataData[0] + "," + dataData[1] + "," + dataData[2] + "," + updatedAmount);
                } else {
                    encryptedReader.println(encryptedCurrentLine);
                    dataReader.println(dataCurrentLine);
                }
            }
            encryptedReader.flush();
            dataReader.flush();
            encryptedReader.close();
            dataReader.close();
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
    }

    public void blockAccount(String accountNumber) {
    }

    public void sendMessage(String account, int status) {
    }

    public double deCryptBalance(String encrypted) {
        BalanceEncryptionAndDecryption balanceEncryptionAndDecryption = new BalanceEncryptionAndDecryption();
        return Double.parseDouble(balanceEncryptionAndDecryption.decrypt(encrypted));
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

}