package oit.is.team7.quiz_7.controller;

import java.security.Principal;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.team7.quiz_7.model.Gameroom;
import oit.is.team7.quiz_7.model.GameroomMapper;
import oit.is.team7.quiz_7.model.GameRoomParticipant;
import oit.is.team7.quiz_7.model.PGameRoomManager;
import oit.is.team7.quiz_7.model.PublicGameRoom;
import oit.is.team7.quiz_7.model.UserAccountMapper;
import oit.is.team7.quiz_7.service.AsyncPGameRoomService;

@Controller
@RequestMapping("/playgame")
public class PlaygameController {
  private final boolean DBG = true;
  private final Logger logger = LoggerFactory.getLogger(PlaygameController.class);

  @Autowired
  PGameRoomManager pGameRoomManager;

  @Autowired
  GameroomMapper gameroomMapper;

  @Autowired
  UserAccountMapper userAccountMapper;

  @Autowired
  AsyncPGameRoomService asyncPGRService;

  @Autowired
  SseController sseController;

  @GetMapping
  public String playgame(ModelMap model) {
    ArrayList<PublicGameRoom> publicGameRoomList = pGameRoomManager.getOpenPublicGameRoomList();
    model.addAttribute("publicGameroomList", publicGameRoomList);
    logger.info("PGRManager.pgrs:" + this.pGameRoomManager.getPublicGameRooms());
    return "playgame/playgame.html";
  }

  @GetMapping("/join_check")
  public String getJoinCheck(@RequestParam("room") long roomID, Principal principal, ModelMap model) {
    PublicGameRoom targetPRoom = this.pGameRoomManager.getPublicGameRooms().get(roomID);

    if (targetPRoom == null) {
      logger.warn("PlaygameController.getJoinCheck(...): PublicGameRoom #%d is null. ", roomID);
    }

    Gameroom targetRoom = gameroomMapper.selectGameroomByID((int) roomID);

    model.addAttribute("pgameroom", targetPRoom);
    model.addAttribute("gameroom", targetRoom);
    logger.info("PlaygameController.getJoinCheck(...): Called. ");
    return "playgame/join_check.html";
  }

  @PostMapping("/join_check")
  public String postJoinCheck(@RequestParam("room") long roomID, Principal principal) {
    // roomIDに対応する公開ゲームルームがない場合(エラー)
    if (pGameRoomManager.getPublicGameRooms().get(roomID) == null) {
      logger.warn(String
          .format("PlaygameController.postJoinCheck(...): Requested NOT exist PublicGameRoom by roomID:%d", roomID));
      return "";
    }

    GameRoomParticipant newParticipant = new GameRoomParticipant();
    long userID = userAccountMapper.selectUserAccountByUsername(principal.getName()).getId();
    newParticipant.setUserID(userID);
    newParticipant.setUserName(principal.getName());

    // 参加者を追加
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get(roomID);
    pgroom.addParticipant(newParticipant);
    logger.info("PlaygameController.postJoinCheck(...): Participants updated.");

    if (DBG) {
      PublicGameRoom checkedRoom = pGameRoomManager.getPublicGameRooms().get(roomID);
      GameRoomParticipant subject = checkedRoom.getParticipants().get(userID);

      logger.info(String.format(
          "[DBG] PlaygameController.postJoinCheck(...): Result of put participants(GameRoomParticipant). userID: %d, subject: "
              + subject,
          userID));
    }

    pGameRoomManager.addParticipantToBelonging(userID, roomID);
    if (DBG) {
      logger.info(String.format(
          "[DBG] PlaygameController.postJoinCheck(...): Result of put belonging(gameRoomID(long)). userID: %d, subject: "
              + pGameRoomManager.getBelonging().get(userID),
          userID));
    }

    return "redirect:/playgame/standby?room=" + roomID;
  }

  @GetMapping("/standby")
  public String standby(@RequestParam("room") int roomID, ModelMap model) {
    model.addAttribute("gameroom", gameroomMapper.selectGameroomByID(roomID));
    PublicGameRoom publicGameRoom = this.pGameRoomManager.getPublicGameRooms().get((long) roomID);
    model.addAttribute("pgameroom", publicGameRoom);
    return "playgame/standby.html";
  }

  @PostMapping("/standby/exit")
  public String exitGameRoom(@RequestParam("room") long roomID, Principal principal) {
    long userID = userAccountMapper.selectUserAccountByUsername(principal.getName()).getId();
    pGameRoomManager.removeParticipantFromBelonging(userID);
    // 参加者を削除
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get(roomID);
    pgroom.removeParticipant(userID);
    return "redirect:/playgame";
  }
}
