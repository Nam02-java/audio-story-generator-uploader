package com.example.speech.aiservice.vn.model.repository.novel;

import com.example.speech.aiservice.vn.model.entity.novel.Novel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NovelRepository extends JpaRepository<Novel, Long> {
    Novel findByTitle(String title);

    List<Novel> findAll();

    boolean existsByTitle(String title);

    boolean existsByLink(String link);

}
