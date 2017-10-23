/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.document.library.document.conversion.internal.auth.verifier.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Istvan Andras Dezsi
 */
@ExtendedObjectClassDefinition(category = "foundation")
@Meta.OCD(
	id = "com.liferay.document.library.document.conversion.internal.auth.verifier.configuration.ImageRequestAuthVerifierConfiguration",
	localization = "content/Language",
	name = "image-request-auth-verifier-configuration-name"
)
public interface ImageRequestAuthVerifierConfiguration {

	@Meta.AD(deflt = "true", required = false)
	public boolean enabled();

	@Meta.AD(required = false)
	public String hostsAllowed();

	@Meta.AD(required = false)
	public String urlsExcludes();

	@Meta.AD(required = false)
	public String urlsIncludes();

}