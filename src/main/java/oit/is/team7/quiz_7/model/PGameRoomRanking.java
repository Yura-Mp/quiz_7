package oit.is.team7.quiz_7.model;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;

/**
 * @classdesc
 * 公開ゲームルームのランキングを包括的に管理するクラス．<p>
 * 基本的に内部フィールドの直接操作は許容できない．<p>
 * 不用意なgetter, setterの定義は許容できない．<p>
 *
 * @author Yura-Mp
 * @since 2025/01/05
 */
public class PGameRoomRanking {
  /**
   * このランキングの並べ替え状態を表す．
   * @param NO_ENTRY - ランキングのエントリがない状態．初期状態．
   * @param NON_SORTED - ランキングが並べ替えられていない状態．
   * @param SORTED_ON_POINT - ランキングが得点順で並べ替えられている状態．
   */
  private enum SortState {
    NO_ENTRY,
    NON_SORTED,
    SORTED_ON_POINT;
  }

  /*
   * ranking(ランキング)とpositionByUserID(ユーザIDに対応するランキング位置)は常に整合性を保っているものとする．
   */
  /**
   * ランキングの並びそのものとなる{@code ArrayList}．
   * 要素はランキングエントリ({@link PGameRoomRankingEntryBean}クラス)．
   */
  private ArrayList<PGameRoomRankingEntryBean> ranking;

  /**
   * ユーザのID({@link Long})とそれに対応するランキング({@code ranking})のエントリ位置({@link Integer})を格納する{@link LinkedHashMap}．
   */
  private LinkedHashMap<Long, Integer> indexByUserID;

  /**
   * ランキングの並び替え状態を保持するフィールド．
   */
  private SortState sortState;

  /**
   * ランキングエントリが空のランキングを返すコンストラクタ．
   * @constructor
   * @return {@code (constructor)} 空のランキング．
   *
   * @author Yura-Mp
   * @since 2025/01/05
   */
  public PGameRoomRanking() {
    this.ranking = new ArrayList<PGameRoomRankingEntryBean>();
    this.indexByUserID = new LinkedHashMap<Long, Integer>();
    this.sortState = SortState.NO_ENTRY;
  }

  /**
   * ランキングエントリ({@link PGameRoomRankingEntryBean}クラス)を追加するメソッド．<p>
   * 追加されるエントリの{@code ID}が，すでにランキングに存在しているエントリの{@code ID}と同じであれば，その既存のエントリに上書きする．<p>
   *
   * @param entry {@link PGameRoomRankingEntryBean} 追加するランキングのエントリ．
   * @return {@link void}
   *
   * @author Yura-Mp
   * @since 2025/01/05
   */
  public void addEntry(PGameRoomRankingEntryBean entry) {
    final int INVALID_IDX = -1;
    int idx = (this.indexByUserID.get(entry.ID) != null ? this.indexByUserID.get(entry.ID) : INVALID_IDX);

    if (idx == INVALID_IDX) {
      this.ranking.add(entry);
      this.indexByUserID.put(entry.ID, this.ranking.size() - 1);
    } else {
      this.ranking.set(idx, entry);
    }

    this.sortState = SortState.NON_SORTED;

    return;
  }

  /**
   * ※詳細は{@code addEntryメソッド}を参照．
   *
   * @param entry {@link PGameRoomRankingEntryBean} 追加するランキングのエントリ．
   * @return {@link void}
   *
   * @author Yura-Mp
   * @since 2025/01/05
   */
  public void setEntry(PGameRoomRankingEntryBean entry) {
    this.addEntry(entry);
  }

  /**
   * @readonly 読み取り専用．不変(この返り値を変更してもこのインスタンスのフィールドには影響しない．)．
   * @param index {@link int} 取得するエントリのランキング添字．
   * @return {@link PGameRoomRankingEntryBean} 取得するランキング({@code ranking})のエントリ．
   *
   * @author Yura-Mp
   * @since 2025/01/05
   */
  public PGameRoomRankingEntryBean getEntry(int index) {
    return new PGameRoomRankingEntryBean(this.ranking.get(index));
  }

  /**
   * @readonly 読み取り専用．不変(この返り値を変更してもこのインスタンスのフィールドには影響しない．)．
   * @param userID {@link long} 取得するエントリのユーザID．
   * @return {@link PGameRoomRankingEntryBean} 取得するランキング({@code ranking})のエントリ．
   *
   * @author Yura-Mp
   * @since 2025/01/05
   */
  public PGameRoomRankingEntryBean getEntry(long userID) {
    return this.getEntry(this.indexByUserID.get(userID));
  }

  /**
   * ランキングの{@link ArrayList}で返す．読み取り専用．<p>
   * このクラス内部のランキング({@code ranking})が何らかの並べ替え方法で並べ替えられている必要がある．もし，並べ替えられていない場合は，デフォルトで{@code sortByPoint()}(得点降順)で並べ替えられる．<p>
   * 返ってくる{@link ArrayList}は，このメソッド呼び出し直前の並べ替えによっての添字順で単調性を保つ．(このメソッド直前での並べ替えに沿って，ランキングの添字がそのまま順位となる．)<p>
   *
   * @readonly 読み取り専用．不変(この返り値を変更してもこのインスタンスのフィールドには影響しない．)．
   * @return {@link ArrayList<PGameRoomRankingEntryBean>} このメソッド直前での並べ替えが適用されたランキングの{@link ArrayList}．
   * @throws IllegalStateException 並べ替えが行われていない状態でランキングを取得しようとしたとき，ランキングを成していない{@link ArrayList}が返却されるのを防ぐためにこのエラーが送出される．
   *
   * @author Yura-Mp
   * @since 2025/01/05
   */
  public ArrayList<PGameRoomRankingEntryBean> getRanking() {
    if (this.sortState == SortState.NON_SORTED) {
      /*
      throw new IllegalStateException("ソートされていないランキングの参照は許可されていません．");
       */
      this.sortByPoint();
    }

    return new ArrayList<PGameRoomRankingEntryBean>(this.ranking);
  }

  public void clearEntry() {
    this.ranking.clear();
    this.indexByUserID.clear();
    this.sortState = SortState.NO_ENTRY;
    return;
  }

  public LinkedHashMap<Long, Integer> getIndexesByUserID() {
    return new LinkedHashMap<Long, Integer>(this.indexByUserID);
  }

  /**
   * エントリを得点({@code point})降順で並べ替えてランキングを成立させるメソッド．<p>
   * 並べ替え状態({@code sortState})にかかわらず，強制的にソートする．
   *
   * @return {@link void}
   *
   * @author Yura-Mp
   * @since 2025/01/05
   */
  public void forceSortByPoint() {
    // [エラー] ランキングのエントリがない場合．
    if (this.ranking.size() <= 0) {
      throw new IllegalStateException("ランキングのエントリがない状態での並べ替えは許可されていません．");
    }

    // 獲得点数の降順でソート．
    Collections.sort(this.ranking, new Comparator<PGameRoomRankingEntryBean>() {
      @Override
      public int compare(PGameRoomRankingEntryBean a, PGameRoomRankingEntryBean b) {
        return a.point > b.point ? -1 : 1;
      }
    });

    // インデックスのMapを再定義．
    for (int i = 0; i < this.ranking.size(); i++) {
      this.indexByUserID.put(this.ranking.get(i).ID, i);
    }

    int rank = 1;
    long seePt = this.ranking.get(0).point;

    // 順位付け
    for (int i = 0; i < this.ranking.size(); i++) {
      if (seePt == this.ranking.get(i).point) {
        this.ranking.get(i).updateRank(rank);
      } else {
        rank = (i + 1);
        this.ranking.get(i).updateRank(rank);
        seePt = this.ranking.get(i).point;
      }
    }

    this.sortState = SortState.SORTED_ON_POINT;

    return;
  }

  /**
   * エントリを得点({@code point})降順で並べ替えてランキングを成立させるメソッド．<p>
   * すでに得点降順で並べ替えられている場合は何もしない．そうでなければ，{@code forceSortByPoint()}を実行する．<p>
   *
   * @return {@link void}
   *
   * @author Yura-Mp
   * @since 2025/01/05
   */
  public void sortByPoint() {
    if (this.sortState == SortState.SORTED_ON_POINT) {
      return;
    }

    this.forceSortByPoint();
  }
}
