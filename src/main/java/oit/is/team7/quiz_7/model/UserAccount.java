package oit.is.team7.quiz_7.model;

import java.util.ArrayList;

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
  boolean available;
  ArrayList<String> roles;

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

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public ArrayList<String> getRoles() {
    return roles;
  }

  public void setRoles(ArrayList<String> roles) {
    this.roles = roles;
  }
}
