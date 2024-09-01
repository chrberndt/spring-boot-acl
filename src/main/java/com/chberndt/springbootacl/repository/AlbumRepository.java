package com.chberndt.springbootacl.repository;

import com.chberndt.springbootacl.entity.Album;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AlbumRepository extends CrudRepository<Album, Long> {

    List<Album> findByArtist(String artist);

    List<Album> findByTitle(String title);

    Album findById(long id);
}
