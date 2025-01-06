package oit.is.team7.quiz_7.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;

import oit.is.team7.quiz_7.model.Gameroom;
import oit.is.team7.quiz_7.model.GameroomMapper;
import oit.is.team7.quiz_7.model.GameRoomParticipant;
import oit.is.team7.quiz_7.model.PGameRoomManager;
import oit.is.team7.quiz_7.model.PublicGameRoom;
import oit.is.team7.quiz_7.model.PGameRoomRanking;
import oit.is.team7.quiz_7.model.PGameRoomRankingEntryBean;
import oit.is.team7.quiz_7.model.QuizJson;
import oit.is.team7.quiz_7.model.QuizTable;
import oit.is.team7.quiz_7.model.QuizTableMapper;
import oit.is.team7.quiz_7.model.UserAccountMapper;
import oit.is.team7.quiz_7.service.AsyncPGameRoomService;

@Controller
@RequestMapping("/playing")
public class PlayingController {
  private final Logger logger = (Logger) LoggerFactory.getLogger(PlayingController.class);

  @Autowired
  GameroomMapper gameroomMapper;
  @Autowired
  PGameRoomManager pGameRoomManager;

  @Autowired
  QuizTableMapper quizTableMapper;

  @Autowired
  UserAccountMapper userAccountMapper;

  @Autowired
  AsyncPGameRoomService asyncPGRService;

  public String get_wait_guest(int roomID, Principal prin, ModelMap model) {
    long userID = userAccountMapper.selectUserAccountByUsername(prin.getName()).getId();
    Map<Long, GameRoomParticipant> participants = this.pGameRoomManager.getPublicGameRooms().get((long) roomID)
        .getParticipants();
    GameRoomParticipant participant = participants.get(userID);
    if (participant != null) {
      model.addAttribute("participant", participant);
    }
    List<GameRoomParticipant> sortParticipants = new ArrayList<>(participants.values());
    sortParticipants.sort((p1, p2) -> Long.compare(p2.getPoint(), p1.getPoint()));
    for (int rank = 0; rank < sortParticipants.size(); rank++) {
      GameRoomParticipant target = sortParticipants.get(rank);
      if (target.getUserID() == userID) {
        model.addAttribute("participant_rank", rank + 1);
      }
    }
    return "/playing/guest/wait.html";
  }

  @GetMapping("/wait")
  public String get_wait_host(@RequestParam("room") int roomID, Principal prin, ModelMap model) {
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get((long) roomID);
    if (!pgroom.getHostUserName().equals(prin.getName())) {
      // ユーザがホストでなければゲスト向けの待機画面を表示
      return get_wait_guest(roomID, prin, model);
    }
    asyncPGRService.setPageTransition(roomID); // ページ遷移イベントを送信
    pgroom.setOpen(false);
    if (pgroom != null) {
      pgroom.resetAllParticipants();
      logger.info("[DBG]{}人の初期化を完了", pgroom.getParticipants().size());
    }
    model.addAttribute("pgameroom", pgroom);
    ArrayList<QuizTable> quizList = new ArrayList<QuizTable>();
    for (long quizID : pgroom.getQuizPool()) {
      quizList.add(quizTableMapper.selectQuizTableByID((int) quizID));
    }
    model.addAttribute("quizList", quizList);
    long nextQuizID = pgroom.getQuizPool().get(pgroom.getNextQuizIndex());
    QuizTable nextQuiz = quizTableMapper.selectQuizTableByID((int) nextQuizID);
    model.addAttribute("nextQuiz", nextQuiz);
    String quizJsonString = nextQuiz.getParsableQuizJSON();
    logger.info("quizJsonString: " + quizJsonString);
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      QuizJson quizJson = objectMapper.readValue(quizJsonString, QuizJson.class);
      model.addAttribute("nextQuizJson", quizJson);
    } catch (Exception e) {
      logger.error("Error at parsing quizJson: " + e.toString());
    }
    return "/playing/host/wait.html";
  }

  @GetMapping("/ans_result")
  public String get_ans_result_host(@RequestParam("room") long roomID, @RequestParam("quiz") int curQuizID,
      Principal prin, ModelMap model) {
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get(roomID);
    // if (!pgroom.getHostUserName().equals(prin.getName())) {
    // // ユーザがホストでなければゲスト向けの回答結果画面を表示
    // return get_wait_guest(roomID, prin, model);
    // }
    model.addAttribute("pgameroom", pgroom);
    ArrayList<QuizTable> quizList = new ArrayList<QuizTable>();
    for (long quizID : pgroom.getQuizPool()) {
      quizList.add(quizTableMapper.selectQuizTableByID((int) quizID));
    }
    model.addAttribute("quizList", quizList);
    QuizTable curQuiz = quizTableMapper.selectQuizTableByID(curQuizID);
    model.addAttribute("curQuiz", curQuiz);
    String quizJsonString = curQuiz.getParsableQuizJSON();
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      QuizJson quizJson = objectMapper.readValue(quizJsonString, QuizJson.class);
      model.addAttribute("curQuizJson", quizJson);
    } catch (Exception e) {
      logger.error("Error at parsing quizJson: " + e.toString());
    }
    // 参加者リストをマッピング
    Map<Long, GameRoomParticipant> participants = pgroom.getParticipants();
    ArrayList<GameRoomParticipant> participantsList = new ArrayList<>(participants.values());
    model.addAttribute("participantsList", participantsList);
    return "/playing/host/ans_result.html";
  }

  // 出題者のランキング表示ページ
  @GetMapping("/quiz_result")
  public String get_quiz_result(@RequestParam("room") int roomID, @RequestParam("quiz") int curQuizID, ModelMap model) {
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get((long) roomID);
    model.addAttribute("pgameroom", pgroom);
    QuizTable curQuiz = quizTableMapper.selectQuizTableByID(curQuizID);
    model.addAttribute("curQuiz", curQuiz);
    String quizJsonString = curQuiz.getParsableQuizJSON();
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      QuizJson quizJson = objectMapper.readValue(quizJsonString, QuizJson.class);
      model.addAttribute("curQuizJson", quizJson);
    } catch (Exception e) {
      logger.error("Error at parsing quizJson: " + e.toString());
    }
    return "/playing/host/quiz_result.html";
  }

  @GetMapping("/overall")
  public String get_overall_host(@RequestParam("room") int roomID, Principal prin, ModelMap model) {
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get((long) roomID);
    if (!pgroom.getHostUserName().equals(prin.getName())) {
      // ユーザがホストでなければゲスト向けの待機画面を表示
      return get_overall_guest(roomID, prin, model);
    }
    model.addAttribute("pgameroom", pgroom);
    // クイズリストをマッピング
    ArrayList<QuizTable> quizList = new ArrayList<QuizTable>();
    for (long quizID : pgroom.getQuizPool()) {
      quizList.add(quizTableMapper.selectQuizTableByID((int) quizID));
    }
    model.addAttribute("quizList", quizList);
    return "/playing/host/overall.html";
  }

  public String get_overall_guest(@RequestParam("room") int roomID, Principal prin, ModelMap model) {
    long userID = userAccountMapper.selectUserAccountByUsername(prin.getName()).getId();
    PublicGameRoom pgroom = pGameRoomManager.getPublicGameRooms().get((long) roomID);
    Map<Long, GameRoomParticipant> participants = pgroom.getParticipants();
    GameRoomParticipant participant = participants.get(userID);
    if (pgroom != null) {
      model.addAttribute("pgameroom", pgroom);
    }
    if (participant != null) {
      model.addAttribute("participant", participant);
    }
    List<GameRoomParticipant> sortParticipants = new ArrayList<>(participants.values());
    sortParticipants.sort((p1, p2) -> Long.compare(p2.getPoint(), p1.getPoint()));
    for (int rank = 0; rank < sortParticipants.size(); rank++) {
      GameRoomParticipant target = sortParticipants.get(rank);
      if (target.getUserID() == userID) {
        model.addAttribute("participantRank", rank + 1);
      }
    }
    return "/playing/guest/overall.html";
  }

  /**
   * 公開ゲームルームの汎用的なiframe(インラインフレーム)用のランキングページを提供するGetMappingメソッド．
   * <p>
   * {@code room (roomID)}をクエリパラメータで指定し，これに対応する公開ゲームルーム({@code PublicGameRoom}インスタンス)が公開ゲームルームマネージャ({@code PGameRoomManager}インスタンス)に存在すれば，その公開ゲームルームのランキングインスタンス({@code PGameRoomRanking})を参照して，ランキングページを作成し返却する．
   * <p>
   * 加えて，{@code user (userID)}をクエリパラメータで指定していれば，そのランキングでのユーザに関する情報とランキングでの自身の位置のハイライトを追加する．
   *
   * @param roomID   {@link Long} -
   *                 どの公開ゲームルームのランキングをリクエストするかを指定するリクエスト(クエリ)パラメータ．URI上では{@code room}で指定する．
   * @param userID   {@link Long} -
   *                 どのユーザ(参加者)の情報に集中するかを指定するリクエスト(クエリ)パラメータ．URI上では{@code user}で指定する．
   * @param DBG_FLAG {@link Boolean} - デバッグフラグ．URI上では{@code DBG}で指定する．
   * @param prin     {@link Principal} - (説明省略)
   * @param model    {@link ModelMap} - (説明省略)
   * @return {@code roomID}に対応する公開ゲームルームがマネージャにあれば，その公開ゲームルームのランキングのページ．加えて{@code userID}に対応するランキングのエントリがあれば，そのユーザに対しての情報・ハイライトをページに追加する．これら以外であれば，テストページあるいはエラーページを返す．
   */
  @GetMapping("/ranking")
  public String getRanking(@RequestParam(name = "room", required = false) Long roomID,
      @RequestParam(name = "user", required = false) Long userID,
      @RequestParam(name = "DBG", defaultValue = "false") final Boolean DBG_FLAG, Principal prin, ModelMap model) {
    // 返り値となるtemplatesが固定的であるため定数化．
    final String RETURN_TEMPLATE = "/playing/ranking.html";

    // [打ち切り] ルームIDが指定されていない場合，テストページを表示．
    if (roomID == null) {
      return RETURN_TEMPLATE;
    }

    // デバッグ用．
    if (DBG_FLAG) {
      Gameroom testGameroom = new Gameroom();
      testGameroom.setID(-1);
      testGameroom.setRoomName("デバッグ用");

      // [打ち切り] ルームIDが違う場合(-1じゃない場合)，対象のルームがないとしてテストページを表示．
      if (roomID != -1) {
        return RETURN_TEMPLATE;
      }

      PGameRoomRanking testRanking = new PGameRoomRanking();

      testRanking.addEntry(new PGameRoomRankingEntryBean(-1L, "Test1", 100000L));
      testRanking.addEntry(new PGameRoomRankingEntryBean(-2L, "Test2", 55400L));
      testRanking.addEntry(new PGameRoomRankingEntryBean(-3L, "Test3", 184000L));
      testRanking.addEntry(new PGameRoomRankingEntryBean(-4L, "Test4", 8000L));
      testRanking.addEntry(new PGameRoomRankingEntryBean(-5L, "Test5", 250000L));
      testRanking.addEntry(new PGameRoomRankingEntryBean(-6L, "Test6", 55400L));
      testRanking.addEntry(new PGameRoomRankingEntryBean(-7L, "--------------------", 100000000L));
      for (long i = -8L; i > -18L; i--) {
        testRanking.addEntry(new PGameRoomRankingEntryBean(i, "Test", 1000L));
      }

      testRanking.sortByPoint();

      model.addAttribute("gameroom", testGameroom);
      model.addAttribute("ranking", testRanking.getRanking());

      // [打ち切り] ユーザIDが指定されていない場合，普通のランキングを表示．
      if (userID == null) {
        return RETURN_TEMPLATE;
      }

      // [打ち切り] ランキングに指定のユーザIDのエントリがない場合，普通のランキングを表示．
      if (testRanking.getIndexesByUserID().get(userID) == null) {
        return RETURN_TEMPLATE;
      }

      PGameRoomRankingEntryBean testUser = testRanking.getRanking().get(testRanking.getIndexesByUserID().get(userID));
      model.addAttribute("yourID", testUser.ID);
      model.addAttribute("yourRank", testUser.rank);
      model.addAttribute("yourPoint", testUser.point);

      return RETURN_TEMPLATE;
    }
    // デバッグ用 END．

    Gameroom targetGameroom = gameroomMapper.selectGameroomByID(roomID.intValue());

    PublicGameRoom targetPGameRoom = pGameRoomManager.getPublicGameRooms().get(roomID);
    // [打ち切り] 公開ゲームルームがない場合，対象のルームがないとしてテストページを表示．
    if (targetPGameRoom == null) {
      return RETURN_TEMPLATE;
    }

    PGameRoomRanking targetRanking = targetPGameRoom.getRanking();

    targetRanking.sortByPoint();

    model.addAttribute("gameroom", targetGameroom);
    model.addAttribute("ranking", targetRanking.getRanking());

    // [打ち切り] ユーザIDが指定されていない場合，普通のランキングを表示．
    if (userID == null) {
      return RETURN_TEMPLATE;
    }

    // [打ち切り] ランキングに指定のユーザIDのエントリがない場合，普通のランキングを表示．
    if (targetRanking.getIndexesByUserID().get(userID) == null) {
      return RETURN_TEMPLATE;
    }

    PGameRoomRankingEntryBean targetUser = targetRanking.getRanking()
        .get(targetRanking.getIndexesByUserID().get(userID));
    model.addAttribute("yourID", targetUser.ID);
    model.addAttribute("yourRank", targetUser.rank);
    model.addAttribute("yourPoint", targetUser.point);

    // リクエストパラメータが全て正常に指定されている場合，ユーザを軸にしたランキングを表示．
    return RETURN_TEMPLATE;
  }
}
