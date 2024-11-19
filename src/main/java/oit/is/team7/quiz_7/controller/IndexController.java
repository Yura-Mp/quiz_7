package oit.is.team7.quiz_7.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

  @GetMapping("/main")
  public String main_page() {
    return "main.html";
  }

  @GetMapping("/create_gameroom")
  public String create_gameroom() {
    return "";
  }

  @GetMapping("/join_gameroom")
  public String join_gameroom() {
    return "";
  }

  @GetMapping("/register_useracc")
  public String register_useracc() {
    return "register_useracc.html";
  }
}
