package com.example.guess_music.service;


import com.example.guess_music.domain.ChatRoom;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;
@Service

public class ChatService {
    private Map<String, ChatRoom> chatRooms;

    @PostConstruct
    //의존관게 주입완료되면 실행되는 코드
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

    //채팅방 불러오기
    public List<ChatRoom> findAllRoom() {
        //채팅방 최근 생성 순으로 반환
        List<ChatRoom> result = new ArrayList<>(chatRooms.values());
        Collections.reverse(result);
        return result;
    }

    //채팅방 하나 불러오기
    public ChatRoom findById(String roomId) {
        return chatRooms.get(roomId);
    }

    //채팅방 생성
//    public ChatRoom createRoom(String name) {
//        ChatRoom chatRoom = ChatRoom.create(1L,name);
//        chatRooms.put(chatRoom.getRoomId(), chatRoom);
//        return chatRoom;
//    }
}
