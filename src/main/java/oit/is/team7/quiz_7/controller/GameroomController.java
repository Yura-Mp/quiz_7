package oit.is.team7.quiz_7.controller;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
// import org.h2.engine.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.team7.quiz_7.model.Gameroom;
import oit.is.team7.quiz_7.model.GameroomMapper;
import oit.is.team7.quiz_7.model.UserAccountMapper;

@Controller
@RequestMapping("/gameroom")
public class GameroomController {
  @Autowired
  UserAccountMapper userAccountMapper;
  @Autowired
  GameroomMapper gameroomMapper;

  @GetMapping
  public String gameroom(Principal principal, ModelMap model) {
    int userId = userAccountMapper.selectUserAccountByUsername(principal.getName()).getId();
    ArrayList<Gameroom> gameroomList = gameroomMapper.selectGameroomByHostUserID(userId);
    model.addAttribute("gameroomList", gameroomList);
    return "gameroom.html";
  }

  @GetMapping("create")
  public String get_create_gameroom() {
    return "gameroom/create.html";
  }

  @PostMapping("create")
  public String post_create_gameroom(@RequestParam String game_room_name, @RequestParam String description,
      Principal principal, ModelMap model) {
    int hostId = userAccountMapper.selectUserAccountByUsername(principal.getName()).getId();
    Gameroom gameroom = gameroomMapper.selectGameroomByHostAndName(hostId, game_room_name);
    if (gameroom != null) {
      model.addAttribute("error_result", "既に同名のゲームルームが存在しています");
      return "gameroom/create_result.html";
    }
    Gameroom newGameroom = new Gameroom();
    newGameroom.setHostUserID(hostId);
    newGameroom.setRoomName(game_room_name);
    newGameroom.setDescription(description);
    gameroomMapper.insertGameroom(newGameroom);
    model.addAttribute("result", "新規のゲームルームを作成しました");
    return "gameroom/create_result.html";
  }

  @GetMapping("/prepare_open")
  public String prepare_open_gameroom(@RequestParam("room") int roomID, ModelMap model) {
    return "";
  }

  @GetMapping("/delete")
  public String delete_gameroom(@RequestParam("room") int roomID, ModelMap model) {
    return "";
  }

}
