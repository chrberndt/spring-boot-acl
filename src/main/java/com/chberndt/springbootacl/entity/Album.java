package com.chberndt.springbootacl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Album {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String artist;
    private String title;

    protected Album() {}

    public Album(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format(
                "Album[id=%d, artist='%s', title='%s']",
                id, artist, title);
    }

    public Long getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }
}
