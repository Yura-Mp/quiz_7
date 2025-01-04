package oit.is.team7.quiz_7.service;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
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
              emitter.send(jsonData);
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

  public void asyncSendAnswerList(PublicGameRoom pgroom) {
    logger.info("asyncSendAnswerList called");
    try {
      while (true) {
        List<GameRoomParticipant> participants = new ArrayList<>(pgroom.getParticipants().values());
        logger.info("Participants list : " + participants);

        String jsonData = new ObjectMapper().writeValueAsString(participants);

        for (SseEmitter emitter : pgroom.getEmitters()) {
          try {
            logger.info("Sending event to emitter: " + emitter + " with data: " + jsonData);
            emitter.send(SseEmitter.event().name("refresh").data(jsonData));
            logger.info("Event sent");
          } catch (Exception e) {
            pgroom.removeEmitter(emitter);
            logger.error("Error sending event: " + e.getMessage());
          }
        }
        logger.info("refresh event sent");
        TimeUnit.MILLISECONDS.sleep(1000);
      }
    } catch (Exception e) {
      logger.error("Error in asyncSendAnswerList(...): {}", e.getMessage());
    }
  }
}
