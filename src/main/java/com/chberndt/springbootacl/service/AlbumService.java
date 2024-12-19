package com.chberndt.springbootacl.service;

import com.chberndt.springbootacl.entity.Album;
import com.chberndt.springbootacl.repository.AlbumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class AlbumService {

	@Autowired
	TransactionTemplate tt;

	@Autowired
	private MutableAclService mutableAclService;

	@Autowired
	private AlbumRepository repository;

	private static final Logger log = LoggerFactory.getLogger(AlbumService.class);

	public Album createAlbum(Album album) {

		log.info("createAlbum()");

		Album newAlbum = repository.save(album);

		long albumId = newAlbum.getId();

		final ObjectIdentity objectIdentity = new ObjectIdentityImpl(Album.class, albumId);

		this.tt.execute((arg0) -> {
			this.mutableAclService.createAcl(objectIdentity);
			Sid roleAdmin = new GrantedAuthoritySid("ROLE_ADMIN");
			grantPermissions(albumId, roleAdmin, BasePermission.ADMINISTRATION);
			return null;
		});

		return newAlbum;
	}

	public void deleteAlbum(long id) {

		// TODO: remove corresponding objectIdentity and ACLs
		repository.deleteById(id);

	}

	public long getAlbumCount() {
		return repository.count();
	}

	private void grantPermissions(long albumId, Sid sid, Permission permission) {
		AclImpl acl = (AclImpl) this.mutableAclService.readAclById(new ObjectIdentityImpl(Album.class, (long) albumId));
		acl.insertAce(acl.getEntries().size(), permission, sid, true);
		updateAclInTransaction(acl);
	}

	private void updateAclInTransaction(final MutableAcl acl) {
		this.tt.execute((arg0) -> {
			this.mutableAclService.updateAcl(acl);

			return null;
		});
	}

}
