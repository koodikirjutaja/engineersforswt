package ee.ut.math.tvt.salessystem.users;

public class Cashier extends Users{
    public Cashier(String username, String userpassword) {
        super(username, userpassword);
    }

    @Override
    public boolean login() {
        return false;
    }

    @Override
    public void logout() {

    }
}
