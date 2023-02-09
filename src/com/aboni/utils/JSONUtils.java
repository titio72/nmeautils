/*
 * Copyright (c) 2022,  Andrea Boni
 * This file is part of NMEARouter.
 * NMEARouter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * NMEARouter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with NMEARouter.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aboni.utils;

import org.json.JSONObject;

public class JSONUtils {

    private JSONUtils() {
    }

    public static boolean addInt(JSONObject json, int v, String attribute) {
        if (v == Integer.MAX_VALUE || v == Integer.MIN_VALUE || json == null || attribute == null || attribute.isEmpty()) {
            return false;
        } else {
            json.put(attribute, v);
            return true;
        }
    }

    public static boolean addDouble(JSONObject json, double v, String attribute) {
        if (Double.isNaN(v) || json == null || attribute == null || attribute.isEmpty()) {
            return false;
        } else {
            json.put(attribute, v);
            return true;
        }
    }

    public static boolean addString(JSONObject json, Object v, String attribute) {
        if (v == null || json == null || attribute == null || attribute.isEmpty()) {
            return false;
        } else {
            json.put(attribute, v.toString());
            return true;
        }
    }

    public static int getAttribute(JSONObject obj, String attribute, int defaultValue) {
        if (obj.has(attribute)) return obj.getInt(attribute);
        else return defaultValue;
    }

    public static double getAttribute(JSONObject obj, String attribute, double defaultValue) {
        if (obj.has(attribute)) return obj.getDouble(attribute);
        else return defaultValue;
    }

    public static String getAttribute(JSONObject obj, String attribute, String defaultValue) {
        if (obj.has(attribute)) return obj.getString(attribute);
        else return defaultValue;
    }
    public static boolean getAttribute(JSONObject obj, String attribute, boolean defaultValue) {
        if (obj.has(attribute)) return obj.getBoolean(attribute);
        else return defaultValue;
    }
}
