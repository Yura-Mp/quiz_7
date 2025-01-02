package oit.is.team7.quiz_7.model;

public class Gameroom {
  int id;
  int hostUserID;
  String roomName;
  String description;

  public int getID() {
    return id;
  }

  public void setID(int id) {
    this.id = id;
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

}
