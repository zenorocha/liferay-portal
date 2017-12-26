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

package com.liferay.fragment.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link FragmentEntryInstanceLinkLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryInstanceLinkLocalService
 * @generated
 */
@ProviderType
public class FragmentEntryInstanceLinkLocalServiceWrapper
	implements FragmentEntryInstanceLinkLocalService,
		ServiceWrapper<FragmentEntryInstanceLinkLocalService> {
	public FragmentEntryInstanceLinkLocalServiceWrapper(
		FragmentEntryInstanceLinkLocalService fragmentEntryInstanceLinkLocalService) {
		_fragmentEntryInstanceLinkLocalService = fragmentEntryInstanceLinkLocalService;
	}

	/**
	* Adds the fragment entry instance link to the database. Also notifies the appropriate model listeners.
	*
	* @param fragmentEntryInstanceLink the fragment entry instance link
	* @return the fragment entry instance link that was added
	*/
	@Override
	public com.liferay.fragment.model.FragmentEntryInstanceLink addFragmentEntryInstanceLink(
		com.liferay.fragment.model.FragmentEntryInstanceLink fragmentEntryInstanceLink) {
		return _fragmentEntryInstanceLinkLocalService.addFragmentEntryInstanceLink(fragmentEntryInstanceLink);
	}

	@Override
	public com.liferay.fragment.model.FragmentEntryInstanceLink addFragmentEntryInstanceLink(
		long groupId, long layoutPageTemplateEntryId, long fragmentEntryId,
		int position) {
		return _fragmentEntryInstanceLinkLocalService.addFragmentEntryInstanceLink(groupId,
			layoutPageTemplateEntryId, fragmentEntryId, position);
	}

	/**
	* Creates a new fragment entry instance link with the primary key. Does not add the fragment entry instance link to the database.
	*
	* @param fragmentEntryInstanceLinkId the primary key for the new fragment entry instance link
	* @return the new fragment entry instance link
	*/
	@Override
	public com.liferay.fragment.model.FragmentEntryInstanceLink createFragmentEntryInstanceLink(
		long fragmentEntryInstanceLinkId) {
		return _fragmentEntryInstanceLinkLocalService.createFragmentEntryInstanceLink(fragmentEntryInstanceLinkId);
	}

	/**
	* Deletes the fragment entry instance link from the database. Also notifies the appropriate model listeners.
	*
	* @param fragmentEntryInstanceLink the fragment entry instance link
	* @return the fragment entry instance link that was removed
	*/
	@Override
	public com.liferay.fragment.model.FragmentEntryInstanceLink deleteFragmentEntryInstanceLink(
		com.liferay.fragment.model.FragmentEntryInstanceLink fragmentEntryInstanceLink) {
		return _fragmentEntryInstanceLinkLocalService.deleteFragmentEntryInstanceLink(fragmentEntryInstanceLink);
	}

	/**
	* Deletes the fragment entry instance link with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param fragmentEntryInstanceLinkId the primary key of the fragment entry instance link
	* @return the fragment entry instance link that was removed
	* @throws PortalException if a fragment entry instance link with the primary key could not be found
	*/
	@Override
	public com.liferay.fragment.model.FragmentEntryInstanceLink deleteFragmentEntryInstanceLink(
		long fragmentEntryInstanceLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _fragmentEntryInstanceLinkLocalService.deleteFragmentEntryInstanceLink(fragmentEntryInstanceLinkId);
	}

	@Override
	public java.util.List<com.liferay.fragment.model.FragmentEntryInstanceLink> deleteLayoutPageTemplateEntryFragmentEntryInstanceLinks(
		long groupId, long layoutPageTemplateEntryId) {
		return _fragmentEntryInstanceLinkLocalService.deleteLayoutPageTemplateEntryFragmentEntryInstanceLinks(groupId,
			layoutPageTemplateEntryId);
	}

	/**
	* @throws PortalException
	*/
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _fragmentEntryInstanceLinkLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _fragmentEntryInstanceLinkLocalService.dynamicQuery();
	}

	/**
	* Performs a dynamic query on the database and returns the matching rows.
	*
	* @param dynamicQuery the dynamic query
	* @return the matching rows
	*/
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return _fragmentEntryInstanceLinkLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.fragment.model.impl.FragmentEntryInstanceLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @return the range of matching rows
	*/
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {
		return _fragmentEntryInstanceLinkLocalService.dynamicQuery(dynamicQuery,
			start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.fragment.model.impl.FragmentEntryInstanceLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching rows
	*/
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {
		return _fragmentEntryInstanceLinkLocalService.dynamicQuery(dynamicQuery,
			start, end, orderByComparator);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @return the number of rows matching the dynamic query
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return _fragmentEntryInstanceLinkLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @param projection the projection to apply to the query
	* @return the number of rows matching the dynamic query
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {
		return _fragmentEntryInstanceLinkLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.fragment.model.FragmentEntryInstanceLink fetchFragmentEntryInstanceLink(
		long fragmentEntryInstanceLinkId) {
		return _fragmentEntryInstanceLinkLocalService.fetchFragmentEntryInstanceLink(fragmentEntryInstanceLinkId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return _fragmentEntryInstanceLinkLocalService.getActionableDynamicQuery();
	}

	/**
	* Returns the fragment entry instance link with the primary key.
	*
	* @param fragmentEntryInstanceLinkId the primary key of the fragment entry instance link
	* @return the fragment entry instance link
	* @throws PortalException if a fragment entry instance link with the primary key could not be found
	*/
	@Override
	public com.liferay.fragment.model.FragmentEntryInstanceLink getFragmentEntryInstanceLink(
		long fragmentEntryInstanceLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _fragmentEntryInstanceLinkLocalService.getFragmentEntryInstanceLink(fragmentEntryInstanceLinkId);
	}

	/**
	* Returns a range of all the fragment entry instance links.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.fragment.model.impl.FragmentEntryInstanceLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of fragment entry instance links
	* @param end the upper bound of the range of fragment entry instance links (not inclusive)
	* @return the range of fragment entry instance links
	*/
	@Override
	public java.util.List<com.liferay.fragment.model.FragmentEntryInstanceLink> getFragmentEntryInstanceLinks(
		int start, int end) {
		return _fragmentEntryInstanceLinkLocalService.getFragmentEntryInstanceLinks(start,
			end);
	}

	@Override
	public java.util.List<com.liferay.fragment.model.FragmentEntryInstanceLink> getFragmentEntryInstanceLinks(
		long groupId, long layoutPageTemplateEntryId) {
		return _fragmentEntryInstanceLinkLocalService.getFragmentEntryInstanceLinks(groupId,
			layoutPageTemplateEntryId);
	}

	/**
	* Returns the number of fragment entry instance links.
	*
	* @return the number of fragment entry instance links
	*/
	@Override
	public int getFragmentEntryInstanceLinksCount() {
		return _fragmentEntryInstanceLinkLocalService.getFragmentEntryInstanceLinksCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return _fragmentEntryInstanceLinkLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	@Override
	public java.lang.String getOSGiServiceIdentifier() {
		return _fragmentEntryInstanceLinkLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _fragmentEntryInstanceLinkLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Updates the fragment entry instance link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param fragmentEntryInstanceLink the fragment entry instance link
	* @return the fragment entry instance link that was updated
	*/
	@Override
	public com.liferay.fragment.model.FragmentEntryInstanceLink updateFragmentEntryInstanceLink(
		com.liferay.fragment.model.FragmentEntryInstanceLink fragmentEntryInstanceLink) {
		return _fragmentEntryInstanceLinkLocalService.updateFragmentEntryInstanceLink(fragmentEntryInstanceLink);
	}

	@Override
	public FragmentEntryInstanceLinkLocalService getWrappedService() {
		return _fragmentEntryInstanceLinkLocalService;
	}

	@Override
	public void setWrappedService(
		FragmentEntryInstanceLinkLocalService fragmentEntryInstanceLinkLocalService) {
		_fragmentEntryInstanceLinkLocalService = fragmentEntryInstanceLinkLocalService;
	}

	private FragmentEntryInstanceLinkLocalService _fragmentEntryInstanceLinkLocalService;
}