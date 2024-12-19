package oit.is.team7.quiz_7.model;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

/**
 * @classdesc
 * 本サービスの公開ゲームルームを一元的に管理するクラス(@Component)．
 * 詳しくは本サービスの最新のクラス設計図(quiz_7_ClassGraph_Isdev24_ver*.drawio)を参照．
 *
 */
@Component
public class PGameRoomManager {
  private final int MAX_ROOM_NUM = 100;

  private LinkedHashMap<Long, PublicGameRoom> publicGameRooms;
  private LinkedHashMap<Long, Long> belonging;
}
