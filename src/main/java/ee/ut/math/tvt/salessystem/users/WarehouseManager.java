package ee.ut.math.tvt.salessystem.users;

public class WarehouseManager extends Users{
    public WarehouseManager(String username, String userpassword) {
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
