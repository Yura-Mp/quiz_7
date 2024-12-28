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

import oit.is.team7.quiz_7.model.PublicGameRoom;
import oit.is.team7.quiz_7.model.GameRoomParticipant;
import oit.is.team7.quiz_7.model.PGameRoomManager;

@Service
public class AsyncPGameRoomService {
  private final Logger logger = LoggerFactory.getLogger(AsyncPGameRoomService.class);
  private boolean participantsUpdated = false;

  @Autowired
  PGameRoomManager pGameRoomManager;

  @Async
  public void asyncSendParticipantsList(SseEmitter emitter, PublicGameRoom pgroom) {
    logger.info("asyncSendParticipantsList called");
    try {
      while (true) {
        if (participantsUpdated) {
          List<GameRoomParticipant> participants = new ArrayList<>(pgroom.getParticipants().values());
          logger.info("Participants list: " + participants);
          for (SseEmitter emitterInRoom : pgroom.getEmitters()) {
            try {
              emitterInRoom.send(SseEmitter.event().name("participantsUpdate").data(participants));
              logger.info("Event sent: " + participants);
            } catch (Exception e) {
              pgroom.removeEmitter(emitterInRoom);
              logger.error("Error sending event: " + e.getMessage());
            }
          }
          logger.info("Participants list sent");
          participantsUpdated = false;
        }
        TimeUnit.MILLISECONDS.sleep(1000);
      }
    } catch (Exception e) {
      logger.error("Error in asyncSendParticipantsList(...): {}", e.getMessage());
      emitter.completeWithError(e);
    } finally {
      emitter.complete();
    }
  }

  public void addParticipantToRoom(long roomID, GameRoomParticipant participant) {
    logger.info("addParticipantToRoom called");
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get(roomID);
    if (pgroom.addParticipant(participant)) {
      logger.info("Participant added");
      participantsUpdated = true;
    }
  }
}
