package com.example.guess_music.controller;

import com.example.guess_music.domain.Game;
import com.example.guess_music.domain.Result;
import com.example.guess_music.service.GameService;
import com.example.guess_music.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
public class GameController {
    private final GameService gameService;
    private int seq,score;
    private Long gameIndex;
    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
        gameIndex=1L;
        seq=1;
        score=0;
    }
    @Autowired
    private HttpSession session;
    @GetMapping("/select")
    public String selectGame(){
        return "/game/select";
    }


    @ResponseBody
    @GetMapping("/gameList")
    public List<Game> gameList(){
        List<Game> gameList = gameService.getGameList();
        System.out.println("got list from service : "+gameList);
        for (Game g :
                gameList) {
            System.out.println(g.getGameIndex()+" / "+g.getTitle()+" / "+g.getSongNum());
        }
        if(gameList.isEmpty()){
            //??
        }
        return gameList;
    }

    @GetMapping("/testGame")
    public String createtestGame(Model model){
        //HttpSession session=request.getSession();
        if(session.getAttribute("seq")!=null){
            seq=(int)session.getAttribute("seq");
        }
        String music=gameIndex+"-"+seq;
        Long gameSize = gameService.getGameSize(1L);
        System.out.println("now target music is : "+music+"and game index,seq : "+gameIndex+" / "+seq);
        model.addAttribute("music",music).addAttribute("remainSong",gameSize-seq+1).addAttribute("totalSong",gameSize);

        return "/game/testGame";
    }

    @GetMapping("/testGame/checkAnswer")
    @ResponseBody
    public Result testGame(@RequestParam("target") String target){
        System.out.println("Got target : "+target);

        Result result=new Result();
        Long gameSize = gameService.getGameSize(1L);
        Result results=gameService.getAnswers(target,gameIndex,seq);
        result.setAnswer(results.getAnswer());

        if(target.equals("skip")|| results.getResult() =="Right"){
            //score 처리 부분 만들어야 함
            if(results.getResult()=="Right")
                score++;

            System.out.println("entered");
            session.setAttribute("seq",++seq);
            if(seq>gameSize) {
                result.setResult("Game End");
                seq=1;
                session.setAttribute("seq",seq);
            }else{
                result.setResult("Next Song");
            }
        }else{
            result.setResult("Wrong Answer");
        }
        System.out.println("will send data : "+result);
        return result;
    }

    @GetMapping("/testGame/hint")
    @ResponseBody
    public String Hint(@RequestParam("type") String type){
        System.out.println("at controller in Hint : "+gameService.getHint(type,gameIndex,seq));
        return gameService.getHint(type,gameIndex,seq);
    }
}
