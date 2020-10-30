interface HiddenMechanism {
    boolean isCorrectPIN(String accountNumber, int PIN);

    double getBalance(String accountNumber);

    void addBalance(String accountNumber, double amount);

    void removeBalance(String accountNumber, double amount);

    void blockAccount(String accountNumber);

    void sendMessage(String account, int status);
}

public class Hidden implements HiddenMechanism {
    protected int numIncorrectPINAttempts = 0;
    @Override
    public boolean isCorrectPIN(String accountNumber, int PIN) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public double getBalance(String accountNumber) {
        // TODO Auto-generated method stub
        return 0;
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

}