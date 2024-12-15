package oit.is.team7.quiz_7.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HasQuizMapper {
  @Select("SELECT * FROM hasQuiz WHERE roomID = #{roomID}")
  ArrayList<HasQuiz> selectHasQuizByRoomID(int roomID);

  @Select("SELECT * FROM hasQuiz WHERE quizID = #{quizID}")
  ArrayList<HasQuiz> selectHasQuizByQuizID(int quizID);

  @Select("SELECT * FROM hasQuiz WHERE roomID = #{roomID} ORDER BY index DESC LIMIT 1")
  HasQuiz maxIndexByRoomID(int roomID);

  @Insert("INSERT INTO hasQuiz (roomID, quizID, index) VALUES (#{roomID}, #{quizID}, #{index})")
  void insertHasQuiz(HasQuiz hasQuiz);

  @Delete("DELETE FROM hasQuiz WHERE roomID = #{roomID}")
  void deleteHasQuizByRoomID(int roomID);
}
