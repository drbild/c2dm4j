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
package org.whispercomm.c2dm4j.test;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * 
 * @author David R. Bild
 * 
 */
public class IsApproximatelyEqualLong extends TypeSafeMatcher<Long> {

	private final Long rhs;

	private final Integer tolerance;

	public IsApproximatelyEqualLong(Long rhs, Integer tolerance) {
		this.rhs = rhs;
		this.tolerance = tolerance;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(rhs);
	}

	@Override
	protected boolean matchesSafely(Long lhs) {
		return Math.max(rhs, lhs) - Math.min(rhs, lhs) <= tolerance;
	}

	@Factory
	public static <T> Matcher<Long> approx(Long rhs, Integer tolerance) {
		return new IsApproximatelyEqualLong(rhs, tolerance);
	}

}
