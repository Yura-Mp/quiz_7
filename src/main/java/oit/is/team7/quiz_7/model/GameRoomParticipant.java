package oit.is.team7.quiz_7.model;

/**
 * @classdesc
 *            単一のゲームルームの参加者を成すクラス．
 *            詳しくは本サービスの最新のクラス設計図(quiz_7_ClassGraph_Isdev24_ver*.drawio)を参照．
 *
 */
public class GameRoomParticipant {
  private long userID;
  private String userName;
  private long point = 0L;
  private boolean answered;
  private double answerTime;
  private AnswerObj answerContent;

  public void setUserID(long userID) {
    this.userID = userID;
  }

  public long getUserID() {
    return userID;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public long getPoint() {
    return point;
  }

  public void resetForNewGame() {
    this.point = 0L;
    this.answered = false;
    this.answerTime = 0.0;
    this.answerContent = null;
  }
}
