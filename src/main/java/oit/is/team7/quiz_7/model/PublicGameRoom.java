package oit.is.team7.quiz_7.model;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.team7.quiz_7.service.AsyncPGameRoomService;

/**
 * @classdesc
 *            単一の公開ゲームルームを成すクラス．
 *            詳しくは本サービスの最新のクラス設計図(quiz_7_ClassGraph_Isdev24_ver*.drawio)を参照．
 *
 */
public class PublicGameRoom {
  private long gameRoomID;
  private String gameRoomName;
  private long hostUserID;
  private String hostUserName;
  private LinkedHashMap<Long, GameRoomParticipant> participants;
  private int maxPlayers = 50;
  private ArrayList<Long> quizPool;
  private List<SseEmitter> emitters;

  @Autowired
  AsyncPGameRoomService asyncPGRService;

  public PublicGameRoom(long gameRoomID, String gameRoomName, long hostUserID, String hostUserName, int maxPlayers,
      ArrayList<Long> quizPool) {
    this.gameRoomID = gameRoomID;
    this.gameRoomName = gameRoomName;
    this.hostUserID = hostUserID;
    this.hostUserName = hostUserName;
    this.participants = new LinkedHashMap<Long, GameRoomParticipant>();
    this.maxPlayers = maxPlayers;
    this.quizPool = quizPool;
    this.emitters = new ArrayList<SseEmitter>();
  }

  public long getGameRoomID() {
    return gameRoomID;
  }

  public void setGameRoomID(long gameRoomID) {
    this.gameRoomID = gameRoomID;
  }

  public String getGameRoomName() {
    return gameRoomName;
  }

  public void setGameRoomName(String gameRoomName) {
    this.gameRoomName = gameRoomName;
  }

  public long getHostUserID() {
    return hostUserID;
  }

  public void setHostUserID(long hostUserID) {
    this.hostUserID = hostUserID;
  }

  public String getHostUserName() {
    return hostUserName;
  }

  public void setHostUserName(String hostUserName) {
    this.hostUserName = hostUserName;
  }

  public LinkedHashMap<Long, GameRoomParticipant> getParticipants() {
    return participants;
  }

  public void setParticipants(LinkedHashMap<Long, GameRoomParticipant> participants) {
    this.participants = participants;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public void setMaxPlayers(int maxPlayers) {
    this.maxPlayers = maxPlayers;
  }

  public ArrayList<Long> getQuizPool() {
    return quizPool;
  }

  public void setQuizPool(ArrayList<Long> quizPool) {
    this.quizPool = quizPool;
  }

  public boolean addParticipant(GameRoomParticipant participant) {
    if (this.participants.size() < this.maxPlayers) {
      this.participants.put(participant.getUserID(), participant);
      return true;
    } else {
      return false;
    }
  }

  public void removeParticipant(long userID) {
    participants.remove(userID);
  }

  public List<SseEmitter> getEmitters() {
    return this.emitters;
  }

  public void addEmitter(SseEmitter emitter) {
    this.emitters.add(emitter);
    emitter.onCompletion(() -> this.emitters.remove(emitter));
    emitter.onTimeout(() -> this.emitters.remove(emitter));
    emitter.onError((e) -> this.emitters.remove(emitter));
  }

  public void removeEmitter(SseEmitter emitter) {
    emitters.remove(emitter);
  }

}
