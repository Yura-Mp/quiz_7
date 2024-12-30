package oit.is.team7.quiz_7.controller;

import java.security.Principal;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;
import oit.is.team7.quiz_7.model.PGameRoomManager;
import oit.is.team7.quiz_7.model.PublicGameRoom;
import oit.is.team7.quiz_7.model.QuizJson;
import oit.is.team7.quiz_7.model.QuizTable;
import oit.is.team7.quiz_7.model.QuizTableMapper;
import oit.is.team7.quiz_7.utils.JsonUtils;

@Controller
@RequestMapping("/playing")
public class PlayingController {
  private final Logger logger = (Logger) LoggerFactory.getLogger(PlayingController.class);

  @Autowired
  PGameRoomManager pGameRoomManager;

  @Autowired
  QuizTableMapper quizTableMapper;

  public String get_wait_guest(Principal prin, ModelMap model) {
    return "/playing/guest/wait.html";
  }

  @GetMapping("/wait")
  public String get_wait_host(@RequestParam("room") int roomID, Principal prin, ModelMap model) {
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get((long) roomID);
    if (!pgroom.getHostUserName().equals(prin.getName())) {
      // ユーザがホストでなければゲスト向けの待機画面を表示
      return get_wait_guest(prin, model);
    }
    model.addAttribute("pgameroom", pgroom);
    ArrayList<QuizTable> quizList = new ArrayList<QuizTable>();
    for (long quizID : pgroom.getQuizPool()) {
      quizList.add(quizTableMapper.selectQuizTableByID((int) quizID));
    }
    model.addAttribute("quizList", quizList);
    long nextQuizID = pgroom.getQuizPool().get(pgroom.getNextQuizIndex());
    QuizTable nextQuiz = quizTableMapper.selectQuizTableByID((int) nextQuizID);
    model.addAttribute("nextQuiz", nextQuiz);
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode quizJsonNode = nextQuiz.getQuizJSON();
    String quizJsonString = JsonUtils.parseJsonNodeToString(quizJsonNode);
    try {
      QuizJson quizJson = objectMapper.readValue(quizJsonString, QuizJson.class);
      model.addAttribute("nextQuizJson", quizJson);
    } catch (Exception e) {
      logger.error("Error at parsing quizJson: " + e.toString());
    }
    return "/playing/host/wait.html";
  }

  @GetMapping("/ranking")
  public String getRanking() {
    return "/playing/ranking.html";
  }

}
