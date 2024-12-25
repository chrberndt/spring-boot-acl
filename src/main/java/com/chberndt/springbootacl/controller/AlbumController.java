package com.chberndt.springbootacl.controller;

import com.chberndt.springbootacl.entity.Album;
import com.chberndt.springbootacl.repository.AlbumRepository;
import com.chberndt.springbootacl.service.AlbumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
	Album newAlbum(@RequestBody Album newAlbum, Principal principal) {
		return service.createAlbum(principal, newAlbum);
	}

	@GetMapping("/albums/{id}")
	Album one(@PathVariable Long id) {
		return service.getAlbum(id);
	}

	@PutMapping("/albums/{id}")
	Album replaceAlbum(@RequestBody Album updatedAlbum, @PathVariable Long id) {
		log.info("replaceAlbum()");
		log.info("album: " + updatedAlbum.toString());
		return service.updateAlbum(id, updatedAlbum);
	}

	@DeleteMapping("/albums/{id}")
	void deleteAlbum(@PathVariable Long id) {
		repository.deleteById(id);
	}

}
