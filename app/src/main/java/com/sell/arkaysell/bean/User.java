package com.sell.arkaysell.bean;

/**
 * Created by arkayapps on 16/03/17.
 */

public class User {
    private String userID;
    private String email;
    private String profileImage;
    private String userDisplayName;
    private String actualPic;
    private String ActualName;
    private String countryCode;
    private String country;
    private String city;
    private String countyFlagURL;
    private boolean isAdsRemove = false;
    private long accountCreateDate;

    public User() {
    }


    public boolean isAdsRemove() {
        return isAdsRemove;
    }

    public void setAdsRemove(boolean adsRemove) {
        isAdsRemove = adsRemove;
    }

    public String getActualPic() {
        return actualPic;
    }

    public void setActualPic(String actualPic) {
        this.actualPic = actualPic;
    }

    public String getActualName() {
        return ActualName;
    }

    public void setActualName(String actualName) {
        ActualName = actualName;
    }

    public String getCountyFlagURL() {
        return countyFlagURL;
    }

    public void setCountyFlagURL(String countyFlagURL) {
        this.countyFlagURL = countyFlagURL;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public long getAccountCreateDate() {
        return accountCreateDate;
    }

    public void setAccountCreateDate(long accountCreateDate) {
        this.accountCreateDate = accountCreateDate;
    }
}
