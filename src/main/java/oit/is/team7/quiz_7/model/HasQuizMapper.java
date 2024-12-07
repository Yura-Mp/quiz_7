package oit.is.team7.quiz_7.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HasQuizMapper {
  @Select("SELECT * FROM hasQuiz WHERE roomID = #{roomID}")
  ArrayList<HasQuiz> selectHasQuizByRoomID(int roomID);

  @Select("SELECT * FROM hasQuiz WHERE quizID = #{quizID}")
  ArrayList<HasQuiz> selectHasQuizByQuizID(int quizID);

  @Delete("DELETE FROM hasQuiz WHERE roomID = #{roomID}")
  void deleteHasQuizByRoomID(int roomID);
}
