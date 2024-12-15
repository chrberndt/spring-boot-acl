package com.chberndt.springbootacl;

import com.chberndt.springbootacl.entity.Album;
import com.chberndt.springbootacl.repository.AlbumRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Populates the Albums in-memory database with album and ACL information.
 *
 * @author Christian Berndt
 */
@Component
public class DataSourcePopulator implements InitializingBean {

	@Autowired
	AlbumRepository albumRepository;

	@Autowired
	JdbcTemplate template;

	@Autowired
	TransactionTemplate tt;

	@Autowired
	private MutableAclService mutableAclService;

	@Override
	public void afterPropertiesSet() throws Exception {

//		Assert.notNull(this.mutableAclService, "mutableAclService required");
//		Assert.notNull(this.template, "dataSource required");
//		Assert.notNull(this.tt, "platformTransactionManager required");

		// Set a user account that will initially own all the created data
		Authentication authRequest = new UsernamePasswordAuthenticationToken("rod", "koala",
				AuthorityUtils.createAuthorityList("ROLE_IGNORED"));
		SecurityContextHolder.getContext().setAuthentication(authRequest);

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

		this.template.execute("CREATE TABLE acl_sid("
				+ "id BIGINT NOT NULL AUTO_INCREMENT,"
				+ "principal TINYINT NOT NULL,"
				+ "sid VARCHAR(100) NOT NULL,"
				+ "PRIMARY KEY (id),"
				+ "CONSTRAINT UNIQUE_UK_1 UNIQUE(sid,principal));");
		this.template.execute("CREATE TABLE acl_class("
				+ "id BIGINT NOT NULL AUTO_INCREMENT,"
				+ "class VARCHAR(100) NOT NULL,"
				+ "class_id_type VARCHAR(100),"
				+ "PRIMARY KEY (id),"
				+ "CONSTRAINT UNIQUE_UK_2 UNIQUE(class));");
		this.template.execute("CREATE TABLE acl_object_identity("
				+ "id BIGINT NOT NULL AUTO_INCREMENT,"
				+ "object_id_class BIGINT NOT NULL,"
				+ "object_id_identity VARCHAR(36) NOT NULL,"
				+ "parent_object BIGINT,"
				+ "owner_sid BIGINT,"
				+ "entries_inheriting TINYINT NOT NULL,"
				+ "PRIMARY KEY (id),"
				+ "CONSTRAINT UNIQUE_UK_3 UNIQUE(object_id_class,object_id_identity),"
				+ "CONSTRAINT FOREIGN_FK_1 FOREIGN KEY(parent_object)REFERENCES acl_object_identity(id),"
				+ "CONSTRAINT FOREIGN_FK_2 FOREIGN KEY(object_id_class)REFERENCES acl_class(id),"
				+ "CONSTRAINT FOREIGN_FK_3 FOREIGN KEY(owner_sid)REFERENCES acl_sid(id));");
		this.template.execute("CREATE TABLE acl_entry("
				+ "id BIGINT NOT NULL AUTO_INCREMENT,"
				+ "acl_object_identity BIGINT NOT NULL,ACE_ORDER INT NOT NULL,"
				+ "sid BIGINT NOT NULL,"
				+ "mask INTEGER NOT NULL,"
				+ "granting TINYINT NOT NULL,"
				+ "audit_success TINYINT NOT NULL,"
				+ "audit_failure TINYINT NOT NULL,"
				+ "PRIMARY KEY (id),"
				+ "CONSTRAINT UNIQUE_UK_4 UNIQUE(acl_object_identity,ACE_ORDER),"
				+ "CONSTRAINT FOREIGN_FK_4 FOREIGN KEY(acl_object_identity) REFERENCES acl_object_identity(id),"
				+ "CONSTRAINT FOREIGN_FK_5 FOREIGN KEY(sid) REFERENCES acl_sid(id));");

		// Populate the table with some albums
		albumRepository.save(new Album("Beastie Boys", "Licensed to Ill"));
		albumRepository.save(new Album("Beastie Boys", "Paul's Boutique"));
		albumRepository.save(new Album("Beastie Boys", "Check Your Head"));
		albumRepository.save(new Album("Beastie Boys", "Ill Communication"));
		albumRepository.save(new Album("Beastie Boys", "Hello Nasty"));
		albumRepository.save(new Album("Taylor Swift", "Taylor Swift"));
		albumRepository.save(new Album("Taylor Swift", "Fearless"));
		albumRepository.save(new Album("Taylor Swift", "Speak Now"));
		albumRepository.save(new Album("Taylor Swift", "Red"));
		albumRepository.save(new Album("Taylor Swift", "1989"));

		long albumCount = albumRepository.count();

		System.out.println("albumCount: " + albumCount);

		// Create acl_object_identity rows (and also acl_class rows as needed)
		for (int i = 1; i <= albumCount; i++) {
			final ObjectIdentity objectIdentity = new ObjectIdentityImpl(Album.class, (long) i);
			this.tt.execute((arg0) -> {
				this.mutableAclService.createAcl(objectIdentity);

				return null;
			});
		}

		// Grant permissions on albums
		grantPermissions(1, "alice", BasePermission.READ);
		grantPermissions(2, "alice", BasePermission.READ);
		grantPermissions(3, "alice", BasePermission.READ);
		grantPermissions(4, "alice", BasePermission.READ);
		grantPermissions(5, "alice", BasePermission.READ);
		grantPermissions(6, "bob", BasePermission.READ);
		grantPermissions(7, "bob", BasePermission.READ);
		grantPermissions(8, "bob", BasePermission.READ);
		grantPermissions(9, "bob", BasePermission.READ);
		grantPermissions(10, "bob", BasePermission.READ);
	}

	private void grantPermissions(int albumId, String recipientUsername, Permission permission) {
		AclImpl acl = (AclImpl) this.mutableAclService
				.readAclById(new ObjectIdentityImpl(Album.class, (long) albumId));
		acl.insertAce(acl.getEntries().size(), permission, new PrincipalSid(recipientUsername), true);
		updateAclInTransaction(acl);
	}

	private void updateAclInTransaction(final MutableAcl acl) {
		this.tt.execute((arg0) -> {
			this.mutableAclService.updateAcl(acl);

			return null;
		});
	}
}
