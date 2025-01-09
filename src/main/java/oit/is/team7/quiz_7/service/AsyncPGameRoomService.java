package oit.is.team7.quiz_7.service;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Iterator;

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
  private final AtomicBoolean participantsUpdated = new AtomicBoolean(false);
  private Map<Long, Boolean> pageTransitionMap = new HashMap<>();

  @Autowired
  PGameRoomManager pGameRoomManager;

  @Autowired
  QuizTableMapper quizTableMapper;

  @Async
  public void asyncSendParticipantsList(PublicGameRoom pgroom) {
    logger.info("asyncSendParticipantsList called");
    try {
      while (true) {
        if (this.participantsUpdated.getAndSet(false)) {
          List<GameRoomParticipant> participants = new ArrayList<>(pgroom.getParticipants().values());
          logger.info("Participants list updated: " + participants);

          String jsonData = new ObjectMapper().writeValueAsString(participants);
          for(int i = 0; i < pgroom.getEmitters().size(); i++) {
            SseEmitter emitter = pgroom.getEmitters().get(i);

            try {
              logger.info("Sending event to emitter: " + emitter + " with data: " + jsonData);
              emitter.send(SseEmitter.event().name("participantsList").data(jsonData));
              logger.info("Event sent: " + jsonData);
            } catch (Exception e) {
              pgroom.removeEmitter(emitter);
              logger.error("Error sending event: " + e.getMessage());
            }
          }

          // synchronized (pgroom.getEmitters()) {
          //   for (SseEmitter emitter : pgroom.getEmitters()) {
          //     try {
          //       logger.info("Sending event to emitter: " + emitter + " with data: " + jsonData);
          //       emitter.send(SseEmitter.event().name("participantsList").data(jsonData));
          //       logger.info("Event sent: " + jsonData);
          //     } catch (Exception e) {
          //       pgroom.removeEmitter(emitter);
          //       logger.error("Error sending event: " + e.getMessage());
          //     }
          //   }
          //   logger.info("Participants list sent");
          // }
        }
        TimeUnit.MILLISECONDS.sleep(1000);
      }
    } catch (Exception e) {
      logger.error("Error in asyncSendParticipantsList(...): {}", e.getMessage());
    }
  }

  public void setParticipantsUpdated() {
    logger.info("setParticipantsUpdated called");
    this.participantsUpdated.set(true);
  }

  public void addParticipantToRoom(long roomID, GameRoomParticipant participant) {
    logger.info("addParticipantToRoom called");
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get(roomID);
    if (pgroom.addParticipant(participant)) {
      logger.info("Participant added");
      setParticipantsUpdated();
    }
  }

  public void removeParticipantFromRoom(long roomID, long userID) {
    logger.info("removeParticipantFromRoom called");
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get(roomID);
    pgroom.removeParticipant(userID);
    logger.info("Participant removed");
    setParticipantsUpdated();
  }

  @Async
  public void asyncSendAnswerList(PublicGameRoom pgroom, int quizID) {
    logger.info("asyncSendAnswerList called");
    double time = quizTableMapper.selectQuizTableByID(quizID).getTimelimit();
    try {
      while (true) { // pgroom.confirmedResult の実装後はそれが true になったら抜けるようにする
        List<GameRoomParticipant> participants = new ArrayList<>(pgroom.getParticipants().values());
        logger.info("Participants list : " + participants);

        ObjectMapper mapper = new ObjectMapper();
        // JSが扱えるオブジェクトに変換
        List<AnswerData> answerDataList = new ArrayList<>();
        for (GameRoomParticipant participant : participants) {
          AnswerObjImpl_4choices ansObj = (AnswerObjImpl_4choices) participant.getAnswerContent();
          logger.info("AnswerObj: " + ansObj);
          if (ansObj == null) {
            continue;
          }
          AnswerData ansData = new AnswerData(participant, ansObj);
          logger.info("AnswerData: " + ansData);
          answerDataList.add(ansData);
        }
        String answerDataJSON = mapper.writeValueAsString(answerDataList);
        logger.info("AnswerData JSON: " + answerDataJSON);

        synchronized (pgroom.getEmitters()) {
          Iterator<SseEmitter> iterator = pgroom.getEmitters().iterator();
          while (iterator.hasNext()) {
            SseEmitter emitter = iterator.next();
            try {
              logger.info("Sending event to emitter: " + emitter + " with data: " + answerDataJSON);
              emitter.send(SseEmitter.event().name("answerData").data(answerDataJSON));
              if (time >= 0) {
                logger.info("Sending time: " + time);
                emitter.send(SseEmitter.event().name("countdown").data(time));
              }
              // TODO: pgroom.confirmedResult が true ならページ遷移イベントを送信
              // イベント名：transition、データ："toRanking" | "toOverall"
              logger.info("Events sent");
            } catch (Exception e) {
              iterator.remove();
              logger.error("Error sending event: " + e.getMessage());
            }
          }
        }
        TimeUnit.MILLISECONDS.sleep(1000); // 1秒ごとに更新
        time--;
      }
    } catch (Exception e) {
      logger.error("Error in asyncSendAnswerList(...): {}", e.getMessage());
    }
  }

  @Async
  public void asyncSendPageTransition(PublicGameRoom pgroom) {
    logger.info("asyncSendPageTransition called with roomID: " + pgroom.getGameRoomID());
    long roomID = pgroom.getGameRoomID();
    pageTransitionMap.put(roomID, false);
    while (true) {
      synchronized (pageTransitionMap) {
        if (pageTransitionMap.get(roomID)) {
          sendPageTransition(pgroom);
          pageTransitionMap.put(roomID, false);
        }
      }
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (Exception e) {
        logger.error("Error in asyncSendPageTransition(...): {}", e.getMessage());
      }
    }
  }

  public void sendPageTransition(PublicGameRoom pgroom) {
    logger.info("sendPageTransition called with roomID: " + pgroom.getGameRoomID());

    for(int i = 0; i < pgroom.getEmitters().size(); i++) {
      SseEmitter emitter = pgroom.getEmitters().get(i);

      try {
        logger.info("Sending page transition event to emitter: " + emitter);
        emitter.send(SseEmitter.event().name("pageTransition").data("pageTransition"));
        logger.info("Page transition event sent");
      } catch (Exception e) {
        pgroom.removeEmitter(emitter);
        logger.error("Error sending page transition event: " + e.getMessage());
      }
    }

    // for (SseEmitter emitter : pgroom.getEmitters()) {
    //   try {
    //     logger.info("Sending page transition event to emitter: " + emitter);
    //     emitter.send(SseEmitter.event().name("pageTransition").data("pageTransition"));
    //     logger.info("Page transition event sent");
    //   } catch (Exception e) {
    //     pgroom.removeEmitter(emitter);
    //     logger.error("Error sending page transition event: " + e.getMessage());
    //   }
    // }
  }

  public void setPageTransition(long roomID) {
    logger.info("setPageTransition called with roomID: " + roomID);
    synchronized (pageTransitionMap) {
      pageTransitionMap.put(roomID, true);
    }
  }

  @Async
  public void asyncAutoRedirectToAnswerPage(SseEmitter emitter, long roomID) {
    logger.info("AsyncPGameRoomService.asyncAutoRedirectToAnswerPage is called with roomID: " + roomID);

    try {
      PublicGameRoom redirectToRoom = pGameRoomManager.getPublicGameRooms().get(roomID);

      emitter.send(SseEmitter.event().name("init")
          .data("SSE connection established. Ready to auto-redirect to Answer Page of roomID: " + roomID));
      while (true) {
        if (redirectToRoom.isAnswering()) {
          emitter.send(roomID);
          break;
        }

        TimeUnit.MILLISECONDS.sleep(100L);
      }
    } catch (Exception e) {
      logger.error("AsyncPGameRoomService.asyncAutoRedirectToAnswerPage Error: " + e.getClass().getName() + ":"
          + e.getMessage());
      emitter.complete();
    } finally {
      try { TimeUnit.MILLISECONDS.sleep(100L); } catch(Exception e) {} // マージン
      emitter.complete();
    }
  }

  public void cancelGameRoom(long roomID) {
    logger.info("cancelGameRoom called with roomID: " + roomID);
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get(roomID);
    if (pgroom != null) {
      notifyCancellation(pgroom);
    }
  }

  @Async
  public void notifyCancellation(PublicGameRoom pgroom) {
    logger.info("notifyCancellation called with roomID: " + pgroom.getGameRoomID());
    for(int i = 0; i < pgroom.getEmitters().size(); i++) {
      SseEmitter emitter = pgroom.getEmitters().get(i);

      try {
        emitter.send(SseEmitter.event().name("gameCancelled").data("gameCancelled"));
      } catch (Exception e) {
        pgroom.removeEmitter(emitter);
      }
    }

    // for (SseEmitter emitter : pgroom.getEmitters()) {
    //   try {
    //     emitter.send(SseEmitter.event().name("gameCancelled").data("gameCancelled"));
    //   } catch (Exception e) {
    //     pgroom.removeEmitter(emitter);
    //   }
    // }
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
          emitter.send("room=" + roomID + "&quiz=" + quizID);
          break;
        }

        TimeUnit.MILLISECONDS.sleep(1000L);
      }
    } catch (Exception e) {
      logger.error("AsyncPGameRoomService.asyncAutoRedirectToAnsResultPage Error: " + e.getClass().getName() + ":"
          + e.getMessage());
      emitter.complete();
    } finally {
      try { TimeUnit.MILLISECONDS.sleep(500L); } catch(Exception e) {} // マージン
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
    while(true) {
      if(targetRoom.getElapsedAnswerTime_ms() > timelimit_ms) break;

      int answeredNum = 0;
      for(GameRoomParticipant ptc : targetRoom.getParticipants().values()) {
        if(ptc.isAnswered()) answeredNum++;
      }
      if(targetRoom.getParticipants().size() <= answeredNum) break;

      try {
        TimeUnit.MILLISECONDS.sleep(500L);
      } catch(Exception e) {
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
    for(GameRoomParticipant ptc : targetRoom.getParticipants().values()) {
      long calcPoint = 0L;
      AnswerObj ans = ptc.getAnswerContent();

      if(ans != null && ((AnswerObjImpl_4choices)ans).getAnsValue() == correctNum) {
        calcPoint = (long)((double)quiz.getPoint() * (1.0 - ((double)ptc.getAnswerTime_ms() / timelimit_ms)));
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
