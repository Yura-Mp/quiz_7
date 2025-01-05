package oit.is.team7.quiz_7.service;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;

import oit.is.team7.quiz_7.model.PublicGameRoom;
import oit.is.team7.quiz_7.model.GameRoomParticipant;
import oit.is.team7.quiz_7.model.PGameRoomManager;

@Service
public class AsyncPGameRoomService {
  private final Logger logger = LoggerFactory.getLogger(AsyncPGameRoomService.class);
  private final AtomicBoolean participantsUpdated = new AtomicBoolean(false);
  private Map<Long, Boolean> pageTransitionMap = new HashMap<>();

  @Autowired
  PGameRoomManager pGameRoomManager;

  @Async
  public void asyncSendParticipantsList(PublicGameRoom pgroom) {
    logger.info("asyncSendParticipantsList called");
    try {
      while (true) {
        if (this.participantsUpdated.getAndSet(false)) {
          List<GameRoomParticipant> participants = new ArrayList<>(pgroom.getParticipants().values());
          logger.info("Participants list updated: " + participants);

          String jsonData = new ObjectMapper().writeValueAsString(participants);

          for (SseEmitter emitter : pgroom.getEmitters()) {
            try {
              logger.info("Sending event to emitter: " + emitter + " with data: " + jsonData);
              emitter.send(SseEmitter.event().name("participantsList").data(jsonData));
              logger.info("Event sent: " + jsonData);
            } catch (Exception e) {
              pgroom.removeEmitter(emitter);
              logger.error("Error sending event: " + e.getMessage());
            }
          }
          logger.info("Participants list sent");
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
    for (SseEmitter emitter : pgroom.getEmitters()) {
      try {
        logger.info("Sending page transition event to emitter: " + emitter);
        emitter.send(SseEmitter.event().name("pageTransition").data("pageTransition"));
        logger.info("Page transition event sent");
      } catch (Exception e) {
        pgroom.removeEmitter(emitter);
        logger.error("Error sending page transition event: " + e.getMessage());
      }
    }
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
      PublicGameRoom redirectToRoom =  pGameRoomManager.getPublicGameRooms().get(roomID);

      emitter.send(SseEmitter.event().name("init").data("SSE connection established. Ready to auto-redirect to Answer Page of roomID: " + roomID));
      while(true) {
        if(redirectToRoom.isAnswering()) {
          emitter.send(roomID);
          break;
        }

        TimeUnit.MILLISECONDS.sleep(100L);
      }
    } catch(Exception e) {
      logger.error("AsyncPGameRoomService.asyncAutoRedirectToAnswerPage Error: " + e.getClass().getName() + ":" + e.getMessage());
    } finally {
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
    for (SseEmitter emitter : pgroom.getEmitters()) {
      try {
        emitter.send(SseEmitter.event().name("gameCancelled").data("gameCancelled"));
      } catch (Exception e) {
        pgroom.removeEmitter(emitter);
      }
    }
  }
}
