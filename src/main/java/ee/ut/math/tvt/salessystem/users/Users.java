package ee.ut.math.tvt.salessystem.users;

public abstract class Users {
    private String username;
    private String userpassword;

    public Users(String username, String userpassword) {
        this.username = username;
        this.userpassword = userpassword;
    }
    public abstract boolean login();
    public abstract void logout();

}



