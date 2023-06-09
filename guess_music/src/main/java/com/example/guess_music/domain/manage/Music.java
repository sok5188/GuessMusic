package com.example.guess_music.domain.manage;
import com.example.guess_music.domain.game.Answers;
import com.example.guess_music.domain.game.Game;
import jakarta.persistence.*;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Music")
@NoArgsConstructor
public class Music {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;

    private String type;

    @Lob
    private byte[] data;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @ManyToOne
    @JoinColumn(name = "gameIndex")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Game game;
//    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Answers> answers=new ArrayList<>();
    public Music(String name, String type, byte[] data, Game game) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.game = game;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


}
