package com.chberndt.springbootacl.repository;

import com.chberndt.springbootacl.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {

	List<Album> findByArtist(String artist);

	List<Album> findByTitle(String title);

	Album findById(long id);

	Album save(@Param("album") Album album);

}
