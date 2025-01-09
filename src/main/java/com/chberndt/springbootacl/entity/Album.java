package com.chberndt.springbootacl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * @author Christian Berndt
 */
@Entity
public class Album {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String artist;

	private String owner;

	private String title;

	protected Album() {
	}

	public Album(String artist, String title) {
		this.artist = artist;
		this.title = title;
	}

	@Override
	public String toString() {
		return String.format("Album[id: %d, owner: %s, artist: '%s', title: '%s']", id, owner, artist, title);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getId() {
		return id;
	}

	public String getArtist() {
		return artist;
	}

	public String getOwner() {
		return owner;
	}

	public String getTitle() {
		return title;
	}

}
