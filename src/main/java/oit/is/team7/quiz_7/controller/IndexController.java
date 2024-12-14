package oit.is.team7.quiz_7.controller;

import java.util.ArrayList;

// import org.h2.engine.User;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.team7.quiz_7.model.GameroomMapper;
import oit.is.team7.quiz_7.model.UserAccount;
import oit.is.team7.quiz_7.model.UserAccountMapper;
import oit.is.team7.quiz_7.security.Quiz7AuthConfiguration;

@Controller
public class IndexController {
  @Autowired
  UserAccountMapper userAccountMapper;
  @Autowired
  GameroomMapper gameroomMapper;

  @GetMapping("/")
  public String index_page() {
      return "index.html";
  }

  // @GetMapping("/index.html")
  // public String index_page_old() {
  //     return "index.html";
  // }

  @GetMapping("/main")
  public String main_page() {
    return "main.html";
  }

  @GetMapping("/join_gameroom")
  public String join_gameroom() {
    return "";
  }

  @GetMapping("/login")
  public String login() {
    return "login.html";
  }

  @GetMapping("/logout")
  public String logout() {
    return "logout.html";
  }

  @GetMapping("/register_useracc")
  public String register_useracc() {
    return "register_useracc.html";
  }

  @Transactional
  @PostMapping("/register_useracc/send")
  public String register_useracc_send(@RequestParam String username, @RequestParam String password, ModelMap model) {
    // validate username and password
    UserAccount userAccount = userAccountMapper.selectUserAccountByUsername(username);
    if (userAccount != null) {
      model.addAttribute("error_result", "このユーザネームはすでに存在しています");
      return "register_useracc.html";
    }
    password = Quiz7AuthConfiguration.passwordEncoder().encode(password);
    UserAccount newUserAccount = new UserAccount();
    newUserAccount.setUserName(username);
    newUserAccount.setPass(password);
    ArrayList<String> roles = new ArrayList<>();
    roles.add("USER");
    newUserAccount.setRoles(roles);

    userAccountMapper.insertUserAccount(newUserAccount);
    newUserAccount = userAccountMapper.selectUserAccountByUsername(username);
    int id = newUserAccount.getId();
    for (String role : roles) {
      userAccountMapper.insertUserRole(id, role);
    }

    model.addAttribute("result", "ユーザアカウントの作成に成功しました");
    return "register_useracc.html";
  }

}
