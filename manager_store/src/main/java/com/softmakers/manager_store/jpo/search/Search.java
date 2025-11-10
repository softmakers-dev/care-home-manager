package com.softmakers.manager_store.jpo.search;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Table(name = "searches")
public class Search {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "search_count")
    private Integer count;

    @Column(name = "DTYPE", insertable = false, updatable = false)
    private String dtype;

    protected Search() {
        this.count = 0;
    }

    public void upCount() {
        this.count++;
    }

    @Transient
    public void setDtype() {
        this.dtype = getClass().getAnnotation(DiscriminatorValue.class).value();
    }
}
