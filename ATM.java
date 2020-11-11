import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

public class ATM {
	public static void main(String args[]) {
		// ATM atm = new ATM();
		// atm.populateDummyData();
		OptionsScreen optionsScreen = new OptionsScreen();
		optionsScreen.main(args);
	}

	void populateDummyData() {
		FileWriter fw;
		FileWriter fw2;
		try {
			fw = new FileWriter("EncryptedData.csv", true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);

			fw2 = new FileWriter("Data.csv", true);
			BufferedWriter bw2 = new BufferedWriter(fw2);
			PrintWriter pw2 = new PrintWriter(bw2);

			BalanceEncryptionAndDecryption balanceEncryptionAndDecryption = new BalanceEncryptionAndDecryption();

			for (int i = 0; i < 20; i++) {
				String PIN = String.format("%05d", ThreadLocalRandom.current().nextInt(0, 10000));
				String accountNumber = String.valueOf(ThreadLocalRandom.current().nextLong(100000000, 1000000000));
				String phoneNumber = String.valueOf(ThreadLocalRandom.current().nextLong(100000000, 1000000000))
						+ ThreadLocalRandom.current().nextInt(9);
				double balance = ThreadLocalRandom.current().nextDouble(1000, 1000000);
				pw.println(toHexString(getSHA(PIN)) + "," + toHexString(getSHA(accountNumber)) + ","
						+ toHexString(getSHA(phoneNumber)) + ","
						+ balanceEncryptionAndDecryption.encrypt(String.valueOf(balance)) + ","
						+ toHexString(getSHA(String.valueOf(false))));
				pw2.println(PIN + "," + accountNumber + "," + phoneNumber + "," + balance + "," + false);
				pw2.flush();
				pw.flush();
			}
			pw.close();
			pw2.close();
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return md.digest(input.getBytes(StandardCharsets.UTF_8));
	}

	public static String toHexString(byte[] hash) {
		BigInteger number = new BigInteger(1, hash);
		StringBuilder hexString = new StringBuilder(number.toString(16));
		while (hexString.length() < 32) {
			hexString.insert(0, '0');
		}
		return hexString.toString();
	}
}

// citation: https://www.geeksforgeeks.org/sha-256-hash-in-java/
