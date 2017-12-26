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

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryInstanceLink;
import com.liferay.fragment.service.FragmentCollectionServiceUtil;
import com.liferay.fragment.service.FragmentEntryInstanceLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryServiceUtil;
import com.liferay.layout.admin.web.internal.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.util.LayoutPageTemplatePortletUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.layout.page.template.service.permission.LayoutPageTemplatePermission;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class LayoutPageTemplateDisplayContext {

	public LayoutPageTemplateDisplayContext(
			RenderRequest renderRequest, RenderResponse renderResponse,
			HttpServletRequest request)
		throws PortalException {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_request = request;
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(_request);

		_displayStyle = portalPreferences.getValue(
			LayoutAdminPortletKeys.GROUP_PAGES, "display-style", "icon");

		return _displayStyle;
	}

	public String getEditLayoutPageTemplateEntryRedirect()
		throws PortalException {

		PortletURL portletURL = _renderResponse.createRenderURL();

		portletURL.setParameter(
			"mvcPath", "/view_layout_page_template_entries.jsp");
		portletURL.setParameter("tabs1", "page-templates");

		if (getLayoutPageTemplateCollectionId() > 0) {
			portletURL.setParameter(
				"layoutPageTemplateCollectionId",
				String.valueOf(getLayoutPageTemplateCollectionId()));
		}

		return portletURL.toString();
	}

	public JSONArray getFragmentCollectionsJSONArray() throws PortalException {
		JSONArray fragmentCollectionsJSONArray =
			JSONFactoryUtil.createJSONArray();

		ThemeDisplay themeDisplay = (ThemeDisplay)_request.getAttribute(
			WebKeys.THEME_DISPLAY);

		List<FragmentCollection> fragmentCollections =
			FragmentCollectionServiceUtil.getFragmentCollections(
				themeDisplay.getScopeGroupId());

		for (FragmentCollection fragmentCollection : fragmentCollections) {
			List<FragmentEntry> fragmentEntries =
				FragmentEntryServiceUtil.fetchFragmentEntries(
					fragmentCollection.getFragmentCollectionId());

			if (ListUtil.isEmpty(fragmentEntries)) {
				continue;
			}

			JSONObject fragmentCollectionJSONObject =
				JSONFactoryUtil.createJSONObject();

			fragmentCollectionJSONObject.put(
				"fragmentCollectionId",
				fragmentCollection.getFragmentCollectionId());

			JSONArray fragmentEntriesJSONArray =
				JSONFactoryUtil.createJSONArray();

			for (FragmentEntry fragmentEntry : fragmentEntries) {
				JSONObject fragmentEntryJSONObject =
					JSONFactoryUtil.createJSONObject();

				fragmentEntryJSONObject.put(
					"fragmentEntryId", fragmentEntry.getFragmentEntryId());
				fragmentEntryJSONObject.put(
					"imagePreviewURL",
					fragmentEntry.getImagePreviewURL(themeDisplay));
				fragmentEntryJSONObject.put("name", fragmentEntry.getName());

				fragmentEntriesJSONArray.put(fragmentEntryJSONObject);
			}

			fragmentCollectionJSONObject.put(
				"fragmentEntries", fragmentEntriesJSONArray);

			fragmentCollectionJSONObject.put(
				"name", fragmentCollection.getName());

			fragmentCollectionsJSONArray.put(fragmentCollectionJSONObject);
		}

		return fragmentCollectionsJSONArray;
	}

	public JSONArray getFragmentEntryInstanceLinksJSONArray()
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)_request.getAttribute(
			WebKeys.THEME_DISPLAY);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			getLayoutPageTemplateEntry();

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<FragmentEntryInstanceLink> fragmentEntryInstanceLinks =
			FragmentEntryInstanceLinkLocalServiceUtil.
				getFragmentEntryInstanceLinks(
					themeDisplay.getScopeGroupId(),
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		for (FragmentEntryInstanceLink fragmentEntryInstanceLink :
				fragmentEntryInstanceLinks) {

			FragmentEntry fragmentEntry =
				FragmentEntryServiceUtil.fetchFragmentEntry(
					fragmentEntryInstanceLink.getFragmentEntryId());

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			jsonObject.put(
				"fragmentEntryId", fragmentEntry.getFragmentEntryId());
			jsonObject.put(
				"imagePreviewURL",
				fragmentEntry.getImagePreviewURL(themeDisplay));
			jsonObject.put("name", fragmentEntry.getName());

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_request, "keywords");

		return _keywords;
	}

	public LayoutPageTemplateCollection getLayoutPageTemplateCollection()
		throws PortalException {

		if (_layoutPageTemplateCollection != null) {
			return _layoutPageTemplateCollection;
		}

		_layoutPageTemplateCollection =
			LayoutPageTemplateCollectionServiceUtil.
				fetchLayoutPageTemplateCollection(
					getLayoutPageTemplateCollectionId());

		return _layoutPageTemplateCollection;
	}

	public long getLayoutPageTemplateCollectionId() {
		if (Validator.isNotNull(_layoutPageTemplateCollectionId)) {
			return _layoutPageTemplateCollectionId;
		}

		_layoutPageTemplateCollectionId = ParamUtil.getLong(
			_request, "layoutPageTemplateCollectionId");

		return _layoutPageTemplateCollectionId;
	}

	public String getLayoutPageTemplateCollectionRedirect()
		throws PortalException {

		String redirect = ParamUtil.getString(_request, "redirect");

		if (Validator.isNull(redirect)) {
			PortletURL backURL = _renderResponse.createRenderURL();

			backURL.setParameter(
				"mvcPath", "/view_layout_page_template_collections.jsp");
			backURL.setParameter("tabs1", "page-templates");

			redirect = backURL.toString();
		}

		return redirect;
	}

	public SearchContainer getLayoutPageTemplateCollectionsSearchContainer()
		throws PortalException {

		if (_layoutPageTemplateCollectionsSearchContainer != null) {
			return _layoutPageTemplateCollectionsSearchContainer;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_request.getAttribute(
			WebKeys.THEME_DISPLAY);

		SearchContainer layoutPageTemplateCollectionsSearchContainer =
			new SearchContainer(
				_renderRequest, _renderResponse.createRenderURL(), null,
				"there-are-no-collections");

		if (!isSearch()) {
			layoutPageTemplateCollectionsSearchContainer.setEmptyResultsMessage(
				"there-are-no-collections.-you-can-add-a-collection-by-" +
					"clicking-the-plus-button-on-the-bottom-right-corner");

			layoutPageTemplateCollectionsSearchContainer.
				setEmptyResultsMessageCssClass(
					"taglib-empty-result-message-header-has-plus-btn");
		}
		else {
			layoutPageTemplateCollectionsSearchContainer.setSearch(true);
		}

		layoutPageTemplateCollectionsSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		layoutPageTemplateCollectionsSearchContainer.setOrderByCol(
			getOrderByCol());

		OrderByComparator<LayoutPageTemplateCollection> orderByComparator =
			LayoutPageTemplatePortletUtil.
				getLayoutPageTemplateCollectionOrderByComparator(
					getOrderByCol(), getOrderByType());

		layoutPageTemplateCollectionsSearchContainer.setOrderByComparator(
			orderByComparator);

		layoutPageTemplateCollectionsSearchContainer.setOrderByType(
			getOrderByType());
		layoutPageTemplateCollectionsSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		List<LayoutPageTemplateCollection> layoutPageTemplateCollections = null;
		int layoutPageTemplateCollectionsCount = 0;

		if (isSearch()) {
			layoutPageTemplateCollections =
				LayoutPageTemplateCollectionServiceUtil.
					getLayoutPageTemplateCollections(
						themeDisplay.getScopeGroupId(), getKeywords(),
						layoutPageTemplateCollectionsSearchContainer.getStart(),
						layoutPageTemplateCollectionsSearchContainer.getEnd(),
						orderByComparator);

			layoutPageTemplateCollectionsCount =
				LayoutPageTemplateCollectionServiceUtil.
					getLayoutPageTemplateCollectionsCount(
						themeDisplay.getScopeGroupId(), getKeywords());
		}
		else {
			layoutPageTemplateCollections =
				LayoutPageTemplateCollectionServiceUtil.
					getLayoutPageTemplateCollections(
						themeDisplay.getScopeGroupId(),
						layoutPageTemplateCollectionsSearchContainer.getStart(),
						layoutPageTemplateCollectionsSearchContainer.getEnd(),
						orderByComparator);

			layoutPageTemplateCollectionsCount =
				LayoutPageTemplateCollectionServiceUtil.
					getLayoutPageTemplateCollectionsCount(
						themeDisplay.getScopeGroupId());
		}

		layoutPageTemplateCollectionsSearchContainer.setTotal(
			layoutPageTemplateCollectionsCount);

		layoutPageTemplateCollectionsSearchContainer.setResults(
			layoutPageTemplateCollections);

		_layoutPageTemplateCollectionsSearchContainer =
			layoutPageTemplateCollectionsSearchContainer;

		return _layoutPageTemplateCollectionsSearchContainer;
	}

	public String getLayoutPageTemplateCollectionTitle()
		throws PortalException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			getLayoutPageTemplateCollection();

		if (layoutPageTemplateCollection == null) {
			return LanguageUtil.get(_request, "add-collection");
		}

		return layoutPageTemplateCollection.getName();
	}

	public SearchContainer getLayoutPageTemplateEntriesSearchContainer()
		throws PortalException {

		if (_layoutPageTemplateEntriesSearchContainer != null) {
			return _layoutPageTemplateEntriesSearchContainer;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_request.getAttribute(
			WebKeys.THEME_DISPLAY);

		SearchContainer layoutPageTemplateEntriesSearchContainer =
			new SearchContainer(
				_renderRequest, _renderResponse.createRenderURL(), null,
				"there-are-no-page-templates");

		if (!isSearch()) {
			layoutPageTemplateEntriesSearchContainer.setEmptyResultsMessage(
				"there-are-no-page-templates.-you-can-add-a-page-template-by-" +
					"clicking-the-plus-button-on-the-bottom-right-corner");
			layoutPageTemplateEntriesSearchContainer.
				setEmptyResultsMessageCssClass(
					"taglib-empty-result-message-header-has-plus-btn");
		}
		else {
			layoutPageTemplateEntriesSearchContainer.setSearch(true);
		}

		layoutPageTemplateEntriesSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		layoutPageTemplateEntriesSearchContainer.setOrderByCol(getOrderByCol());

		OrderByComparator<LayoutPageTemplateEntry> orderByComparator =
			LayoutPageTemplatePortletUtil.
				getLayoutPageTemplateEntryOrderByComparator(
					getOrderByCol(), getOrderByType());

		layoutPageTemplateEntriesSearchContainer.setOrderByComparator(
			orderByComparator);

		layoutPageTemplateEntriesSearchContainer.setOrderByType(
			getOrderByType());

		List<LayoutPageTemplateEntry> layoutPageTemplateEntries = null;
		int layoutPageTemplateEntriesCount = 0;

		if (isSearch()) {
			layoutPageTemplateEntries =
				LayoutPageTemplateEntryLocalServiceUtil.
					getLayoutPageTemplateEntries(
						themeDisplay.getScopeGroupId(),
						getLayoutPageTemplateCollectionId(), getKeywords(),
						layoutPageTemplateEntriesSearchContainer.getStart(),
						layoutPageTemplateEntriesSearchContainer.getEnd(),
						orderByComparator);

			layoutPageTemplateEntriesCount =
				LayoutPageTemplateEntryServiceUtil.
					getLayoutPageTemplateEntriesCount(
						themeDisplay.getScopeGroupId(),
						getLayoutPageTemplateCollectionId(), getKeywords());
		}
		else {
			layoutPageTemplateEntries =
				LayoutPageTemplateEntryLocalServiceUtil.
					getLayoutPageTemplateEntries(
						themeDisplay.getScopeGroupId(),
						getLayoutPageTemplateCollectionId(),
						layoutPageTemplateEntriesSearchContainer.getStart(),
						layoutPageTemplateEntriesSearchContainer.getEnd(),
						orderByComparator);

			layoutPageTemplateEntriesCount =
				LayoutPageTemplateEntryServiceUtil.
					getLayoutPageTemplateEntriesCount(
						themeDisplay.getScopeGroupId(),
						getLayoutPageTemplateCollectionId());
		}

		layoutPageTemplateEntriesSearchContainer.setResults(
			layoutPageTemplateEntries);
		layoutPageTemplateEntriesSearchContainer.setTotal(
			layoutPageTemplateEntriesCount);

		_layoutPageTemplateEntriesSearchContainer =
			layoutPageTemplateEntriesSearchContainer;

		return _layoutPageTemplateEntriesSearchContainer;
	}

	public LayoutPageTemplateEntry getLayoutPageTemplateEntry()
		throws PortalException {

		if (_layoutPageTemplateEntry != null) {
			return _layoutPageTemplateEntry;
		}

		_layoutPageTemplateEntry =
			LayoutPageTemplateEntryServiceUtil.fetchLayoutPageTemplateEntry(
				getLayoutPageTemplateEntryId());

		return _layoutPageTemplateEntry;
	}

	public long getLayoutPageTemplateEntryId() {
		if (Validator.isNotNull(_layoutPageTemplateEntryId)) {
			return _layoutPageTemplateEntryId;
		}

		_layoutPageTemplateEntryId = ParamUtil.getLong(
			_request, "layoutPageTemplateEntryId");

		return _layoutPageTemplateEntryId;
	}

	public String getLayoutPageTemplateEntryTitle() throws PortalException {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			getLayoutPageTemplateEntry();

		if (layoutPageTemplateEntry == null) {
			return LanguageUtil.get(_request, "add-page-template");
		}

		return layoutPageTemplateEntry.getName();
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = ParamUtil.getString(
			_request, "orderByCol", "create-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(_request, "orderByType", "asc");

		return _orderByType;
	}

	public String[] getOrderColumns() {
		return new String[] {"create-date", "name"};
	}

	public boolean isDisabledLayoutPageTemplateCollectionsManagementBar()
		throws PortalException {

		if (_hasLayoutPageTemplateCollectionsResults()) {
			return false;
		}

		if (isSearch()) {
			return true;
		}

		return true;
	}

	public boolean isDisabledLayoutPageTemplateEntriesManagementBar()
		throws PortalException {

		if (_hasLayoutPageTemplateEntriesResults()) {
			return false;
		}

		return true;
	}

	public boolean isSearch() {
		if (Validator.isNotNull(getKeywords())) {
			return true;
		}

		return false;
	}

	public boolean isShowAddButton(String actionId) {
		ThemeDisplay themeDisplay = (ThemeDisplay)_request.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (LayoutPageTemplatePermission.contains(
				themeDisplay.getPermissionChecker(),
				LayoutPageTemplatePermission.RESOURCE_NAME,
				LayoutAdminPortletKeys.GROUP_PAGES,
				themeDisplay.getSiteGroupId(), actionId)) {

			return true;
		}

		return false;
	}

	public boolean isShowLayoutPageTemplateCollectionsSearch()
		throws PortalException {

		if (_hasLayoutPageTemplateCollectionsResults()) {
			return true;
		}

		if (isSearch()) {
			return true;
		}

		return false;
	}

	public boolean isShowLayoutPageTemplateEntriesSearch()
		throws PortalException {

		if (_hasLayoutPageTemplateEntriesResults()) {
			return true;
		}

		return false;
	}

	private boolean _hasLayoutPageTemplateCollectionsResults()
		throws PortalException {

		SearchContainer searchContainer =
			getLayoutPageTemplateCollectionsSearchContainer();

		if (searchContainer.getTotal() > 0) {
			return true;
		}

		return false;
	}

	private boolean _hasLayoutPageTemplateEntriesResults()
		throws PortalException {

		SearchContainer searchContainer =
			getLayoutPageTemplateEntriesSearchContainer();

		if (searchContainer.getTotal() > 0) {
			return true;
		}

		return false;
	}

	private String _displayStyle;
	private String _keywords;
	private LayoutPageTemplateCollection _layoutPageTemplateCollection;
	private Long _layoutPageTemplateCollectionId;
	private SearchContainer _layoutPageTemplateCollectionsSearchContainer;
	private SearchContainer _layoutPageTemplateEntriesSearchContainer;
	private LayoutPageTemplateEntry _layoutPageTemplateEntry;
	private Long _layoutPageTemplateEntryId;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final HttpServletRequest _request;

}