package oit.is.team7.quiz_7.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.team7.quiz_7.model.UserAccountMapper;
import oit.is.team7.quiz_7.model.PGameRoomManager;
import oit.is.team7.quiz_7.model.PublicGameRoom;
import oit.is.team7.quiz_7.service.AsyncPGameRoomService;

@RestController
@RequestMapping("/sse")
public class SseController {
  private final Logger logger = LoggerFactory.getLogger(SseController.class);
  @Autowired
  AsyncPGameRoomService asyncPGRService;

  @Autowired
  PGameRoomManager pGameRoomManager;

  @Autowired
  UserAccountMapper userAccountMapper;

  // 参加者リストを取得するエンドポイント
  @GetMapping("/participantsList")
  public SseEmitter getParticipantsList(@RequestParam("room") long roomID) {
    logger.info("getParticipantsList called with roomID:" + roomID);
    final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get(roomID);
    if (pgroom == null) {
      logger.error("Room not found: {}", roomID);
      emitter.completeWithError(new Exception("Room not found"));
      return emitter;
    }
    pgroom.addEmitter(emitter);
    try {
      // 初期メッセージを送信して接続が確立されたことを確認
      logger.info("Sending init message to roomID: " + roomID);
      emitter.send(SseEmitter.event().name("init").data("SSE connection established"));
      asyncPGRService.asyncSendParticipantsList(pgroom);
    } catch (Exception e) {
      logger.error("Error in getParticipantsList(...): {}", e.getMessage());
      emitter.completeWithError(e);
    }
    return emitter;
  }

  // ページ遷移イベントを受け取るエンドポイント
  @GetMapping("/pageTransition")
  public SseEmitter pageTransition(@RequestParam("room") long roomID) {
    logger.info("pageTransition called with roomID:" + roomID);
    final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get(roomID);
    if (pgroom == null) {
      logger.error("Room not found: {}", roomID);
      emitter.completeWithError(new Exception("Room not found"));
      return emitter;
    }
    pgroom.addEmitter(emitter);
    try {
      // 初期メッセージを送信して接続が確立されたことを確認
      logger.info("Sending init message to roomID: " + roomID);
      emitter.send(SseEmitter.event().name("init").data("SSE connection established"));
      asyncPGRService.asyncSendPageTransition(pgroom);
    } catch (Exception e) {
      logger.error("Error in pageTransition(...): {}", e.getMessage());
      emitter.completeWithError(e);
    }
    return emitter;
  }

  // 解答ページ(/playing/answer)に強制遷移させるエンドポイント
  @GetMapping("/transitToAnswer")
  public SseEmitter transitToAnswer(Principal prin) {
    logger.info("SseController.transitToAnswer is called by user '" + prin.getName() + "'. ");

    final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    long userID = userAccountMapper.selectUserAccountByUsername(prin.getName()).getId();
    long roomID = pGameRoomManager.getBelonging().get(userID);

    asyncPGRService.asyncAutoRedirectToAnswerPage(emitter, roomID);

    return emitter;
  }
}
