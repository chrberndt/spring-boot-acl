package com.chberndt.springbootacl.repository;

import com.chberndt.springbootacl.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Christian Berndt
 */
public interface AlbumRepository extends JpaRepository<Album, Long> {

	List<Album> findByArtist(String artist);

	List<Album> findByTitle(String title);

}
