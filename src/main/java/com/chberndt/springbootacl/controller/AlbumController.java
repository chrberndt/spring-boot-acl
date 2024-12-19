package com.chberndt.springbootacl.controller;

import com.chberndt.springbootacl.entity.Album;
import com.chberndt.springbootacl.exception.AlbumNotFoundException;
import com.chberndt.springbootacl.repository.AlbumRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Christian Berndt
 */
@RestController
public class AlbumController {

	private final AlbumRepository repository;

	public AlbumController(AlbumRepository albumRepository) {
		this.repository = albumRepository;
	}

	@GetMapping("/albums")
	List<Album> all() {
		return repository.findAll();
	}

	@PostMapping("/albums")
	Album newAlbum(@RequestBody Album newAlbum) {
		return repository.save(newAlbum);
	}

	@GetMapping("/albums/{id}")
	Album one(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));
	}

	@PutMapping("/albums/{id}")
	Album replaceAlbum(@RequestBody Album newAlbum, @PathVariable Long id) {
		return repository.findById(id).map(album -> {
			album.setArtist(newAlbum.getArtist());
			album.setTitle(newAlbum.getTitle());
			return repository.save(album);
		}).orElseGet(() -> {
			return repository.save(newAlbum);
		});
	}

	@DeleteMapping("/albums/{id}")
	void deleteAlbum(@PathVariable Long id) {
		repository.deleteById(id);
	}

}
