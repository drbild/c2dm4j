/*
 * Copyright 2012 The Regents of the University of Michigan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.whispercomm.c2dm4j.auth;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An authentication token provider that persists the token to a file. The
 * authentication token must be encoded in UTF-8 and be the only contents of hte
 * file.
 * <p>
 * SLF4J is used for logging.
 * 
 * @author David R. Bild
 * 
 */
public class FileAuthTokenProvider extends AbstractAuthTokenProvider {
	protected static final String ENCODING = "UTF-8";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileAuthTokenProvider.class);

	private final File file;

	/**
	 * Creates a new authentication token provider backed by the specified file.
	 * 
	 * @param file
	 *            the file containing the authentication token.
	 */
	public FileAuthTokenProvider(File file) {
		this.file = file;
	}

	/**
	 * Creates a new authentication token provider backed by the file with the
	 * specified name.
	 * 
	 * @param file
	 *            name of the file containing the authentication token.
	 */
	public FileAuthTokenProvider(String file) {
		this(new File(file));
	}

	@Override
	protected AuthToken readToken() throws IOException {
		try {
			return new AuthToken(FileUtils.readFileToString(file, ENCODING)
					.trim());
		} catch (IOException e) {
			String msg = String.format(
					"Failed to read C2DM authentication token from file %s",
					this.file);
			LOGGER.warn(msg);
			throw new IOException(msg, e);
		}
	}

	@Override
	protected void writeToken(AuthToken token) throws IOException {
		try {
			FileUtils.writeStringToFile(file, token.toString(), ENCODING);
			LOGGER.info("Persisted to C2DM authentication token to file {}",
					file);
		} catch (IOException e) {
			String msg = String.format(
					"Failed to persist C2DM authentication token to file %s",
					token, this.file);
			LOGGER.warn(msg);
			throw new IOException(msg, e);
		}
	}
}
