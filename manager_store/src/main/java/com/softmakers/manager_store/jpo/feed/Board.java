package com.softmakers.manager_store.jpo.feed;

import com.softmakers.manager_domain.entity.feed.BoardDto;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.beans.BeanUtils;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "board_name")
    private String boardName;

    @Column(name = "description")
    private String description;

    public Board( BoardDto boardDto ) {
        BeanUtils.copyProperties( boardDto, this );
    }

    public BoardDto toDomain() {
        BoardDto boardDto = new BoardDto();
        BeanUtils.copyProperties( this, boardDto );
        return boardDto;
    }
}
