package oit.is.team7.quiz_7.service;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;

import oit.is.team7.quiz_7.model.PublicGameRoom;
import oit.is.team7.quiz_7.model.QuizJson;
import oit.is.team7.quiz_7.model.QuizTable;
import oit.is.team7.quiz_7.model.QuizTableMapper;
import oit.is.team7.quiz_7.model.AnswerObj;
import oit.is.team7.quiz_7.model.AnswerObjImpl_4choices;
import oit.is.team7.quiz_7.model.AnswerData;
import oit.is.team7.quiz_7.model.GameRoomParticipant;
import oit.is.team7.quiz_7.model.PGameRoomManager;
import oit.is.team7.quiz_7.model.PGameRoomRankingEntryBean;

@Service
public class AsyncPGameRoomService {
  private final Logger logger = LoggerFactory.getLogger(AsyncPGameRoomService.class);

  @Autowired
  PGameRoomManager pGameRoomManager;

  @Autowired
  QuizTableMapper quizTableMapper;

  @Async
  public void asyncSendParticipantsList(SseEmitter emitter, PublicGameRoom pgroom) {
    logger.info("asyncSendParticipantsList called");
    final long roomID = pgroom.getGameRoomID();
    while (true) {
      pgroom = pGameRoomManager.getPublicGameRooms().get(roomID);
      if (pgroom == null || !pgroom.isOpen()) {
        break;
      }
      List<GameRoomParticipant> participants = new ArrayList<>(pgroom.getParticipants().values());

      try {
        String jsonData = new ObjectMapper().writeValueAsString(participants);
        logger.info("Sending event to emitter: " + emitter + " with data: " + jsonData);
        emitter.send(SseEmitter.event().name("participantsList").data(jsonData));
        logger.info("Event sent: " + jsonData);
        TimeUnit.MILLISECONDS.sleep(1000);
      } catch (Exception e) {
        logger.error("Error sending event: " + e.getMessage());
      }
    }
    emitter.complete();
  }

  @Async
  public void asyncSendAnswerList(SseEmitter emitter, PublicGameRoom pgroom, int quizID) {
    logger.info("asyncSendAnswerList called");
    double time = quizTableMapper.selectQuizTableByID(quizID).getTimelimit();

    while (true) {
      List<GameRoomParticipant> participants = new ArrayList<>(pgroom.getParticipants().values());
      logger.info("Participants list : " + participants);

      ObjectMapper mapper = new ObjectMapper();
      // JSが扱えるオブジェクトに変換
      List<AnswerData> answerDataList = new ArrayList<>();
      for (GameRoomParticipant participant : participants) {
        AnswerObjImpl_4choices ansObj = (AnswerObjImpl_4choices) participant.getAnswerContent();
        logger.info("AnswerObj: " + ansObj);
        AnswerData ansData = new AnswerData(participant, ansObj);
        logger.info("AnswerData: " + ansData);
        answerDataList.add(ansData);
      }

      try {
        String answerDataJSON = mapper.writeValueAsString(answerDataList);
        logger.info("AnswerData JSON: " + answerDataJSON);
        logger.info("Sending event to emitter: " + emitter + " with data: " + answerDataJSON);
        emitter.send(SseEmitter.event().name("answerData").data(answerDataJSON));
        if (time >= 0) {
          logger.info("Sending time: " + time);
          emitter.send(SseEmitter.event().name("countdown").data(time));
        }
        if (pgroom.isConfirmedResult()) {
          if (pgroom.getNextQuizIndex() >= pgroom.getQuizPool().size()) {
            emitter.send(SseEmitter.event().name("transition").data("toOverall"));
          } else {
            emitter.send(SseEmitter.event().name("transition").data("toRanking"));
          }
          break;
        }
        logger.info("Events sent");
        TimeUnit.MILLISECONDS.sleep(1000); // 1秒ごとに更新
        time--;
      } catch (Exception e) {
        logger.error("Error sending event: " + e.getMessage());
      }
    }
    emitter.complete();
  }

  @Async
  public void asyncSendPageTransition(SseEmitter emitter, PublicGameRoom pgroom) {
    logger.info("asyncSendPageTransition called with roomID: " + pgroom.getGameRoomID());
    final long roomID = pgroom.getGameRoomID();
    while (true) {
      pgroom = pGameRoomManager.getPublicGameRooms().get(roomID);
      if (pgroom == null) {
        break;
      }
      if (!pgroom.isOpen()) {
        try {
          emitter.send(SseEmitter.event().name("pageTransition").data("pageTransition"));
        } catch (Exception e) {
          logger.error("Error in asyncSendPageTransition(...): {}", e.getMessage());
        }
        break;
      }
      try {
        TimeUnit.MILLISECONDS.sleep(100L);
      } catch (Exception e) {
      }
    }
    emitter.complete();
  }

  @Async
  public void asyncAutoRedirectToAnswerPage(SseEmitter emitter, final long roomID, final long quizID) {
    logger.info("AsyncPGameRoomService.asyncAutoRedirectToAnswerPage is called with roomID: " + roomID + ", quizID: " + quizID);

    try {
      PublicGameRoom redirectToRoom = pGameRoomManager.getPublicGameRooms().get(roomID);

      emitter.send(SseEmitter.event().name("init")
          .data("SSE connection established. Ready to auto-redirect to Answer Page of roomID: " + roomID + ", quizID: " + quizID));
      while (true) {
        if (redirectToRoom.isAnswering()) {
          emitter.send("room=" + roomID + "&quiz=" + quizID);
          break;
        }

        TimeUnit.MILLISECONDS.sleep(100L);
      }
    } catch (Exception e) {
      logger.error("AsyncPGameRoomService.asyncAutoRedirectToAnswerPage Error: " + e.getClass().getName() + ":"
          + e.getMessage());
      emitter.completeWithError(e);
    } finally {
      try {
        TimeUnit.MILLISECONDS.sleep(100L);
      } catch (Exception e) {
      } // マージン
      emitter.complete();
    }
  }

  @Async
  public void cancelGameRoom(SseEmitter emitter, long roomID) {
    logger.info("cancelGameRoom called with roomID: " + roomID);
    while (true) {
      PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get(roomID);
      if (pgroom == null) {
        logger.info("notifyCancellation called with roomID: " + roomID);

        try {
          emitter.send(SseEmitter.event().data("gameCancelled"));
        } catch (Exception e) {
        }
        break;
      }
      if (!pgroom.isOpen()) {
        break;
      }
      try {
        TimeUnit.MILLISECONDS.sleep(500L);
      } catch (Exception e) {
      }
    }
    emitter.complete();
  }

  @Async
  public void asyncAutoRedirectToAnsResultPage(SseEmitter emitter, final long roomID, final long quizID) {
    logger.info("AsyncPGameRoomService.asyncAutoRedirectToAnsResultPage is called with roomID: " + roomID + ", quizID: "
        + quizID);

    try {
      PublicGameRoom redirectToRoom = pGameRoomManager.getPublicGameRooms().get(roomID);

      emitter.send(SseEmitter.event().name("init")
          .data("SSE connection established. Ready to auto-redirect to AnsResult Page of roomID: " + roomID
              + ", quizID: " + quizID));
      while (true) {
        if (redirectToRoom.isConfirmedResult()) {
          emitter.send("redirect");
          // emitter.send("room=" + roomID + "&quiz=" + quizID);
          break;
        }

        TimeUnit.MILLISECONDS.sleep(1000L);
      }
    } catch (Exception e) {
      logger.error("AsyncPGameRoomService.asyncAutoRedirectToAnsResultPage Error: " + e.getClass().getName() + ":"
          + e.getMessage());
      emitter.completeWithError(e);
    } finally {
      try {
        TimeUnit.MILLISECONDS.sleep(500L);
      } catch (Exception e) {
      } // マージン
      emitter.complete();
    }
  }

  @Async
  public void asyncTimeupGameProc(final long roomID) {
    PublicGameRoom targetRoom = pGameRoomManager.getPublicGameRooms().get(roomID);
    final long quizID = pGameRoomManager.getPublicGameRooms().get(roomID).getNextQuizID();
    final QuizTable quiz = quizTableMapper.selectQuizTableByID((int) quizID);
    final long timelimit_ms = (long) (quiz.getTimelimit() * 1000L);

    // 解答残り時間が0以下になったとき，またはその公開ゲームルームの全参加者の解答が完了したときまで以降の処理を待機．
    while (true) {
      if (targetRoom.getElapsedAnswerTime_ms() > timelimit_ms)
        break;

      int answeredNum = 0;
      for (GameRoomParticipant ptc : targetRoom.getParticipants().values()) {
        if (ptc.isAnswered())
          answeredNum++;
      }
      if (targetRoom.getParticipants().size() <= answeredNum)
        break;

      try {
        TimeUnit.MILLISECONDS.sleep(500L);
      } catch (Exception e) {
        logger.error("asyncTimeupGameProc Error: " + e.getClass().getName() + ":" + e.getMessage());
      }
    }

    // 解答終了(解答中フラグをfalseに)
    targetRoom.endAnswer();

    // 得点計算(4択クイズ) & ランキング更新
    ObjectMapper objectMapper = new ObjectMapper();
    String quizJsonString = quiz.getParsableQuizJSON();
    int correctNum = 0;
    try {
      QuizJson quizJson = objectMapper.readValue(quizJsonString, QuizJson.class);
      correctNum = quizJson.getCorrect();
    } catch (Exception e) {
      logger.error("Error at parsing quizJson: " + e.toString());
    }
    for (GameRoomParticipant ptc : targetRoom.getParticipants().values()) {
      long calcPoint = 0L;
      AnswerObj ans = ptc.getAnswerContent();

      if (ans != null && ((AnswerObjImpl_4choices) ans).getAnsValue() == correctNum) {
        calcPoint = (long) ((double) quiz.getPoint() * (1.0 - ((double) ptc.getAnswerTime_ms() / timelimit_ms)));
      } else {
        calcPoint = 0L;
      }

      ptc.setPointDiff(calcPoint);
      ptc.addPoint(calcPoint);

      PGameRoomRankingEntryBean ptc_entry = targetRoom.getRanking().getEntry(ptc.getUserID());
      ptc_entry.point = ptc.getPoint();
      targetRoom.getRanking().setEntry(ptc_entry);
    }
    targetRoom.getRanking().sortByPoint();

    // クイズインデックスのインクリメント
    targetRoom.incrementNextQuizIndex();

    // 結果確定フラグのセット
    targetRoom.confirmResult();

    return;
  }
}
