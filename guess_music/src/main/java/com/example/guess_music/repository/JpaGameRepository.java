package com.example.guess_music.repository;

import com.example.guess_music.domain.Answers;
import com.example.guess_music.domain.Game;
import com.example.guess_music.domain.QAnswers;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class JpaGameRepository implements GameRepository{
    private final EntityManager em;

    public JpaGameRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Answers save(Answers answers) {
        em.persist(answers);
        return answers;
    }

    @Override
    public List<Answers> findAnswer(Long gameIndex) {
        //나중
        return null;
    }

    @Override
    public List<Long> findNumGameByGameIndex(Long gameIndex) {
        //game index로 game내의 노래의 수를 리턴
        QAnswers qAnswers=QAnswers.answers;
        JPAQueryFactory queryFactory=new JPAQueryFactory(em);
        JPAQuery<Long> query = queryFactory.from(qAnswers).select(qAnswers.seq.countDistinct()).where(qAnswers.gameIndex.gameIndex.eq(gameIndex));
        List<Long> fetch = query.fetch();
        return fetch;
    }

    @Override
    public Optional<List<String>> findAnswerBySeq(Long gameIndex, int seq) {
        List<String> resultList = em.createQuery("select m.answer from Answers m where m.gameIndex.gameIndex= :gameIndex and m.seq=:seq", String.class).setParameter("gameIndex",gameIndex).setParameter("seq",seq).getResultList();
        return Optional.ofNullable(resultList);
    }

    @Override
    public Optional<String> findSingerBySeq(Long gameIndex, int seq) {
        String singleResult = em.createQuery("select m.singer from Answers m where m.gameIndex.gameIndex= :gameIndex and m.seq=:seq", String.class).setParameter("gameIndex", gameIndex).setParameter("seq", seq).getSingleResult();
        System.out.println("find singer : "+ singleResult);
        return Optional.ofNullable(singleResult);
    }

    @Override
    public Optional<String> findInitialBySeq(Long gameIndex, int seq) {
        String singleResult = em.createQuery("select m.initial from Answers m where m.gameIndex.gameIndex= :gameIndex and m.seq=:seq", String.class).setParameter("gameIndex", gameIndex).setParameter("seq", seq).getSingleResult();
        System.out.println("find initial : "+ singleResult);
        return Optional.ofNullable(singleResult);
    }

    @Override
    public Optional<List<Game>> findGameList() {
        return Optional.ofNullable(em.createQuery("select g from Game g", Game.class).getResultList());
    }

}
