package com.chberndt.springbootacl;

import com.chberndt.springbootacl.entity.Album;
import com.chberndt.springbootacl.service.AlbumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Populates the Albums in-memory database with album and ACL information.
 *
 * @author Christian Berndt
 */
@Component
public class DataSourcePopulator implements InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(DataSourcePopulator.class);

	@Autowired
	AlbumService albumService;

	@Autowired
	JdbcTemplate template;

	@Autowired
	private MutableAclService mutableAclService;

	@Override
	public void afterPropertiesSet() throws Exception {

		try {
			this.template.execute("DROP TABLE IF EXISTS acl_entry");
			this.template.execute("DROP TABLE IF EXISTS acl_object_identity");
			this.template.execute("DROP TABLE IF EXISTS acl_class");
			this.template.execute("DROP TABLE IF EXISTS acl_sid");
			this.template.execute("DELETE FROM album");

		}
		catch (Exception ex) {
			System.out.println("Failed to drop tables: " + ex.getMessage());
		}

		this.template.execute("CREATE TABLE acl_sid(" + "id BIGINT NOT NULL AUTO_INCREMENT,"
				+ "principal TINYINT NOT NULL," + "sid VARCHAR(100) NOT NULL," + "PRIMARY KEY (id),"
				+ "CONSTRAINT UNIQUE_UK_1 UNIQUE(sid,principal));");
		this.template
			.execute("CREATE TABLE acl_class(" + "id BIGINT NOT NULL AUTO_INCREMENT," + "class VARCHAR(100) NOT NULL,"
					+ "class_id_type VARCHAR(100)," + "PRIMARY KEY (id)," + "CONSTRAINT UNIQUE_UK_2 UNIQUE(class));");
		this.template.execute("CREATE TABLE acl_object_identity(" + "id BIGINT NOT NULL AUTO_INCREMENT,"
				+ "object_id_class BIGINT NOT NULL," + "object_id_identity VARCHAR(36) NOT NULL,"
				+ "parent_object BIGINT," + "owner_sid BIGINT," + "entries_inheriting TINYINT NOT NULL,"
				+ "PRIMARY KEY (id)," + "CONSTRAINT UNIQUE_UK_3 UNIQUE(object_id_class,object_id_identity),"
				+ "CONSTRAINT FOREIGN_FK_1 FOREIGN KEY(parent_object)REFERENCES acl_object_identity(id),"
				+ "CONSTRAINT FOREIGN_FK_2 FOREIGN KEY(object_id_class)REFERENCES acl_class(id),"
				+ "CONSTRAINT FOREIGN_FK_3 FOREIGN KEY(owner_sid)REFERENCES acl_sid(id));");
		this.template.execute("CREATE TABLE acl_entry(" + "id BIGINT NOT NULL AUTO_INCREMENT,"
				+ "acl_object_identity BIGINT NOT NULL,ACE_ORDER INT NOT NULL," + "sid BIGINT NOT NULL,"
				+ "mask INTEGER NOT NULL," + "granting TINYINT NOT NULL," + "audit_success TINYINT NOT NULL,"
				+ "audit_failure TINYINT NOT NULL," + "PRIMARY KEY (id),"
				+ "CONSTRAINT UNIQUE_UK_4 UNIQUE(acl_object_identity,ACE_ORDER),"
				+ "CONSTRAINT FOREIGN_FK_4 FOREIGN KEY(acl_object_identity) REFERENCES acl_object_identity(id),"
				+ "CONSTRAINT FOREIGN_FK_5 FOREIGN KEY(sid) REFERENCES acl_sid(id));");

		// Set a user account that will own Beastie Boys albums
		Authentication alicesToken = new UsernamePasswordAuthenticationToken("alice", "secret",
				AuthorityUtils.createAuthorityList("ROLE_USER"));
		SecurityContextHolder.getContext().setAuthentication(alicesToken);

		// Insert some albums that belong to alice
		albumService.createAlbum(new Album("Beastie Boys", "Licensed to Ill"));
		albumService.createAlbum(new Album("Beastie Boys", "Paul's Boutique"));
		albumService.createAlbum(new Album("Beastie Boys", "Check Your Head"));
		albumService.createAlbum(new Album("Beastie Boys", "Ill Communication"));
		albumService.createAlbum(new Album("Beastie Boys", "Hello Nasty"));

		// Set a user account that will own Taylor Swift albums
		Authentication bobsToken = new UsernamePasswordAuthenticationToken("bob", "secret",
				AuthorityUtils.createAuthorityList("ROLE_USER"));
		SecurityContextHolder.getContext().setAuthentication(bobsToken);
		albumService.createAlbum(new Album("Taylor Swift", "Taylor Swift"));
		albumService.createAlbum(new Album("Taylor Swift", "Fearless"));
		albumService.createAlbum(new Album("Taylor Swift", "Speak Now"));
		albumService.createAlbum(new Album("Taylor Swift", "Red"));
		albumService.createAlbum(new Album("Taylor Swift", "1989"));
	}

}
