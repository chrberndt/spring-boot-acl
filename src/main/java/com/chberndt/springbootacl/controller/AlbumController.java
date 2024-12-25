package com.chberndt.springbootacl.controller;

import com.chberndt.springbootacl.entity.Album;
import com.chberndt.springbootacl.repository.AlbumRepository;
import com.chberndt.springbootacl.service.AlbumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Christian Berndt
 */
@RestController
public class AlbumController {

	private static final Logger log = LoggerFactory.getLogger(AlbumController.class);

	private final AlbumRepository repository;

	private final AlbumService service;

	public AlbumController(AlbumRepository albumRepository, AlbumService albumService) {
		this.repository = albumRepository;
		this.service = albumService;
	}

	@GetMapping("/albums")
	List<Album> all() {
		return service.getAll();
	}

	@PostMapping("/albums")
	Album newAlbum(@RequestBody Album newAlbum) {
		return service.createAlbum(newAlbum);
	}

	@GetMapping("/albums/{id}")
	Album one(@PathVariable Long id) {
		return service.getAlbum(id);
	}

	@PutMapping("/albums/{id}")
	Album replaceAlbum(@RequestBody Album newAlbum, @PathVariable Long id) {
		log.info("replaceAlbum()");
		log.info("album: " + newAlbum.toString());
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
