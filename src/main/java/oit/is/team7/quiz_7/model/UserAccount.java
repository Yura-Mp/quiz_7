package oit.is.team7.quiz_7.model;

/**
 * @class public class UserAccount
 * @author Yura-Mp (e1b22079)
 * @description UserAccount data java class.
 */
public class UserAccount {
  // schema.sql にデータの意味を記載．

  int id;
  String userName;
  String pass;
  String roles;
  boolean available;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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

  public String getRoles() {
    return roles;
  }

  public void setRoles(String roles) {
    this.roles = roles;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

}
