package com.chberndt.springbootacl.repository;

import com.chberndt.springbootacl.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Christian Berndt
 */
public interface AlbumRepository extends JpaRepository<Album, Long> {

	Optional<Album> findByIdAndOwner(long id, String owner);

}
