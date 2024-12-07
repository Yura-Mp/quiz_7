package oit.is.team7.quiz_7.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GameroomMapper {
  @Select("SELECT * FROM gameroom WHERE ID = #{ID}")
  Gameroom selectGameroomByID(int ID);

  @Select("SELECT * FROM gameroom WHERE hostUserID = #{hostUserID}")
  ArrayList<Gameroom> selectGameroomByHostUserID(int hostUserID);

  @Update("UPDATE gameroom SET published = #{published} WHERE ID = #{ID}")
  void updatePublishedByID(int ID, boolean published);

  @Insert("INSERT INTO gameroom (hostUserID, roomName, description) VALUES (#{hostUserID}, #{roomName}, #{description})")
  @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
  void insertGameroom(Gameroom gameroom);

  @Delete("DELETE FROM gameroom WHERE ID = #{ID}")
  void deleteGameroomByID(int ID);
}
