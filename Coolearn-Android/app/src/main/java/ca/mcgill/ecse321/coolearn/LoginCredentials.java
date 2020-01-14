package ca.mcgill.ecse321.coolearn;

/**
 * This class stores the data associated with a logged in user,
 * including their name and id.
 * @author group 11
 * @version 1.1
 */
public class LoginCredentials {
    private String userName;
    private int userRoleId;

    /**
     * Creates a set of LoginCredentials representing
     * a user who has not yet logged in (ie, has no name or id)
     */
    public LoginCredentials() {
        userName = null;
        userRoleId = -1;
    }

    /**
     * Returns the name of the user
     * @return the name of the user
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns the UserRole id of the user
     * @return the UserRole id of the suer
     */
    public int getUserRoleId() {
        return userRoleId;
    }

    /**
     * Sets the user's name
     * @param userName the name of the user
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Sets the user's id
     * @param i the user's id
     */
    public void setUserRoleId(int i) {
        this.userRoleId = i;
    }

    /**
     * Returns a boolean telling whether or not a user has logged in
     * @return true if the user is logged in else false if no log in has occured
     */
    public boolean isLoggedIn() {
        return userName != null && userRoleId != -1;
    }

}
