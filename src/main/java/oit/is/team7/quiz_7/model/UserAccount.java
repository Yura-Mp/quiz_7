package oit.is.team7.quiz_7.model;

/**
 * @class public class UserAccount
 * @author Yura-Mp (e1b22079)
 * @description UserAccount data java class.
 */
public class UserAccount {
  // schema.sql にデータの意味を記載．
  String userId;
  String userName;
  String pass;
  boolean available;

  public UserAccount() {

  }

  public UserAccount(String userId, String userName, String pass, boolean available) {
    this.userId = userId;
    this.userName = userName;
    this.pass = pass;
    this.available = available;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPass() {
    return pass;
  }

  public void setPass(String pass) {
    this.pass = pass;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }
}
