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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.whispercomm.c2dm4j.auth.AuthToken;
import org.whispercomm.c2dm4j.auth.FileAuthTokenProvider;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link FileAuthTokenProvider}.
 * 
 * @author David R. Bild
 * 
 */
public class FileAuthTokenProviderTest {
	private static final AuthToken TOKEN_ONE = new AuthToken(
			"My First Token with a \n newline.");
	private static final AuthToken TOKEN_TWO = new AuthToken(
			"My Second Token with a \n newline.");

	private File file;

	private FileAuthTokenProvider provider;

	@Before
	public void setup() throws IOException {
		file = File.createTempFile("c2dm", ".tok");
		provider = new FileAuthTokenProvider(file);
	}

	@After
	public void cleanup() {
		file.delete();
	}

	@Test
	public void writeTokenWritesToFile() throws IOException {
		provider.writeToken(TOKEN_ONE);
		assertThat(
				FileUtils.readFileToString(file, FileAuthTokenProvider.ENCODING),
				is(TOKEN_ONE.toString()));
	}

	@Test
	public void readTokenReadsFromFile() throws IOException {
		FileUtils.writeStringToFile(file, TOKEN_TWO.toString(),
				FileAuthTokenProvider.ENCODING);
		assertThat(provider.readToken(), is(TOKEN_TWO));
	}

}
