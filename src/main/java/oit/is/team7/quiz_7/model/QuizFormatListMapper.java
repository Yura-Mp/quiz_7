package oit.is.team7.quiz_7.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface QuizFormatListMapper {
  @Select("SELECT * FROM QuizFormatList WHERE quizFormat = #{quizFormat}")
  QuizFormatList selectQuizFormatByFormat(String quizFormat);

  @Select("SELECT * FROM QuizFormatList WHERE ID = #{id}")
  QuizFormatList selectQuizFormatById(long id);
}
