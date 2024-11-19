package oit.is.team7.quiz_7.controller;

// import org.h2.engine.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.team7.quiz_7.model.UserAccount;
import oit.is.team7.quiz_7.model.UserAccountMapper;

@Controller
public class IndexController {
  @Autowired
  UserAccountMapper userAccountMapper;

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

  @PostMapping("/register_useracc/send")
  public String register_useracc_send(@RequestParam String username, @RequestParam String password, ModelMap model) {
    // validate username and password
    UserAccount userAccount = userAccountMapper.selectByUsername(username);
    if (userAccount != null) {
      model.addAttribute("result", "Username already exists.");
      return "register_useracc_send.html";
    }
    UserAccount newUserAccount = new UserAccount();
    newUserAccount.setUserName(username);
    newUserAccount.setPass(password);
    newUserAccount.setRoles("USER");
    userAccountMapper.insertUserAccount(newUserAccount);
    return "register_useracc_send.html";
  }
}
