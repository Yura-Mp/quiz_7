package oit.is.team7.quiz_7.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
// import org.apache.ibatis.annotations.Update;
// import org.apache.ibatis.annotations.Delete;

@Mapper
public interface UserAccountMapper {
  @Select("SELECT * FROM userAccount WHERE username = #{userName}")
  UserAccount selectUserAccountByUsername(String username);

  @Select("SELECT roles FROM userRoles WHERE id = #{id}")
  ArrayList<String> selectUserAccountRolesById(int id);

  @Insert("INSERT INTO userAccount (userName, pass) VALUES (#{userName}, #{pass})")
  @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
  void insertUserAccount(UserAccount userAccount);

  @Insert("INSERT INTO userRoles (id, roles) VALUES (#{id}, #{role})")
  void insertUserRole(int id, String role);
}
