package oit.is.team7.quiz_7.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface QuizTableMapper {
  @Select("SELECT * FROM quizTable WHERE ID = #{ID}")
  QuizTable selectQuizTableByID(int ID);

  @Select("SELECT * FROM quizTable WHERE authorID = #{authorID}")
  ArrayList<QuizTable> selectQuizTableByAuthorID(int authorID);

  @Insert("INSERT INTO quizTable (quizFormatID, authorID, title, description, quizJSON) VALUES (#{quizFormatID}, #{authorID}, #{title}, #{description}, #{quizJSON})")
  @Options(useGeneratedKeys = true, keyColumn = "ID", keyProperty = "ID")
  void insertQuizTable(QuizTable quizTable);

  @Delete("DELETE FROM quizTable WHERE ID = #{ID}")
  void deleteQuizTableByID(int ID);
}
