// This file is part of CosmeticRanks, created on 16/04/2024 (01:21 AM)
// Name : Helper
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.utils;

import io.github.deathgod7.SE7ENLib.database.component.Column;

import java.util.List;

public class Helper {
	public static Column findColumn(List<Column> columns, String columnName) {
		for (Column column : columns) {
			if (column.getName().equals(columnName)) {
				return column;
			}
		}
		return null; // Column with the specified name not found
	}

}
