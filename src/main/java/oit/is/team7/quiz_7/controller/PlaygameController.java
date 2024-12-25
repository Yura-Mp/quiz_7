package oit.is.team7.quiz_7.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import oit.is.team7.quiz_7.model.Gameroom;
import oit.is.team7.quiz_7.model.GameroomMapper;
import oit.is.team7.quiz_7.model.PGameRoomManager;
import oit.is.team7.quiz_7.model.PublicGameRoom;

@Controller
@RequestMapping("/playgame")
public class PlaygameController {
  private final Logger logger = LoggerFactory.getLogger(PlaygameController.class);

  @Autowired
  PGameRoomManager pGameRoomManager;

  @Autowired
  GameroomMapper gameroomMapper;

  @GetMapping
  public String playgame(ModelMap model) {
    ArrayList<Gameroom> publicRooms = gameroomMapper.selectGameroomByPublished(true);
    ArrayList<PublicGameRoom> publicGameRoomList = new ArrayList<PublicGameRoom>();
    for (Gameroom room : publicRooms) {
      PublicGameRoom publicGameRoom = this.pGameRoomManager.getPublicGameRooms().get((long) room.getID());
      if (publicGameRoom != null) {
        publicGameRoomList.add(publicGameRoom);
      }
    }
    model.addAttribute("publicGameroomList", publicGameRoomList);
    logger.info("PGRManager.pgrs:" + this.pGameRoomManager.getPublicGameRooms());
    return "playgame.html";
  }
}
