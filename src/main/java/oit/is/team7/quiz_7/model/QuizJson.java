package oit.is.team7.quiz_7.model;

public class QuizJson {
  public int correct;
  public String[] choices;

  public int getCorrect() {
    return correct;
  }

  public void setCorrect(int correct) {
    this.correct = correct;
  }

  public String[] getChoices() {
    return choices;
  }

  public void setChoices(String[] choices) {
    this.choices = choices;
  }
}
