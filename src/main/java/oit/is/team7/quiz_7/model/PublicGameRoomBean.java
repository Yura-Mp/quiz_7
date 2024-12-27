package oit.is.team7.quiz_7.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @classdesc
 *            PublicGameRoomクラスのBean版クラス．
 *
 */
public class PublicGameRoomBean {
  long gameRoomID;
  String gameRoomName;
  long hostUserID;
  String hostUserName;
  LinkedHashMap<Long, GameRoomParticipant> participants;
  int maxPlayers;
  ArrayList<Long> quizPool;

  public PublicGameRoomBean() {

  }

  public long getGameRoomID() {
    return gameRoomID;
  }

  public void setGameRoomID(long gameRoomID) {
    this.gameRoomID = gameRoomID;
  }

  public String getGameRoomName() {
    return gameRoomName;
  }

  public void setGameRoomName(String gameRoomName) {
    this.gameRoomName = gameRoomName;
  }

  public long getHostUserID() {
    return hostUserID;
  }

  public void setHostUserID(long hostUserID) {
    this.hostUserID = hostUserID;
  }

  public String getHostUserName() {
    return hostUserName;
  }

  public void setHostUserName(String hostUserName) {
    this.hostUserName = hostUserName;
  }

  public LinkedHashMap<Long, GameRoomParticipant> getParticipants() {
    return participants;
  }

  public void setParticipants(LinkedHashMap<Long, GameRoomParticipant> participants) {
    this.participants = participants;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public void setMaxPlayers(int maxPlayers) {
    this.maxPlayers = maxPlayers;
  }

  public ArrayList<Long> getQuizPool() {
    return quizPool;
  }

  public void setQuizPool(ArrayList<Long> quizPool) {
    this.quizPool = quizPool;
  }
}
