package oit.is.team7.quiz_7.utils;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonUtils {

  public static String parseJsonNodeToString(JsonNode jsonNode) {
    if (jsonNode == null) {
      return null;
    }
    String jsonString = jsonNode.toString();
    return jsonString.substring(1, jsonString.length() - 1).replace("\\\"", "\"");
  }
}
