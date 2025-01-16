/******************************************************************
 *
 * SMS Gateway
 * 
 * (C) Copyright Pimmy (Kliment Stefanov). 2016  
 * kliment@hotmail.co.uk
 * All Rights Reserved
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 * RESTRICTED RIGHTS:
 *
 * This file may have been supplied under a license.
 * It may be used, disclosed, and/or copied only as permitted
 * under such license agreement. Any copy must contain the
 * above copyright notice and this restricted rights notice.
 * Use, copying, and/or disclosure of the file is strictly
 * prohibited unless otherwise provided in the license agreement.
 *
 ******************************************************************/
package gis;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import log.Logger;
import utils.Constants;

public class GISUtils
{
    private static final String                         CLASS                       = GISUtils.class.getSimpleName();

    /**
	 * Validate co-ordinates and log any possible errors with the value.
	 * 
	 * @param coords
	 * @param mapPoint
	 */
	static boolean validateCoordinates(final String coords, final int mapPoint)
	{
		boolean											valid						= false;

		try
		{
			if (coords != null && coords.length() > 0)
			{
				float									fCoordinate					= Float.valueOf(coords);
				
				switch (mapPoint)
				{
				case Constants.GIS_NORTH_REF:
				case Constants.GIS_SOUTH_REF:
					if (fCoordinate < -90.0f || fCoordinate > 90.0f)
					{
						Logger.write(Logger.MINOR, CLASS, "Coordinates for Latitude fall outside the expected limits: " + fCoordinate);
					} else {
						valid														= true;
					}
					break;
				case Constants.GIS_WEST_REF:
				case Constants.GIS_EAST_REF:
					if (fCoordinate < -180.0f || fCoordinate > 180.0f)
					{
						Logger.write(Logger.MINOR, CLASS, "Coordinates for Longitude fall outside the expected limits: " + fCoordinate);
					} else {
						valid														= true;
					}
					break;
				default:
					break;
				}
			}
		} catch (NumberFormatException e) {
			Logger.write(Logger.MINOR, CLASS, "Invalid coordinates format found: [" + coords + "] map point ref: " + mapPoint);
		} catch (Exception e) {
			Logger.write(Logger.MINOR, CLASS, "Exception during coordinates validation: [" + coords + "] map point ref: " + mapPoint);
		}
		return valid;
	}

	/**
	 * Parse the NW by SE co-ordinates from the bounds parameter.  The bounds
	 * string will take the form ((NW Lat, NW Long), (SE Lat, SE Long)) - e.g.:
	 * ((34.461276680644175, -99.68994179687502), (37.13404478358378, -95.29541054687502))
	 * 
	 * @param bounds the NW and SE grid references
	 * @return Map the 4 compass points, North, South, East and West
	 */
	public static Map<String, Object> getCoordinates(CharSequence bounds)
	{
		Map<String, Object> 							points						= new HashMap<String, Object>(5, 1);

		if (bounds != null && bounds.length() > 0) {
			Pattern										regex						= Pattern.compile(Constants.GIS_COORDS_REGEX);
			Matcher										matcher						= regex.matcher(bounds);

			if (matcher.matches()) {
				String									north						= matcher.group(Constants.GIS_NORTH_REF);
				validateCoordinates(north, Constants.GIS_NORTH_REF);
				points.put(Constants.GIS_NORTH, north);

				String									west						= matcher.group(Constants.GIS_WEST_REF);
				validateCoordinates(west, Constants.GIS_WEST_REF);
				points.put(Constants.GIS_WEST, west);

				String									south						= matcher.group(Constants.GIS_SOUTH_REF);
				validateCoordinates(south, Constants.GIS_SOUTH_REF);
				points.put(Constants.GIS_SOUTH, south);

				String									east						= matcher.group(Constants.GIS_EAST_REF);
				validateCoordinates(east, Constants.GIS_EAST_REF);
				points.put(Constants.GIS_EAST, east);
			} else {
				Logger.write(Logger.MINOR, CLASS, "Failed to parse coordinates from parameter [" + bounds + "] using REG-EX: " + regex.toString());
			}

		}

		return points;
	}
}
