/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;



public class SessionManager {
    private static String loggedInEmail;

    // Set the email of the currently logged-in admin
    public static void setLoggedInEmail(String email) {
        loggedInEmail = email;
    }

    // Get the email of the currently logged-in admin
    public static String getLoggedInEmail() {
        return loggedInEmail;
    }

    // Clear session (optional use during logout)
    public static void clearSession() {
        loggedInEmail = null;
    }
}

