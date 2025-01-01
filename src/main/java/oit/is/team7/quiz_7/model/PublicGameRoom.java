package oit.is.team7.quiz_7.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @classdesc
 *            単一の公開ゲームルームを成すクラス．
 *            詳しくは本サービスの最新のクラス設計図(quiz_7_ClassGraph_Isdev24_ver*.drawio)を参照．
 *
 */
public class PublicGameRoom {
  private long gameRoomID;
  private String gameRoomName;
  private long hostUserID;
  private String hostUserName;
  private LinkedHashMap<Long, GameRoomParticipant> participants;
  private int maxPlayers = 50;
  private ArrayList<Long> quizPool;
  private PGameRoomRanking ranking;
  private int nextQuizIndex;

  public PublicGameRoom(long gameRoomID, String gameRoomName, long hostUserID, String hostUserName, int maxPlayers,
      ArrayList<Long> quizPool) {
    this.gameRoomID = gameRoomID;
    this.gameRoomName = gameRoomName;
    this.hostUserID = hostUserID;
    this.hostUserName = hostUserName;
    this.participants = new LinkedHashMap<Long, GameRoomParticipant>();
    this.maxPlayers = maxPlayers;
    this.quizPool = quizPool;
    this.ranking = new PGameRoomRanking();
    this.nextQuizIndex = 0;
  }

  public long getGameRoomID() {
    return gameRoomID;
  }

  public String getGameRoomName() {
    return gameRoomName;
  }

  public long getHostUserID() {
    return hostUserID;
  }

  public String getHostUserName() {
    return hostUserName;
  }

  public LinkedHashMap<Long, GameRoomParticipant> getParticipants() {
    return participants;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public ArrayList<Long> getQuizPool() {
    return quizPool;
  }

  public PGameRoomRanking getRanking() {
    return ranking;
  }

  public int getNextQuizIndex() {
    return nextQuizIndex;
  }

  public void incrementNextQuizIndex() {
    this.nextQuizIndex++;
  }

  public void removeParticipant(long userID) {
      participants.remove(userID);
  }
}
