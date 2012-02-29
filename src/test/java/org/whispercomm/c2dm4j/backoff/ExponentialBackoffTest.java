/*
 * Copyright 2012 The University of Michigan
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
package org.whispercomm.c2dm4j.backoff;

import org.junit.Before;
import org.junit.Test;
import org.whispercomm.c2dm4j.backoff.ExponentialBackoff;

import static org.whispercomm.c2dm4j.test.Matchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link ExponentialBackoff}.
 * 
 * @author David R. Bild
 * 
 */
public class ExponentialBackoffTest {

	ExponentialBackoff cut;

	@Before
	public void setup() {
		cut = new ExponentialBackoff(10, 5);
	}

	@Test
	public void initialDelayIsZero() {
		Attempt attempt = cut.begin();
		assertThat(attempt.delay(), is(0L));
	}

	@Test
	public void afterOneFailDelayIs10ms() {
		cut.begin().recordFailure();
		assertThat(cut.begin().delay(), is(approx(10L, 10)));
	}

	@Test
	public void afterTwoFailsDelayIs30ms() {
		cut.begin().recordFailure();
		cut.begin().recordFailure();
		assertThat(cut.begin().delay(), is(approx(30L, 10)));
	}

	@Test
	public void afterFiveFailsDelayIs310ms() {
		for (int i = 0; i < 5; ++i) {
			cut.begin().recordFailure();
		}
		assertThat(cut.begin().delay(), is(approx(310L, 10)));
	}

	@Test
	public void afterMaxFailsDelayIsStill310ms() {
		for (int i = 0; i < 6; ++i) {
			cut.begin().recordFailure();
		}
		assertThat(cut.begin().delay(), is(approx(310L, 10)));
	}

	@Test
	public void succeedResetsDelayTo0ms() {
		for (int i = 0; i < 4; ++i) {
			cut.begin().recordFailure();
		}
		assertThat(cut.begin().delay(), is(approx(150L, 10)));

		cut.begin().recordSuccess();
		assertThat(cut.begin().delay(), is(0L));
	}

	@Test
	public void failureOnOldAttemptDoesNotIncreaseDelay() {
		for (int i = 0; i < 3; ++i) {
			cut.begin().recordFailure();
		}

		Attempt attempt = cut.begin();
		cut.begin().recordFailure();
		assertThat(cut.begin().delay(), is(approx(150L, 10)));
		attempt.recordFailure();
		assertThat(cut.begin().delay(), is(approx(150L, 10)));
	}

	@Test
	public void successOnOldAttemptDoesNotResetDelay() {
		cut.begin().recordFailure();
		cut.begin().recordFailure();

		Attempt attempt = cut.begin();
		cut.begin().recordFailure();
		assertThat(cut.begin().delay(), is(approx(70L, 10)));
		attempt.recordSuccess();
		assertThat(cut.begin().delay(), is(approx(70L, 10)));
	}
}
