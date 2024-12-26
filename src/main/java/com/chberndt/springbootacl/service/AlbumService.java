package com.chberndt.springbootacl.service;

import com.chberndt.springbootacl.entity.Album;
import com.chberndt.springbootacl.exception.AlbumNotFoundException;
import com.chberndt.springbootacl.repository.AlbumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.security.Principal;
import java.util.List;

/**
 * @author Christian Berndt
 */
@Component
public class AlbumService {

	@Autowired
	TransactionTemplate tt;

	@Autowired
	private MutableAclService mutableAclService;

	@Autowired
	private AlbumRepository repository;

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

	@PreAuthorize("hasRole('USER')")
	public void deleteAlbum(long id, Principal principal) {
		// TODO: remove corresponding objectIdentity and ACLs
		repository.findByIdAndOwner(id, principal.getName()).map(album -> {
			repository.deleteById(id);
			return album;
		}).orElseThrow(() -> new AccessDeniedException(null));
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

	@PreAuthorize("hasRole('USER')")
	public Album saveAlbum(Album newAlbum) {
		return repository.save(newAlbum);
	}

	@PreAuthorize("hasRole('USER')")
	public Album updateAlbum(long id, Principal principal, Album updatedAlbum) {
		return repository.findByIdAndOwner(id, principal.getName()).map(album -> {
			album.setArtist(updatedAlbum.getArtist());
			album.setTitle(updatedAlbum.getTitle());
			return repository.save(album);
		}).orElseThrow(() -> new AccessDeniedException(null));
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
