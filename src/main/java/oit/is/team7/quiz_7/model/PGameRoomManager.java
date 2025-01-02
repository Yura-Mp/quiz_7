package oit.is.team7.quiz_7.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

/**
 * @classdesc
 *            本サービスの公開ゲームルームを一元的に管理するクラス(@Component)．
 *            詳しくは本サービスの最新のクラス設計図(quiz_7_ClassGraph_Isdev24_ver*.drawio)を参照．
 *
 */
@Component
public class PGameRoomManager {
  private final int MAX_ROOM_NUM = 100;

  private LinkedHashMap<Long, PublicGameRoom> publicGameRooms;
  private LinkedHashMap<Long, Long> belonging;

  public PGameRoomManager() {
    this.publicGameRooms = new LinkedHashMap<Long, PublicGameRoom>();
    this.belonging = new LinkedHashMap<Long, Long>();
  }

  public LinkedHashMap<Long, PublicGameRoom> getPublicGameRooms() {
    return publicGameRooms;
  }

  public void setPublicGameRooms(LinkedHashMap<Long, PublicGameRoom> publicGameRooms) {
    this.publicGameRooms = publicGameRooms;
  }

  public LinkedHashMap<Long, Long> getBelonging() {
    return belonging;
  }

  public void setBelonging(LinkedHashMap<Long, Long> belonging) {
    this.belonging = belonging;
  }

  public boolean addPublicGameRoom(PublicGameRoom pgroom) {
    if (this.publicGameRooms.size() < MAX_ROOM_NUM) {
      this.publicGameRooms.put(pgroom.getGameRoomID(), pgroom);
      return true;
    } else {
      return false;   
    }
  }
  
  public void removeGameRoom(long roomID) {
    publicGameRooms.remove(roomID);
  }

  public ArrayList<PublicGameRoom> getOpenPublicGameRoomList() {
    ArrayList<PublicGameRoom> publicGameRoomList = new ArrayList<PublicGameRoom>();
    for (PublicGameRoom room : publicGameRooms.values()) {
      if (room.isOpen()) {
        publicGameRoomList.add(room);
      }
    }
    return publicGameRoomList;
  }

  public void removeParticipantFromGameRoom(long userID, long roomID) {
    belonging.remove(userID);
    PublicGameRoom gameRoom = publicGameRooms.get(roomID);
    if (gameRoom != null) {
      gameRoom.removeParticipant(userID);
    }
  }

  public void addParticipantToBelonging(long userID, long roomID) {
    belonging.put(userID, roomID);
  }

  public void removeParticipantFromBelonging(long userID) {
    belonging.remove(userID);
  }

}
