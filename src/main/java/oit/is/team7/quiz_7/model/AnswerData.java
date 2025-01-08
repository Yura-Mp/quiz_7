package oit.is.team7.quiz_7.model;

public class AnswerData {
  private String userName;
  private int answerContent;
  private long answerTime_ms;

  public AnswerData(GameRoomParticipant participant, AnswerObjImpl_4choices answerObj) {
    this.userName = participant.getUserName();
    this.answerContent = (int) answerObj.getAnsValue();
    this.answerTime_ms = participant.getAnswerTime_ms();
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Integer getAnswerContent() {
    return answerContent;
  }

  public void setAnswerContent(Integer answerContent) {
    this.answerContent = answerContent;
  }

  public long getAnswerTime_ms() {
    return answerTime_ms;
  }

  public void setAnswerTime_ms(long answerTime) {
    this.answerTime_ms = answerTime;
  }

}
