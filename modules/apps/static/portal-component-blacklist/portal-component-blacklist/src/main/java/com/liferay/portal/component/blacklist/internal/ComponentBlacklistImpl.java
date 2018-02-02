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

package com.liferay.portal.component.blacklist.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.component.blacklist.ComponentBlacklist;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.component.blacklist.internal.ComponentBlacklistConfiguration",
	immediate = true, service = ComponentBlacklist.class
)
public class ComponentBlacklistImpl implements ComponentBlacklist {

	@Override
	public boolean contains(String componentName) {
		return _blacklistComponentNames.contains(componentName);
	}

	@Override
	public Set<String> getBlacklistComponentNames() {
		return Collections.unmodifiableSet(_blacklistComponentNames);
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		_bundleListener = new ComponentDisablingBundleListener();

		_bundleContext.addBundleListener(_bundleListener);

		modified(properties);
	}

	@Deactivate
	protected void deactivate() {
		_bundleContext.removeBundleListener(_bundleListener);

		_bundleContext = null;

		_disabledComponentNames.clear();
	}

	protected void disableComponents(
		Bundle bundle, Set<String> blacklistComponentNames) {

		performComponentDescriptionDTOOperation(
			bundle, blacklistComponentNames,
			(bundleSymbolicName, componentDescriptionDTO) -> {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Disabling: ", componentDescriptionDTO.name,
							" from bundle: ", bundle.getSymbolicName()));
				}

				serviceComponentRuntime.disableComponent(
					componentDescriptionDTO);

				_disabledComponentNames.add(componentDescriptionDTO.name);

			});
	}

	protected void enableComponents(
		final Bundle bundle, final Set<String> reactivateComponentNames) {

		performComponentDescriptionDTOOperation(
			bundle, reactivateComponentNames,
			(bundleSymbolicName, componentDescriptionDTO) -> {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Enabling: ", componentDescriptionDTO.name,
							" from bundle: ", bundle.getSymbolicName()));
				}

				serviceComponentRuntime.enableComponent(
					componentDescriptionDTO);

				_disabledComponentNames.remove(componentDescriptionDTO.name);
			});
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		Bundle[] bundles = _bundleContext.getBundles();

		ComponentBlacklistConfiguration componentBlacklistConfiguration =
			ConfigurableUtil.createConfigurable(
				ComponentBlacklistConfiguration.class, properties);

		_blacklistComponentNames = new HashSet<>(
			Arrays.asList(
				componentBlacklistConfiguration.blacklistComponentNames()));

		Set<String> reactivateComponentNames = new HashSet<>();

		_disabledComponentNames.forEach(
			disabledComponentName -> {
				if (!_blacklistComponentNames.contains(disabledComponentName)) {
					reactivateComponentNames.add(disabledComponentName);
				}
			});

		for (Bundle bundle : bundles) {
			disableComponents(bundle, _blacklistComponentNames);

			enableComponents(bundle, reactivateComponentNames);
		}
	}

	protected void performComponentDescriptionDTOOperation(
		Bundle bundle, Set<String> componentNames,
		ComponentDescriptionDTOOperator componentDescriptionDTOOperator) {

		componentNames.forEach(
			componentName -> {
				ComponentDescriptionDTO componentDescriptionDTO =
					serviceComponentRuntime.getComponentDescriptionDTO(
						bundle, componentName);

				if (componentDescriptionDTO != null) {
					componentDescriptionDTOOperator.operate(
						bundle.getSymbolicName(), componentDescriptionDTO);
				}
			});
	}

	@Reference
	protected ServiceComponentRuntime serviceComponentRuntime;

	private static final Log _log = LogFactoryUtil.getLog(
		ComponentBlacklistImpl.class);

	private Set<String> _blacklistComponentNames;
	private BundleContext _bundleContext;
	private BundleListener _bundleListener;
	private final Set<String> _disabledComponentNames = new HashSet<>();

	private interface ComponentDescriptionDTOOperator {

		public void operate(
			String bundleSymbolicName,
			ComponentDescriptionDTO componentDescriptionDTO);

	}

	private class ComponentDisablingBundleListener implements BundleListener {

		@Override
		public void bundleChanged(final BundleEvent bundleEvent) {
			if (bundleEvent.getType() != BundleEvent.STARTED) {
				return;
			}

			Bundle bundle = bundleEvent.getBundle();

			disableComponents(bundle, _blacklistComponentNames);
		}

	}

}