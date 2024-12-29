package oit.is.team7.quiz_7.model;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;

public class PGameRoomRanking {
  private enum SortState {
    NO_ENTRY,
    NON_SORTED,
    SORTED_ON_POINT;
  }

  /*
   * ranking(ランキング)とpositionByUserID(ユーザIDに対応するランキング位置)は常に整合性を保っているものとする．
   */
  private ArrayList<PGameRoomRankingEntryBean> ranking;
  private LinkedHashMap<Long, Integer> indexByUserID;
  private SortState sortState;

  public PGameRoomRanking() {
    this.ranking = new ArrayList<PGameRoomRankingEntryBean>();
    this.indexByUserID = new LinkedHashMap<Long, Integer>();
    this.sortState = SortState.NO_ENTRY;
  }

  public void addEntry(PGameRoomRankingEntryBean entry) {
    final int INVALID_IDX = -1;
    int idx = (this.indexByUserID.get(entry.ID) != null ? this.indexByUserID.get(entry.ID) : INVALID_IDX);

    if(idx == INVALID_IDX) {
      this.ranking.add(entry);
      this.indexByUserID.put(entry.ID, this.ranking.size()-1);
    } else {
      this.ranking.set(idx, entry);
    }

    this.sortState = SortState.NON_SORTED;

    return;
  }

  public ArrayList<PGameRoomRankingEntryBean> getRanking() {
    if(this.sortState == SortState.NON_SORTED) {
      throw new IllegalStateException("ソートされていないランキングの参照は許可されていません．");
    }

    return new ArrayList<PGameRoomRankingEntryBean>(this.ranking);
  }

  public LinkedHashMap<Long, Integer> getIndexesByUserID() {
    return new LinkedHashMap<Long, Integer>(this.indexByUserID);
  }

  public void clearEntry() {
    this.ranking.clear();
    this.indexByUserID.clear();
    this.sortState = SortState.NO_ENTRY;
    return;
  }

  public void sortByPoint() {
    // [エラー] ランキングのエントリがない場合．
    if(this.ranking.size() <= 0) {
      throw new IllegalStateException("ランキングのエントリがない状態での並べ替えは許可されていません．");
    }

    if(this.sortState == SortState.SORTED_ON_POINT) {
      return;
    }

    // 獲得点数の降順でソート．
    Collections.sort(this.ranking, new Comparator<PGameRoomRankingEntryBean>() {
      @Override
      public int compare(PGameRoomRankingEntryBean a, PGameRoomRankingEntryBean b) {
        return a.point > b.point ? -1 : 1;
      }
    });

    // インデックスのMapを再定義．
    for(int i = 0; i < this.ranking.size(); i++) {
      this.indexByUserID.put(this.ranking.get(i).ID, i);
    }

    int rank = 1;
    long seePt = this.ranking.get(0).point;

    // 順位付け
    for(int i = 0; i < this.ranking.size(); i++) {
      if(seePt == this.ranking.get(i).point) {
        this.ranking.get(i).updateRank(rank);
      } else {
        rank = (i+1);
        this.ranking.get(i).updateRank(rank);
        seePt = this.ranking.get(i).point;
      }
    }

    this.sortState = SortState.SORTED_ON_POINT;

    return;
  }
}
