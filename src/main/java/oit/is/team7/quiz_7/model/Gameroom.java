package oit.is.team7.quiz_7.model;

public class Gameroom {
  int ID;
  int hostUserID;
  String roomName;
  String description;
  boolean published;

  public int getID() {
    return ID;
  }

  public void setID(int iD) {
    ID = iD;
  }

  public int getHostUserID() {
    return hostUserID;
  }

  public void setHostUserID(int hostUserID) {
    this.hostUserID = hostUserID;
  }

  public String getRoomName() {
    return roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isPublished() {
    return published;
  }

  public void setPublished(boolean published) {
    this.published = published;
  }

}
