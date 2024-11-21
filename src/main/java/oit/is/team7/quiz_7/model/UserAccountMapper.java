package oit.is.team7.quiz_7.model;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
// import org.apache.ibatis.annotations.Update;
// import org.apache.ibatis.annotations.Delete;

@Mapper
public interface UserAccountMapper {
  @Select("SELECT * FROM userAccount WHERE username = #{userName}")
  UserAccount selectByUsername(String username);

  @Insert("INSERT INTO userAccount (userName, pass, roles) VALUES (#{userName}, #{pass}, #{roles})")
  @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
  void insertUserAccount(UserAccount userAccount);
}
