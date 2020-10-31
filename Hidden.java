import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    @Override
    public boolean isCorrectPIN(String accountNumber, String PIN) {
        try {
            FileReader fr = new FileReader("./EncryptedData.csv");
            BufferedReader br = new BufferedReader(fr);
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                if (toHexString(getSHA(accountNumber)).equals(currentLine.split(",")[1])
                        && toHexString(getSHA(PIN)).equals(currentLine.split(",")[0])) {
                    return true;
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public double getBalance(String accountNumber) {
        BalanceEncryptionAndDecryption balanceEncryptionAndDecryption = new BalanceEncryptionAndDecryption();
        try {
            FileReader fr = new FileReader("./EncryptedData.csv");
            BufferedReader br = new BufferedReader(fr);
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                if(currentLine.split(",")[1].equals(toHexString(getSHA(accountNumber)))){
                    return Double.parseDouble(balanceEncryptionAndDecryption.decrypt(currentLine.split(",")[3]));
                }
            }
        } catch (IOException | NumberFormatException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void addBalance(String accountNumber, double amount) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeBalance(String accountNumber, double amount) {
        // TODO Auto-generated method stub

    }

    @Override
    public void blockAccount(String accountNumber) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendMessage(String account, int status) {
        // TODO Auto-generated method stub

    }

    @Override
    public double deCryptBalance(String key) {
        // TODO Auto-generated method stub
        return 0;
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