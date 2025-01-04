package oit.is.team7.quiz_7.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    pgroom.addEmitter(emitter);
    try {
      // 初期メッセージを送信して接続が確立されたことを確認
      logger.info("Sending init message to roomID: " + roomID);
      emitter.send(SseEmitter.event().name("init").data("SSE connection established"));
      asyncPGRService.asyncSendAnswerList(pgroom, quizID);
    } catch (Exception e) {
      logger.error("Error in getAnswerList(...): {}", e.getMessage());
      emitter.completeWithError(e);
    }
    return emitter;
  }
}
