package com.chberndt.springbootacl;

import com.chberndt.springbootacl.entity.Album;
import com.chberndt.springbootacl.service.AlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Christian Berndt
 */
@SpringBootTest
@WithMockUser
public class AlbumSecurityTests {

	@Autowired
	private AlbumService service;

	@Test
	void contextLoads() throws Exception {
		assertThat(service).isNotNull();
	}

	@Test
	@WithAnonymousUser
	public void anonymousUser_should_getAlbum() {
		Album album = service.getAlbum(1);
		assertThat(album).isNotNull();
		assertThat(album.getId()).isEqualTo(1);
	}

	@Test
	@WithAnonymousUser
	public void anonymousUser_should_getAllAlbums() {
		List<Album> albums = service.getAll();
		assertThat(albums).isNotNull();
		assertThat(albums.size()).isEqualTo(10);
	}

	@Test
	@WithAnonymousUser
	public void anonymousUser_shouldNot_createAlbum() {
		assertThrows(AccessDeniedException.class, () -> service.createAlbum(new Album("Taylor Swift", "Reputation")));
	}

	@Test
	@WithAnonymousUser
	public void anonymousUser_shouldNot_updateAlbum() {
		// TODO
	}

	@Test
	@WithAnonymousUser
	public void anonymousUser_shouldNot_deleteAlbum() {
		// TODO
	}

	@Test
	@DirtiesContext
	public void authenticatedUser_should_createAlbum() {
		Album album = service.createAlbum(new Album("Taylor Swift", "Reputation"));
		assertThat(album).isNotNull();
		assertThat(album.getId()).isEqualTo(11);
		// TODO: assertThat album is owned by "user"
	}

	@Test
	public void authenticatedUser_should_updateOwnedAlbum() {
		// TODO: use random album 1-5
	}

	@Test
	public void authenticatedUser_should_deleteOwnedAlbum() {
		// TODO: use random album 1-5
	}

	@Test
	public void roleAdmin_should_updateAnyAlbum() {
		// TODO: use random album 1-10
	}

	@Test
	public void roleAdmin_should_deleteAnyAlbum() {
		// TODO: use random album 1-10
	}

}
