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

package com.liferay.frontend.taglib.chart.model;

import com.liferay.portal.kernel.json.JSON;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Iván Zaera Avellón
 */
public abstract class ChartConfig<ColumnType extends Column>
	extends ChartObject {

	public void addColumn(ColumnType column) {
		get("columns", ArrayList.class).add(column);
	}

	public void addColumns(Collection<? extends ColumnType> columns) {
		for (ColumnType column : columns) {
			addColumn(column);
		}
	}

	public void addColumns(ColumnType... columns) {
		for (ColumnType column : columns) {
			addColumn(column);
		}
	}

	@JSON(include = false)
	public AxisX getAxisX() {
		return get("axisX", AxisX.class);
	}

	@JSON(include = false)
	public AxisY getAxisY() {
		return get("axisY", AxisY.class);
	}

	@JSON(include = false)
	public AxisY2 getAxisY2() {
		return get("axisY2", AxisY2.class);
	}

	public void setAxisRotated(boolean rotated) {
		set("axisRotated", rotated);
	}

}