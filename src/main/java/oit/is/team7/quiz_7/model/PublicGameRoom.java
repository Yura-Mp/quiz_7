package oit.is.team7.quiz_7.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @classdesc
 * 単一の公開ゲームルームを成すクラス．
 * 詳しくは本サービスの最新のクラス設計図(quiz_7_ClassGraph_Isdev24_ver*.drawio)を参照．
 *
*/
public class PublicGameRoom {
  private long gameRoomID;
  private long hostUserID;
  private LinkedHashMap<Long, GameRoomParticipant> participants;
  private int maxPlayers = 50;
  private ArrayList<Long> quizPool;
}
