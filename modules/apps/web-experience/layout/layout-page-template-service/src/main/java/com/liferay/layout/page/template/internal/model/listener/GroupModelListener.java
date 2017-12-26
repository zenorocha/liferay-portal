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

package com.liferay.layout.page.template.internal.model.listener;

import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(immediate = true, service = ModelListener.class)
public class GroupModelListener extends BaseModelListener<Group> {

	@Override
	public void onBeforeRemove(Group group) throws ModelListenerException {
		try {
			List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
				_layoutPageTemplateCollectionLocalService.
					getLayoutPageTemplateCollections(
						group.getGroupId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS);

			for (LayoutPageTemplateCollection layoutPageTemplateCollection :
					layoutPageTemplateCollections) {

				_layoutPageTemplateCollectionLocalService.
					deleteLayoutPageTemplateCollection(
						layoutPageTemplateCollection.
							getLayoutPageTemplateCollectionId());
			}
		}
		catch (Exception e) {
			throw new ModelListenerException(e);
		}
	}

	@Reference(unbind = "-")
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

}