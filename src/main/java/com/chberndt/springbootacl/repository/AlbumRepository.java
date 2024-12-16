package com.chberndt.springbootacl.repository;

import com.chberndt.springbootacl.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {

	@PostFilter("hasPermission(filterObject, 'READ')")
	List<Album> findAll();

	@PostAuthorize("hasPermission(returnObject, 'READ')")
	List<Album> findByArtist(String artist);

	@PostFilter("hasPermission(filterObject, 'READ')")
	List<Album> findByTitle(String title);

	@PostFilter("hasPermission(filterObject, 'READ')")
	Album findById(long id);

	// @PreAuthorize("hasPermission(#album, 'WRITE')")
	Album save(@Param("album") Album album);

}
