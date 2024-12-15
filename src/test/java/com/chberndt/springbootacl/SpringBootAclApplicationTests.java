package com.chberndt.springbootacl;

import com.chberndt.springbootacl.entity.Album;
import com.chberndt.springbootacl.repository.AlbumRepository;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

@SpringBootTest
class SpringBootAclApplicationTests {

	@Autowired
	AlbumRepository albumRepository;

	@Test
	void contextLoads() {
	}

	@Test
	@WithMockUser("alice")
	public void givenUserAlice_whenFindAll_thenReturnFiveAlbums() {
		System.out.println("givenUserAlice_whenFindAll_thenReturnFiveAlbums()");
		List<Album> albums = albumRepository.findAll();
		for (Album album : albums) {
			System.out.println(album);
		}
	}

}
