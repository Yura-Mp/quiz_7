package oit.is.team7.quiz_7.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.team7.quiz_7.model.Gameroom;
import oit.is.team7.quiz_7.model.GameroomMapper;
import oit.is.team7.quiz_7.model.PGameRoomManager;
import oit.is.team7.quiz_7.model.PGameRoomRanking;
import oit.is.team7.quiz_7.model.PGameRoomRankingEntryBean;
import oit.is.team7.quiz_7.model.PublicGameRoom;
import oit.is.team7.quiz_7.model.PublicGameRoomBean;


@Controller
@RequestMapping("/playing")
public class PlayingController {
  @Autowired
  GameroomMapper gameroomMapper;
  @Autowired
  PGameRoomManager pGameRoomManager;

  @GetMapping("/wait")
  public String get_wait(Principal prin, ModelMap model) {
    return "/playing/wait.html";
  }

  @GetMapping("/ranking")
  public String getRanking(@RequestParam(name = "room", required = false) Long roomID, @RequestParam(name = "user", required = false) Long userID, Principal prin, ModelMap model) {
    // 返り値となるtemplatesが固定的であるため定数化．
    final String RETURN_TEMPLATE = "/playing/ranking.html";

    // [打ち切り] ルームIDが指定されていない場合，テストページを表示．
    if(roomID == null) {
      return RETURN_TEMPLATE;
    }

    // Test 連動時に以下のコードを削除(or コメントアウト)．
    //
    PGameRoomRanking testRanking = new PGameRoomRanking();

    testRanking.addEntry(new PGameRoomRankingEntryBean(-1L, "Test1", 100000L));
    testRanking.addEntry(new PGameRoomRankingEntryBean(-2L, "Test2", 55400L));
    testRanking.addEntry(new PGameRoomRankingEntryBean(-3L, "Test3", 184000L));
    testRanking.addEntry(new PGameRoomRankingEntryBean(-4L, "Test4", 8000L));
    testRanking.addEntry(new PGameRoomRankingEntryBean(-5L, "Test5", 250000L));
    testRanking.addEntry(new PGameRoomRankingEntryBean(-6L, "Test6", 55400L));
    testRanking.addEntry(new PGameRoomRankingEntryBean(-7L, "--------------------", 100000000L));

    testRanking.sortByPoint();

    model.addAttribute("ranking", testRanking.getRanking());

    // [打ち切り] ユーザIDが指定されていない場合，普通のランキングを表示．
    if(userID == null) {
      return RETURN_TEMPLATE;
    }

    if(testRanking.getIndexesByUserID().get(userID) == null) {
      return RETURN_TEMPLATE;
    }

    PGameRoomRankingEntryBean testUser =  testRanking.getRanking().get(testRanking.getIndexesByUserID().get(userID));
    model.addAttribute("yourID", testUser.ID);
    model.addAttribute("yourRank", testUser.rank);
    model.addAttribute("yourPoint", testUser.point);

    if(true) {
      return RETURN_TEMPLATE;
    }
    //
    // Test END


    Gameroom targetGameroom = gameroomMapper.selectGameroomByID(roomID.intValue());

    PublicGameRoom targetPGameRoom = pGameRoomManager.getPublicGameRooms().get(roomID);
    // 公開ゲームルームがない場合:
    if(targetPGameRoom == null) {
      return "/playing/ranking.html";
    }

    PGameRoomRanking targetRanking = targetPGameRoom.getRanking();

    targetRanking.sortByPoint();

    model.addAttribute("gameroom", targetGameroom);
    model.addAttribute("ranking", targetRanking.getRanking());

    // [打ち切り] ユーザIDが指定されていない場合，普通のランキングを表示．
    if(userID == null) {
      return RETURN_TEMPLATE;
    }

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
