package com.chberndt.springbootacl.controller;

import com.chberndt.springbootacl.entity.Album;
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

	private final AlbumService service;

	public AlbumController(AlbumService albumService) {
		this.service = albumService;
	}

	@GetMapping("/albums")
	List<Album> all() {
		return service.getAll();
	}

	@GetMapping("/albums/count")
	long count() {
		return service.getAlbumCount();
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
	Album replaceAlbum(@RequestBody Album album, @PathVariable Long id) {
		return service.updateAlbum(id, album);
	}

	@DeleteMapping("/albums/{id}")
	void deleteAlbum(@PathVariable Long id) {
		service.deleteAlbum(id);
	}

}
