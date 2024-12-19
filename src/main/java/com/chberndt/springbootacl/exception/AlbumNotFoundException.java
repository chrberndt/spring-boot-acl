package com.chberndt.springbootacl.exception;

/**
 * @author Christian Berndt
 */
public class AlbumNotFoundException extends RuntimeException {

	public AlbumNotFoundException(Long id) {
		super("Could not find album " + id);
	}

}
