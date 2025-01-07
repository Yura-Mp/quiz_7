package oit.is.team7.quiz_7.model;

/**
 *
 */
public class PGameRoomRankingEntryBean {
  public long ID = 0L; // エントリのユーザID．
  public String name = ""; // エントリのユーザ名．
  public int rank = 1; // エントリの順位．
  public int rankDiff = 0; // updateRank()メソッドでの順位更新前後の順位差．
  public String rankDiffStr = "-"; // updateRank()メソッドで更新される順位差文字列．
  public long point = 0L; // エントリの得点．

  public PGameRoomRankingEntryBean() {

  }

  public PGameRoomRankingEntryBean(long id, String name) {
    this.ID = id;
    this.name = name;
  }

  public PGameRoomRankingEntryBean(long id, String name, long point) {
    this(id, name);
    this.point = point;
  }

  public PGameRoomRankingEntryBean(PGameRoomRankingEntryBean original) {
    this.ID = original.ID;
    this.name = new String(original.name);
    this.rank = original.rank;
    this.rankDiff = original.rankDiff;
    this.rankDiffStr = new String(original.rankDiffStr);
    this.point = original.point;
  }

  public void updateRank(int rank) {
    this.rankDiff = rank - this.rank;
    this.rank = rank;

    if(this.rankDiff < 0) {
      this.rankDiffStr = "∧" + (-this.rankDiff);
    }
    else if(this.rankDiff > 0) {
      this.rankDiffStr = "∨" + (this.rankDiff);
    }
    else {
      this.rankDiffStr = "－";
    }

    return;
  }
}
