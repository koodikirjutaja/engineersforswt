package ee.ut.math.tvt.salessystem.users;

public class Admin extends Users{
    public Admin(String username, String userpassword) {
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
