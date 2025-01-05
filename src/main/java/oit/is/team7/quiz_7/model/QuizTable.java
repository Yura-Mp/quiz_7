package oit.is.team7.quiz_7.model;

import com.fasterxml.jackson.databind.JsonNode;

import oit.is.team7.quiz_7.utils.JsonUtils;

public class QuizTable {
  int ID;
  int quizFormatID;
  int authorID;
  String title;
  String description;
  double timelimit;
  int point;

  JsonNode quizJSON;

  public int getID() {
    return ID;
  }

  public void setID(int iD) {
    ID = iD;
  }

  public int getQuizFormatID() {
    return quizFormatID;
  }

  public void setQuizFormatID(int quizFormatID) {
    this.quizFormatID = quizFormatID;
  }

  public int getAuthorID() {
    return authorID;
  }

  public void setAuthorID(int authorID) {
    this.authorID = authorID;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public double getTimelimit() {
    return timelimit;
  }

  public void setTimelimit(double timelimit) {
    this.timelimit = timelimit;
  }

  public int getPoint() {
    return point;
  }

  public void setPoint(int point) {
    this.point = point;
  }

  public String getQuizJSON() {
    return JsonUtils.parseJsonNodeToString(this.quizJSON);
  }

  public void setQuizJSON(JsonNode quizJSON) {
    this.quizJSON = quizJSON;
  }

}
