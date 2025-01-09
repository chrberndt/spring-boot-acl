package com.chberndt.springbootacl;

import com.chberndt.springbootacl.entity.Album;
import com.chberndt.springbootacl.service.AlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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

	private final String MODIFIED = "MODIFIED";

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
		assertThrows(AccessDeniedException.class,
				() -> service.createAlbum(SecurityContextHolder.getContext().getAuthentication(),
						new Album("Taylor Swift", "Reputation")));
	}

	@Test
	@WithAnonymousUser
	public void anonymousUser_shouldNot_updateAlbum() {
		Album album = service.getAlbum(1);
		album.setArtist(MODIFIED);
		album.setOwner(MODIFIED);
		assertThrows(AccessDeniedException.class, () -> service.updateAlbum(1, album));
	}

	@Test
	@WithAnonymousUser
	public void anonymousUser_shouldNot_deleteAlbum() {
		assertThrows(AccessDeniedException.class, () -> service.deleteAlbum(1));
	}

	@Test
	@DirtiesContext
	public void authenticatedUser_should_createAlbum() {
		Album album = service.createAlbum(SecurityContextHolder.getContext().getAuthentication(),
				new Album("Taylor Swift", "Reputation"));
		assertThat(album).isNotNull();
		assertThat(album.getId()).isEqualTo(11);
		assertThat(album.getOwner()).isEqualTo("user");
	}

	@Test
	@WithMockUser("alice")
	@DirtiesContext
	public void authenticatedUser_should_updateOwnedAlbum() {
		// TODO: look up album by owner, then use random album
		Album album = service.getAlbum(1);
		album.setId(10L);
		album.setArtist(MODIFIED);
		album.setOwner(MODIFIED);
		Album modifiedAlbum = service.updateAlbum(1, album);
		assertThat(modifiedAlbum).isNotNull();
		assertThat(modifiedAlbum.getId()).isEqualTo(1);
		assertThat(modifiedAlbum.getOwner()).isEqualTo("alice");
		assertThat(modifiedAlbum.getArtist()).isEqualTo(MODIFIED);

	}

	@Test
	@WithMockUser("alice")
	@DirtiesContext
	public void authenticatedUser_should_deleteOwnedAlbum() {
		// TODO: select albums owned by alice and delete a random album from alice
		// TODO: assert that the corresponding ACLs have been removed
		service.deleteAlbum(1);
		assertThat(service.getAlbumCount()).isEqualTo(9);
	}

	@Test
	@WithMockUser("alice")
	public void authenticatedUser_shouldNot_updateForeignAlbum() {
		// TODO: select albums owned by bob and try to update a random album from bob
		Album album = service.getAlbum(6);
		album.setArtist(MODIFIED);
		album.setOwner(MODIFIED);
		assertThrows(AccessDeniedException.class, () -> service.updateAlbum(6, album));
	}

	@Test
	@WithMockUser("alice")
	public void authenticatedUser_shouldNot_deleteForeignAlbum() {
		// TODO: select albums owned by bob and try to delete a random album from bob
		assertThrows(AccessDeniedException.class, () -> service.deleteAlbum(6));
	}

	@Test
	public void roleAdmin_should_updateAnyAlbum() {
		// TODO: use random album 1-10
	}

	@Test
	public void roleAdmin_should_deleteAnyAlbum() {
		// TODO: use random album 1-10
		// TODO: assert that the corresponding ACLs have been removed
	}

}
