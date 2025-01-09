package com.chberndt.springbootacl.service;

import com.chberndt.springbootacl.entity.Album;
import com.chberndt.springbootacl.exception.AlbumNotFoundException;
import com.chberndt.springbootacl.repository.AlbumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.security.Principal;
import java.util.List;

/**
 * @author Christian Berndt
 */
@Component
public class AlbumService {

	TransactionTemplate tt;

	private final MutableAclService mutableAclService;

	private final AlbumRepository repository;

	public AlbumService(TransactionTemplate tt, MutableAclService mutableAclService, AlbumRepository albumRepository) {
		this.tt = tt;
		this.mutableAclService = mutableAclService;
		this.repository = albumRepository;
	}

	private static final Logger log = LoggerFactory.getLogger(AlbumService.class);

	@PreAuthorize("hasRole('USER')")
	public Album createAlbum(Principal principal, Album album) {

		album.setOwner(principal.getName());

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

	@Transactional // rolls back the transaction if post-authorization fails
	@PostAuthorize("returnObject.owner == authentication.name || hasRole('ADMIN')")
	public Album deleteAlbum(long id) {

		Album album = repository.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));
		ObjectIdentity objectIdentity = new ObjectIdentityImpl(Album.class, album.getId());
		mutableAclService.deleteAcl(objectIdentity, true);
		repository.delete(album);

		return album;

	}

	public Album getAlbum(long id) {
		return repository.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));
	}

	public List<Album> getAll() {
		return repository.findAll();
	}

	public long getAlbumCount() {
		return repository.count();
	}

	@Transactional // rolls back the transaction if post-authorization fails
	@PostAuthorize("returnObject.owner == authentication.name || hasRole('ADMIN')")
	public Album updateAlbum(long id, Album updatedAlbum) {

		Album album = repository.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));

		updatedAlbum.setOwner(album.getOwner());
		updatedAlbum.setId(album.getId());

		return repository.save(updatedAlbum);
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
