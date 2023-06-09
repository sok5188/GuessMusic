package com.example.guess_music.controller;

import com.example.guess_music.domain.game.Answers;
import com.example.guess_music.domain.game.Game;
import com.example.guess_music.domain.manage.CreateGameForm;
import com.example.guess_music.domain.manage.Music;
import com.example.guess_music.domain.manage.SaveSongForm;
import com.example.guess_music.domain.manage.SearchDTO;
import com.example.guess_music.service.ManagerService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
@Slf4j
@Controller
@RequestMapping("/manage")
public class ManagerController {
    private final ManagerService managerService;
    public ManagerController(ManagerService manageService) {
        this.managerService = manageService;
    }

    @GetMapping("")
    public String enterManager(@ModelAttribute("searchDTO") SearchDTO searchDTO, Model model){
        Page<Game> pagedGames = managerService.getPagedGames(searchDTO.getPageNum());
        List<Game> games = pagedGames.toList();
        searchDTO.setTotalPageSize(pagedGames.getTotalPages());
        model.addAttribute("games",games);
        return "manage/manager";
    }

    @ResponseBody
    @DeleteMapping("")
    public String deleteGame(@RequestParam("gameIndex") Long gameIndex){
        //해당 게임 DB에서 삭제 -> Cascade로 해당 게임의 answers전부 삭제
        if(managerService.delete(gameIndex))
            return "Success";
        else {
            log.error("Fail to delete game");
            return "Fail";
        }
    }

    @GetMapping("/createGame")
    public String createGamePage(){
        return "manage/createGame";
    }

    @PostMapping("/createGame")
    public String createGame(CreateGameForm form){
        Game game = new Game();
        game.setTitle(form.getTitle());
        managerService.join(game);
        return "redirect:/manage";
    }
    @GetMapping("/modifyGame")
    public String modifyGamePage(@ModelAttribute("searchDTO") SearchDTO searchDTO, Model model,@ModelAttribute("gameIndex") Long gameIndex){
        Page<Answers> pagedAnswers = managerService.getPagedAnswers(searchDTO.getPageNum(), gameIndex);
        List<Answers> answers = pagedAnswers.toList();
        searchDTO.setTotalPageSize(pagedAnswers.getTotalPages());
        model.addAttribute("answers",answers);
        return "manage/updateSong";
    }
    @ResponseBody
    @PostMapping("/modifyGame")
    public String modifyGameTitle(@RequestParam("gameIndex") Long gameIndex, @RequestParam("newTitle") String title){
        if(managerService.updateGameTitle(gameIndex,title))
            return "Success";
        else {
            log.error("Fail to update game title");
            return "Fail";
        }
    }
    @DeleteMapping("/modifyGame")
    @ResponseBody
    public String deleteSong(@RequestParam("gameIndex") Long gameIndex,@RequestParam("seq") Long seq){
        if(managerService.delete(gameIndex,seq))
            return "Success";
        else {
            log.error("Fail to delete song");
            return "Fail";
        }

    }
    @ResponseBody@GetMapping("/songList")
    public List<Answers> modifySong(@RequestParam("gameIndex") Long gameIndex){
        return managerService.getAnswerList(gameIndex);
    }

    @GetMapping("/upload")
    public String uploadSong(@RequestParam("gameIndex")Long gameIndex,Model model){
        model.addAttribute("gameIndex",gameIndex);
        return "manage/uploadSong";
    }

    @PostMapping("/upload")
    public String saveSong(SaveSongForm form,Model model) throws IOException {
        Long gameIndex = form.getGameIndex();
        log.info("gameIndex : "+gameIndex);
        String[] split = form.getMp3().getOriginalFilename().split("\\.");
        String extension= split[split.length-1];
        //mp3 파일만 허용
        if(extension.equals("mp3")&&split.length>1){
            //파일을 file db에 저장
            Music store = managerService.storeMusic(form.getMp3(),gameIndex);
            //입력한 노래 정보를 db에 저장
            Long result = managerService.storeAnswers(form.getAnswer(), form.getSinger(), form.getInitial(), gameIndex, store);
            log.info("store file result: "+result);
            if(result!=-1){
                String red="redirect:/manage/upload?gameIndex="+gameIndex;
                return red;
            }else{
                log.error("Fail to upload song");
                return "Bad";
            }
        }else{
            log.error("Invalid File");
            return "Bad";
        }
    }
    @ResponseBody
    @PostMapping("/updateAnswer")
    public String updateAnswer(@RequestParam("id") Long id,@RequestParam("answer") String answer){
        System.out.println("get update Answer req : "+id);
        if(managerService.updateAnswer(id,answer))
            return "Success";
        else
        {
            log.error("Fail to update answer");
            return "Fail";
        }
    }

    @ResponseBody
    @PostMapping("/addAnswer")
    public String addAnswer(@RequestParam("seq") Long seq,@RequestParam("answer") String answer,@RequestParam("gameIndex") Long gameIndex){
        System.out.println("get add Answer req : "+seq);
        Answers saved = managerService.addAnswer(gameIndex, seq, answer);
        if(saved.getAnswer()!=null)
            return "Success";
        else
        {
            log.error("Fail to add answer");
            return "Fail";
        }

    }
    @ResponseBody
    @DeleteMapping("/updateAnswer")
    public String deleteAnswer(@RequestParam("ansId") Long id){
        Answers answerById = managerService.findAnswerById(id);
        if(answerById==null)
            return "Fail";
        managerService.deleteAnswer(id);
        String musicId = answerById.getMusic().getId();
        Long gameIndex = answerById.getGameIndex().getGameIndex();
        managerService.validateMusic(musicId,gameIndex);
        return "Success";
    }

    //for Test DB
    @GetMapping("/increase")
    public String increaseView(Model model){
        List<Long> size = managerService.getSize();
        model.addAttribute("memberNum",size.get(0));
        model.addAttribute("answerNum",size.get(1));
        model.addAttribute("songNum",size.get(2));
        model.addAttribute("gameNum",size.get(3));
        return "manage/increase";
    }
    @PostMapping("/increase")
    public String increaseDB(@RequestParam("type") String type,Model model){
        System.out.println("type : "+type);
        managerService.increaseDB(type);
        List<Long> size = managerService.getSize();
        model.addAttribute("memberNum",size.get(0));
        model.addAttribute("answerNum",size.get(1));
        model.addAttribute("songNum",size.get(2));
        model.addAttribute("gameNum",size.get(3));
        return "manage/increase";
    }

}
