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
// import oit.is.team7.quiz_7.model.UserAccountMapper;
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
    // if (pgroom == null) {
    //   logger.error("Room not found: {}", roomID);
    //   emitter.completeWithError(new Exception("Room not found"));
    //   return emitter;
    // }
    try {
      // 初期メッセージを送信して接続が確立されたことを確認
      logger.info("Sending init message to roomID: " + roomID);
      emitter.send(SseEmitter.event().name("init").data("SSE connection established"));
      asyncPGRService.asyncSendParticipantsList(emitter, pgroom);
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
    // if (pgroom == null) {
    //   logger.error("Room not found: {}", roomID);
    //   emitter.completeWithError(new Exception("Room not found"));
    //   return emitter;
    // }
    try {
      // 初期メッセージを送信して接続が確立されたことを確認
      logger.info("Sending init message to roomID: " + roomID);
      emitter.send(SseEmitter.event().name("init").data("SSE connection established"));
      asyncPGRService.asyncSendPageTransition(emitter, pgroom);
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
    final long userID = userAccountMapper.selectUserAccountByUsername(prin.getName()).getId();
    final long roomID = pGameRoomManager.getBelonging().get(userID);
    final long quizID = pGameRoomManager.getPublicGameRooms().get(roomID).getNextQuizID();

    asyncPGRService.asyncAutoRedirectToAnswerPage(emitter, roomID, quizID);

    return emitter;
  }

  // 解答状況を取得するエンドポイント
  @GetMapping("/answerList")
  public SseEmitter getAnswerList(@RequestParam("room") long roomID, @RequestParam("quiz") int quizID) {
    logger.info("getAnswerList called with roomID:" + roomID);
    final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get(roomID);
    if (pgroom == null) {
      logger.error("Room not found: {}", roomID);
      emitter.completeWithError(new Exception("Room not found"));
      return emitter;
    }
    try {
      // 初期メッセージを送信して接続が確立されたことを確認
      logger.info("Sending init message to roomID: " + roomID);
      emitter.send(SseEmitter.event().name("init").data("SSE connection established"));
      asyncPGRService.asyncSendAnswerList(emitter, pgroom, quizID);
    } catch (Exception e) {
      logger.error("Error in getAnswerList(...): {}", e.getMessage());
      emitter.completeWithError(e);
    }

    return emitter;
  }

  // 解答結果表示ページ(/playing/ans_result)に自動遷移させるエンドポイント
  @GetMapping("/transitToAnsResult")
  public SseEmitter transitToAnsResult(Principal prin) {
    logger.info("SseController.transitToAnsResult is called by user '" + prin.getName() + "'. ");

    final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    final long userID = userAccountMapper.selectUserAccountByUsername(prin.getName()).getId();
    final long roomID = pGameRoomManager.getBelonging().get(userID);

    asyncPGRService.asyncAutoRedirectToAnsResultPage(emitter, roomID);

    return emitter;
  }

  @GetMapping("/cancellation")
  public SseEmitter cancellation(@RequestParam("room") final long roomID) {
    final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    asyncPGRService.cancelGameRoom(emitter, roomID);
    return emitter;
  }
}
