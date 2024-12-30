package oit.is.team7.quiz_7.typehandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

public class JsonNodeTypeHandler extends BaseTypeHandler<JsonNode> {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, JsonNode parameter, JdbcType jdbcType)
      throws SQLException {
    // JsonNodeを文字列に変換してセット
    ps.setString(i, parameter.toString());
  }

  @Override
  public JsonNode getNullableResult(ResultSet rs, String columnName) throws SQLException {
    // JSON文字列をJsonNodeに変換
    String json = rs.getString(columnName);
    return json == null ? null : parseJson(json);
  }

  @Override
  public JsonNode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    // JSON文字列をJsonNodeに変換
    String json = rs.getString(columnIndex);
    return json == null ? null : parseJson(json);
  }

  @Override
  public JsonNode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    // JSON文字列をJsonNodeに変換
    String json = cs.getString(columnIndex);
    return json == null ? null : parseJson(json);
  }

  private JsonNode parseJson(String json) {
    try {
      return objectMapper.readTree(json);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse JSON string to JsonNode", e);
    }
  }
}
