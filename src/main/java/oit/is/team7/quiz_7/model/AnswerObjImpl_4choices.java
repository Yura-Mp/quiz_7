package oit.is.team7.quiz_7.model;

public class AnswerObjImpl_4choices implements AnswerObj {
  private int ansValue;

  public AnswerObjImpl_4choices(int ansValue) {
    this.ansValue = ansValue;
  }

  @Override
  public Integer getAnsValue() {
    return this.ansValue;
  }

  public void setAnsValue(int ansValue) {
    this.ansValue = ansValue;
  }
}
