/* 
 * MWVSentence.java
 * Copyright (C) 2011 Kimmo Tuukkanen
 * 
 * This file is part of Java Marine API.
 * <http://ktuukkan.github.io/marine-api/>
 * 
 * Java Marine API is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * Java Marine API is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java Marine API. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Side;

public interface VWRSentence extends Sentence {

	/**
	 * Get wind angle.
	 * 
	 * @return Wind angle in degrees.
	 */
	double getAngle();

	/**
	 * Returns the wind speed.
	 * 
	 * @return Wind speed value in KN
	 */
	double getSpeed();

	/**
	 * Get data validity status.
	 * 
	 * @return Data status
	 */
	DataStatus getStatus();

	/**
	 * Tells if the angle is relative or true.
	 * 
	 * @return True if relative to true north, otherwise false (relative to bow)
	 */
	Side getSide();

	/**
	 * Set wind angle.
	 * 
	 * @param angle Wind angle in degrees.
	 * @see #setTrue(boolean)
	 */
	void setAngle(double angle);

	/**
	 * Set the wind speed value.
	 * 
	 * @param speed Wind speed to set in KN.
	 */
	void setSpeed(double speed);

	/**
	 * Set data validity status.
	 * 
	 * @param status Data status to set.
	 */
	void setStatus(DataStatus status);

	/**
	 * Set angle to relative or true.
	 * 
	 * @param isTrue True for true angle, false for relative to bow.
	 * @see #setAngle(double)
	 */
	void setSide(Side side);
}
