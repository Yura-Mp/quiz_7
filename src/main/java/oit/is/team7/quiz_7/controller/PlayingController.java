package oit.is.team7.quiz_7.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import oit.is.team7.quiz_7.model.PGameRoomManager;

@Controller
@RequestMapping("/playing")
public class PlayingController {
  @Autowired
  PGameRoomManager pGameRoomManager;

  @GetMapping("/wait")
  public String get_wait(Principal prin, ModelMap model) {
    return "/playing/wait.html";
  }

}
