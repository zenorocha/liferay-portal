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

package com.liferay.layout.page.template.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryInstanceLink;
import com.liferay.fragment.service.FragmentCollectionServiceUtil;
import com.liferay.fragment.service.FragmentEntryInstanceLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryServiceUtil;
import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateEntryException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryNameException;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
@Sync
public class LayoutPageTemplateEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test(expected = DuplicateLayoutPageTemplateEntryException.class)
	public void testAddDuplicateLayoutPageTemplateEntries() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateCollectionServiceUtil.
				addLayoutPageTemplateCollection(
					_group.getGroupId(), "Layout Page Template Collection",
					null, serviceContext);

		LayoutPageTemplateEntryServiceUtil.addLayoutPageTemplateEntry(
			_group.getGroupId(),
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			"Layout Page Template Entry", null, serviceContext);

		LayoutPageTemplateEntryServiceUtil.addLayoutPageTemplateEntry(
			_group.getGroupId(),
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			"Layout Page Template Entry", null, serviceContext);
	}

	@Test(expected = LayoutPageTemplateEntryNameException.class)
	public void testAddLayoutPageEntryWithNullName() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateCollectionServiceUtil.
				addLayoutPageTemplateCollection(
					_group.getGroupId(), "Layout Page Template Collection",
					null, serviceContext);

		LayoutPageTemplateEntryServiceUtil.addLayoutPageTemplateEntry(
			_group.getGroupId(),
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			null, null, serviceContext);
	}

	@Test
	public void testAddLayoutPageTemplateEntry() throws PortalException {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateCollectionServiceUtil.
				addLayoutPageTemplateCollection(
					_group.getGroupId(), "Layout Page Template Collection",
					null, serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryServiceUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				"Layout Page Template Entry", null, serviceContext);

		Assert.assertEquals(
			"Layout Page Template Entry", layoutPageTemplateEntry.getName());
	}

	@Test(expected = LayoutPageTemplateEntryNameException.class)
	public void testAddLayoutPageTemplateEntryWithEmptyName() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateCollectionServiceUtil.
				addLayoutPageTemplateCollection(
					_group.getGroupId(), "Layout Page Template Collection",
					null, serviceContext);

		LayoutPageTemplateEntryServiceUtil.addLayoutPageTemplateEntry(
			_group.getGroupId(),
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			StringPool.BLANK, null, serviceContext);
	}

	@Test
	public void testAddLayoutPageTemplateEntryWithFragmentEntries()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateCollectionServiceUtil.
				addLayoutPageTemplateCollection(
					_group.getGroupId(), "Layout Page Template Collection",
					null, serviceContext);

		FragmentCollection fragmentCollection =
			FragmentCollectionServiceUtil.addFragmentCollection(
				_group.getGroupId(), "Fragment Collection", StringPool.BLANK,
				serviceContext);

		FragmentEntry fragmentEntry1 =
			FragmentEntryServiceUtil.addFragmentEntry(
				_group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				"Fragment Entry 1", null, null, null, serviceContext);

		FragmentEntry fragmentEntry2 =
			FragmentEntryServiceUtil.addFragmentEntry(
				_group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				"Fragment Entry 2", null, null, null, serviceContext);

		List<FragmentEntry> fragmentEntryInstanceLinkEntries =
			new ArrayList<>();

		fragmentEntryInstanceLinkEntries.add(fragmentEntry1);
		fragmentEntryInstanceLinkEntries.add(fragmentEntry2);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryServiceUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				"Layout Page Template Entry", fragmentEntryInstanceLinkEntries,
				serviceContext);

		List<FragmentEntryInstanceLink> actualLayoutPageTemplateEntriesCount =
			FragmentEntryInstanceLinkLocalServiceUtil.
				getFragmentEntryInstanceLinks(
					_group.getGroupId(),
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			actualLayoutPageTemplateEntriesCount.toString(), 2,
			actualLayoutPageTemplateEntriesCount.size());
	}

	@Test
	public void testAddMultipleLayoutPageTemplateEntries()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateCollectionServiceUtil.
				addLayoutPageTemplateCollection(
					_group.getGroupId(), "Layout Page Template Collection",
					null, serviceContext);

		List<LayoutPageTemplateEntry> originalLayoutPageTemplateEntries =
			LayoutPageTemplateEntryServiceUtil.getLayoutPageTemplateEntries(
				layoutPageTemplateCollection.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		LayoutPageTemplateEntryServiceUtil.addLayoutPageTemplateEntry(
			_group.getGroupId(),
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			"Layout Page Template Entry 1", null, serviceContext);

		LayoutPageTemplateEntryServiceUtil.addLayoutPageTemplateEntry(
			_group.getGroupId(),
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			"Layout Page Template Entry 2", null, serviceContext);

		List<LayoutPageTemplateEntry> actualLayoutPageTemplateEntries =
			LayoutPageTemplateEntryServiceUtil.getLayoutPageTemplateEntries(
				layoutPageTemplateCollection.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			actualLayoutPageTemplateEntries.toString(),
			originalLayoutPageTemplateEntries.size() + 2,
			actualLayoutPageTemplateEntries.size());
	}

	@Test
	public void testDeleteLayoutPageTemplateEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateCollectionServiceUtil.
				addLayoutPageTemplateCollection(
					_group.getGroupId(), "Layout Page Template Collection",
					null, serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryServiceUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				"Layout Page Template Entry", null, serviceContext);

		LayoutPageTemplateEntryServiceUtil.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertNull(
			LayoutPageTemplateEntryServiceUtil.fetchLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
	}

	@Test
	public void testRemoveFragmentsFromLayoutPageTemplateEntry()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateCollectionServiceUtil.
				addLayoutPageTemplateCollection(
					_group.getGroupId(), "Layout Page Template Collection",
					null, serviceContext);

		FragmentCollection fragmentCollection =
			FragmentCollectionServiceUtil.addFragmentCollection(
				_group.getGroupId(), "Fragment Collection", StringPool.BLANK,
				serviceContext);

		FragmentEntry fragmentEntry1 =
			FragmentEntryServiceUtil.addFragmentEntry(
				_group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				"Fragment Entry 1", null, null, null, serviceContext);

		FragmentEntry fragmentEntry2 =
			FragmentEntryServiceUtil.addFragmentEntry(
				_group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				"Fragment Entry 2", null, null, null, serviceContext);

		List<FragmentEntry> fragmentEntryInstanceLinkEntries =
			new ArrayList<>();

		fragmentEntryInstanceLinkEntries.add(fragmentEntry1);
		fragmentEntryInstanceLinkEntries.add(fragmentEntry2);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryServiceUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				"Layout Page Template Entry", fragmentEntryInstanceLinkEntries,
				serviceContext);

		LayoutPageTemplateEntryServiceUtil.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), "New name",
			new ArrayList<FragmentEntry>(), serviceContext);

		List<FragmentEntryInstanceLink> actualLayoutPageTemplateEntriesCount =
			FragmentEntryInstanceLinkLocalServiceUtil.
				getFragmentEntryInstanceLinks(
					_group.getGroupId(),
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			actualLayoutPageTemplateEntriesCount.toString(), 0,
			actualLayoutPageTemplateEntriesCount.size());
	}

	@DeleteAfterTestRun
	private Group _group;

}