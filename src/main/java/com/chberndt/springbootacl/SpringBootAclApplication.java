package com.chberndt.springbootacl;

import com.chberndt.springbootacl.repository.AlbumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SpringBootAclApplication {

	private static final Logger log = LoggerFactory.getLogger(SpringBootAclApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootAclApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(AlbumRepository repository) {
		return (args) -> {

			// fetch all albums
			// log.info("Albums found with findAll():");
			// log.info("-------------------------------");
			// repository.findAll().forEach(album -> {
			// log.info(album.toString());
			// });
			// log.info("");
			//
			// // fetch an individual album by ID
			// Album album = repository.findById(1L);
			// if (album != null) {
			// log.info("Album found with findById(1L):");
			// log.info("--------------------------------");
			// log.info(album.toString());
			// log.info("");
			// } else {
			// log.info("No album found with findById(1L):");
			// log.info("--------------------------------");
			// }
			//
			// // fetch albums by artist
			// log.info("Album found with findByArtist('Beastie Boys'):");
			// log.info("--------------------------------------------");
			// repository.findByArtist("Beastie Boys").forEach(beastieBoysAlbum -> {
			// log.info(beastieBoysAlbum.toString());
			// });
			// log.info("");
		};
	}

}
