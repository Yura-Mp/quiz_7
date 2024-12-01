package oit.is.team7.quiz_7.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface QuizTableMapper {
  @Select("SELECT * FROM quizTable WHERE ID = #{ID}")
  QuizTable selectQuizTableByID(int ID);

  @Select("SELECT * FROM quizTable WHERE authorID = #{authorID}")
  ArrayList<QuizTable> selectQuizTableByAuthorID(int authorID);

  @Delete("DELETE FROM quizTable WHERE ID = #{ID}")
  void deleteQuizTableByID(int ID);
}
