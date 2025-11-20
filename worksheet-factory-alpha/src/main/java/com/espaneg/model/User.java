package com.espaneg.model;

// ==================================================================================
//                                 DATA OBJECT
// ==================================================================================

public class User {
    private final String fullName;
    private final String email;
    private final String mobileNumber;
    private final String password;

    public User(String fullName, String email, String mobileNumber, String password) {
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.password = password;
    }

    public String getFullName() {
        return fullName; }
    public String getEmail() {
        return email; }
    public String getMobileNumber() {
        return mobileNumber; }
    public String getPassword() {
        return password; }
}
