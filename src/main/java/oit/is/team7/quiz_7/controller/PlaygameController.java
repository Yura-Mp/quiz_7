package oit.is.team7.quiz_7.controller;

import java.util.ArrayList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import oit.is.team7.quiz_7.model.PGameRoomManager;
import oit.is.team7.quiz_7.model.PublicGameRoom;

@Controller
@RequestMapping("/playgame")
public class PlaygameController {
  private final Logger logger = LoggerFactory.getLogger(PlaygameController.class);

  @Autowired
  PGameRoomManager pGameRoomManager;

  @GetMapping
  public String playgame(ModelMap model) {
    Set<Long> roomIDSet = this.pGameRoomManager.getPublicGameRooms().keySet();
    ArrayList<PublicGameRoom> publicGameRoomList = new ArrayList<PublicGameRoom>();
    for (Long roomID : roomIDSet) {
      PublicGameRoom publicGameRoom = this.pGameRoomManager.getPublicGameRooms().get(roomID);
      publicGameRoomList.add(publicGameRoom);
    }
    model.addAttribute("publicGameroomList", publicGameRoomList);
    logger.info("PGRManager.pgrs:" + this.pGameRoomManager.getPublicGameRooms());
    return "playgame.html";
  }
}
