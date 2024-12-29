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
  public String getRanking(@RequestParam("room") long roomID, Principal prin, ModelMap model) {
    // Test
    // 連動時に以下のコードを削除(or コメントアウト)．
    PGameRoomRanking testRanking = new PGameRoomRanking();

    testRanking.addEntry(new PGameRoomRankingEntryBean(-1L, "Test1", 100000L));
    testRanking.addEntry(new PGameRoomRankingEntryBean(-2L, "Test2", 55400L));
    testRanking.addEntry(new PGameRoomRankingEntryBean(-3L, "Test3", 184000L));
    testRanking.addEntry(new PGameRoomRankingEntryBean(-4L, "Test4", 8000L));
    testRanking.addEntry(new PGameRoomRankingEntryBean(-5L, "Test5", 250000L));

    testRanking.sortByPoint();

    model.addAttribute("ranking", testRanking.getRanking());

    if(true) return "/playing/ranking.html";
    // Test END


    Gameroom targetGameroom = gameroomMapper.selectGameroomByID((int)roomID);

    PublicGameRoom targetPGameRoom = pGameRoomManager.getPublicGameRooms().get(roomID);
    // 公開ゲームルームがない場合:
    if(targetPGameRoom == null) {
      return "/playing/ranking.html";
    }

    PGameRoomRanking targetRanking = targetPGameRoom.getRanking();

    targetRanking.sortByPoint();

    model.addAttribute("gameroom", targetGameroom);
    model.addAttribute("ranking", targetRanking.getRanking());

    return "/playing/ranking.html";
  }
}
