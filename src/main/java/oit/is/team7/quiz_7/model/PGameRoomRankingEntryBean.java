package oit.is.team7.quiz_7.model;

public class PGameRoomRankingEntryBean {
  public long ID = 0L;
  public String name = "";
  public int rank = 1;
  public int rankDiff = -1;
  public String rankDiffStr = "-";
  public long point = -1;

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
