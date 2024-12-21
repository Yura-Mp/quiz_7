package oit.is.team7.quiz_7.controller;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import oit.is.team7.quiz_7.model.Gameroom;
import oit.is.team7.quiz_7.model.GameroomMapper;
import oit.is.team7.quiz_7.model.HasQuiz;
import oit.is.team7.quiz_7.model.HasQuizMapper;
import oit.is.team7.quiz_7.model.QuizFormatListMapper;
import oit.is.team7.quiz_7.model.QuizJson;
import oit.is.team7.quiz_7.model.QuizTable;
import oit.is.team7.quiz_7.model.QuizTableMapper;
import oit.is.team7.quiz_7.model.UserAccountMapper;

@Controller
@RequestMapping("/gameroom")
public class GameroomController {
  @Autowired
  UserAccountMapper userAccountMapper;
  @Autowired
  GameroomMapper gameroomMapper;
  @Autowired
  HasQuizMapper hasQuizMapper;
  @Autowired
  QuizTableMapper quizTableMapper;
  @Autowired
  QuizFormatListMapper quizFormatListMapper;

  @GetMapping
  public String gameroom(Principal principal, ModelMap model) {
    int userId = userAccountMapper.selectUserAccountByUsername(principal.getName()).getId();
    ArrayList<Gameroom> gameroomList = gameroomMapper.selectGameroomByHostUserID(userId);
    model.addAttribute("gameroomList", gameroomList);
    return "gameroom/gameroom.html";
  }

  @GetMapping("/create")
  public String get_create_gameroom() {
    return "gameroom/create.html";
  }

  @PostMapping("/create")
  public String post_create_gameroom(@RequestParam String roomName, @RequestParam String description,
      Principal principal, ModelMap model) {
    int hostId = userAccountMapper.selectUserAccountByUsername(principal.getName()).getId();
    Gameroom gameroom = gameroomMapper.selectGameroomByHostAndName(hostId, roomName);
    if (gameroom != null) {
      model.addAttribute("error_result", "既に同名のゲームルームが存在しています");
      return "gameroom/create_result.html";
    }
    Gameroom newGameroom = new Gameroom();
    newGameroom.setHostUserID(hostId);
    newGameroom.setRoomName(roomName);
    newGameroom.setDescription(description);
    gameroomMapper.insertGameroom(newGameroom);
    model.addAttribute("result", "新規のゲームルームを作成しました");
    model.addAttribute("gameroom", gameroomMapper.selectGameroomByHostAndName(hostId, roomName));
    return "gameroom/create_result.html";
  }

  @GetMapping("/prepare_open")
  public String get_prepare_open_gameroom(@RequestParam("room") int roomID, ModelMap model) {
    model.addAttribute("gameroom", gameroomMapper.selectGameroomByID(roomID));
    return "gameroom/prepare_open.html";
  }

  @PostMapping("/prepare_open")
  public String post_prepare_open_gameroom() {
    return "";
  }

  @GetMapping("/delete")
  public String delete_gameroom(@RequestParam("room") int roomID, ModelMap model) {
    model.addAttribute("gameroom", gameroomMapper.selectGameroomByID(roomID));
    ArrayList<HasQuiz> quizIDList = hasQuizMapper.selectHasQuizByRoomID(roomID);
    ArrayList<QuizTable> quizList = new ArrayList<QuizTable>();
    for (HasQuiz hasQuiz : quizIDList) {
      quizList.add(quizTableMapper.selectQuizTableByID(hasQuiz.getQuizID()));
    }
    model.addAttribute("quizList", quizList);
    return "gameroom/delete_gameroom.html";
  }

  @GetMapping("/delete/confirm")
  public String delete_gameroom_confirm(@RequestParam("room") int roomID, ModelMap model) {
    hasQuizMapper.deleteHasQuizByRoomID(roomID);
    gameroomMapper.deleteGameroomByID(roomID);
    return "redirect:/gameroom";
  }

  @GetMapping("/register_quiz")
  public String register_quiz(@RequestParam("room") int roomID, ModelMap model) {
    model.addAttribute("gameroom", gameroomMapper.selectGameroomByID(roomID));
    ArrayList<HasQuiz> quizIDList = hasQuizMapper.selectHasQuizByRoomID(roomID);
    ArrayList<QuizTable> quizList = new ArrayList<QuizTable>();
    for (HasQuiz hasQuiz : quizIDList) {
      quizList.add(quizTableMapper.selectQuizTableByID(hasQuiz.getQuizID()));
    }
    model.addAttribute("quizList", quizList);
    return "gameroom/register_quiz.html";
  }

  @GetMapping("/edit_quiz")
  public String get_edit_quiz(@RequestParam("room") int roomID, ModelMap model) {
    model.addAttribute("gameroom", gameroomMapper.selectGameroomByID(roomID)); // 編集対象のゲームルームの情報を追加

    return "gameroom/edit_quiz.html";
  }

  @PostMapping("/edit_quiz")
  public String post_edit_quiz(@RequestParam("room") int roomID, @RequestParam String title,
      @RequestParam String description, @RequestParam int correct_num, @RequestParam String choice1,
      @RequestParam String choice2, @RequestParam String choice3, @RequestParam String choice4,
      Principal principal, ModelMap model) throws JsonProcessingException {

    QuizJson quizJson = new QuizJson();
    quizJson.correct = correct_num;
    quizJson.choices = new String[] { choice1, choice2, choice3, choice4 };
    ObjectMapper objectMapper = new ObjectMapper();

    model.addAttribute("gameroom", gameroomMapper.selectGameroomByID(roomID)); // 編集対象のゲームルームの情報を追加
    String[] fields = { title, description, choice1, choice2, choice3, choice4 };
    for (String field : fields) {
      if (field == null || field.trim().isEmpty()) {
        model.addAttribute("error_result", "【失敗】空白の項目が存在します");
        return "gameroom/edit_quiz.html";
      }
    }

    int hostId = userAccountMapper.selectUserAccountByUsername(principal.getName()).getId();
    int formatId = quizFormatListMapper.selectQuizFormatByFormat("4choices").getId();
    String jsonStr = objectMapper.writeValueAsString(quizJson);
    QuizTable newQuizTable = new QuizTable();
    newQuizTable.setQuizFormatID(formatId);
    newQuizTable.setAuthorID(hostId);
    newQuizTable.setTitle(title);
    newQuizTable.setDescription(description);
    newQuizTable.setQuizJSON(jsonStr);
    quizTableMapper.insertQuizTable(newQuizTable);

    HasQuiz latestHasQuiz = hasQuizMapper.maxIndexByRoomID(roomID);
    HasQuiz newHasQuiz = new HasQuiz();
    newHasQuiz.setRoomID(roomID);
    newHasQuiz.setQuizID(newQuizTable.getID());
    if (latestHasQuiz != null) {
      newHasQuiz.setIndex(latestHasQuiz.getIndex() + 1);
    } else {
      newHasQuiz.setIndex(1);
    }
    hasQuizMapper.insertHasQuiz(newHasQuiz);

    model.addAttribute("result", "【成功】問題を作成しました");
    return "gameroom/edit_quiz.html";
  }
}
