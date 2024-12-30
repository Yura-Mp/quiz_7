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
import oit.is.team7.quiz_7.model.Gameroom;
import oit.is.team7.quiz_7.model.GameroomMapper;
import oit.is.team7.quiz_7.model.PGameRoomManager;
import oit.is.team7.quiz_7.model.PublicGameRoom;
import oit.is.team7.quiz_7.model.PublicGameRoomBean;
import oit.is.team7.quiz_7.model.PGameRoomRanking;
import oit.is.team7.quiz_7.model.PGameRoomRankingEntryBean;
import oit.is.team7.quiz_7.model.QuizJson;
import oit.is.team7.quiz_7.model.QuizTable;
import oit.is.team7.quiz_7.model.QuizTableMapper;
import oit.is.team7.quiz_7.utils.JsonUtils;

@Controller
@RequestMapping("/playing")
public class PlayingController {
  private final Logger logger = (Logger) LoggerFactory.getLogger(PlayingController.class);

  @Autowired
  GameroomMapper gameroomMapper;
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
  public String getRanking(@RequestParam(name = "room", required = false) Long roomID, @RequestParam(name = "user", required = false) Long userID, @RequestParam(name = "DBG", defaultValue = "false") final Boolean DBG_FLAG, Principal prin, ModelMap model) {
    // 返り値となるtemplatesが固定的であるため定数化．
    final String RETURN_TEMPLATE = "/playing/ranking.html";

    // [打ち切り] ルームIDが指定されていない場合，テストページを表示．
    if(roomID == null) {
      return RETURN_TEMPLATE;
    }

    // デバッグ用．
    if(DBG_FLAG) {
      Gameroom testGameroom = new Gameroom();
      testGameroom.setID(-1);
      testGameroom.setRoomName("デバッグ用");

      // [打ち切り] ルームIDが違う場合(-1じゃない場合)，対象のルームがないとしてテストページを表示．
      if(roomID != -1) {
        return RETURN_TEMPLATE;
      }

      PGameRoomRanking testRanking = new PGameRoomRanking();

      testRanking.addEntry(new PGameRoomRankingEntryBean(-1L, "Test1", 100000L));
      testRanking.addEntry(new PGameRoomRankingEntryBean(-2L, "Test2", 55400L));
      testRanking.addEntry(new PGameRoomRankingEntryBean(-3L, "Test3", 184000L));
      testRanking.addEntry(new PGameRoomRankingEntryBean(-4L, "Test4", 8000L));
      testRanking.addEntry(new PGameRoomRankingEntryBean(-5L, "Test5", 250000L));
      testRanking.addEntry(new PGameRoomRankingEntryBean(-6L, "Test6", 55400L));
      testRanking.addEntry(new PGameRoomRankingEntryBean(-7L, "--------------------", 100000000L));
      for(long i = -8L; i > -18L; i--) {
        testRanking.addEntry(new PGameRoomRankingEntryBean(i, "Test", 1000L));
      }

      testRanking.sortByPoint();

      model.addAttribute("gameroom", testGameroom);
      model.addAttribute("ranking", testRanking.getRanking());

      // [打ち切り] ユーザIDが指定されていない場合，普通のランキングを表示．
      if(userID == null) {
        return RETURN_TEMPLATE;
      }

      // [打ち切り] ランキングに指定のユーザIDのエントリがない場合，普通のランキングを表示．
      if(testRanking.getIndexesByUserID().get(userID) == null) {
        return RETURN_TEMPLATE;
      }

      PGameRoomRankingEntryBean testUser =  testRanking.getRanking().get(testRanking.getIndexesByUserID().get(userID));
      model.addAttribute("yourID", testUser.ID);
      model.addAttribute("yourRank", testUser.rank);
      model.addAttribute("yourPoint", testUser.point);

      return RETURN_TEMPLATE;
    }
    // デバッグ用 END．


    Gameroom targetGameroom = gameroomMapper.selectGameroomByID(roomID.intValue());

    PublicGameRoom targetPGameRoom = pGameRoomManager.getPublicGameRooms().get(roomID);
    // [打ち切り] 公開ゲームルームがない場合，対象のルームがないとしてテストページを表示．
    if(targetPGameRoom == null) {
      return RETURN_TEMPLATE;
    }

    PGameRoomRanking targetRanking = targetPGameRoom.getRanking();

    targetRanking.sortByPoint();

    model.addAttribute("gameroom", targetGameroom);
    model.addAttribute("ranking", targetRanking.getRanking());

    // [打ち切り] ユーザIDが指定されていない場合，普通のランキングを表示．
    if(userID == null) {
      return RETURN_TEMPLATE;
    }

    // [打ち切り] ランキングに指定のユーザIDのエントリがない場合，普通のランキングを表示．
    if(targetRanking.getIndexesByUserID().get(userID) == null) {
      return RETURN_TEMPLATE;
    }

    PGameRoomRankingEntryBean targetUser = targetRanking.getRanking().get(targetRanking.getIndexesByUserID().get(userID));
    model.addAttribute("yourID", targetUser.ID);
    model.addAttribute("yourRank", targetUser.rank);
    model.addAttribute("yourPoint", targetUser.point);

    // リクエストパラメータが全て正常に指定されている場合，ユーザを軸にしたランキングを表示．
    return RETURN_TEMPLATE;
  }
}
