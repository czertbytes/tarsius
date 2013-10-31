/*
 * Copyright (c) 2011 by Daniel Hodan <daniel.hodan@anyd.cz>.
 *
 * This file is part of Tarsius.
 *
 * Tarsius is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Tarsius is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Tarsius. If not, see <http://www.gnu.org/licenses/>.
 */

package cz.anyd.common;

public class SQLQuery {

    public static final String PACKAGES_WITH_PARSER_DATA = "SELECT PACKAGE.ID,PACKAGE.NAME,PARSER.CLASS_NAME,PACKAGE_PARSER.PARSER_PARAM1, PACKAGE_PARSER.PARSER_PARAM2, PACKAGE_PARSER.PARSER_PARAM3 FROM PACKAGE JOIN PACKAGE_PARSER ON PACKAGE_PARSER.PACKAGE_ID = PACKAGE.ID JOIN PARSER ON PARSER.ID = PACKAGE_PARSER.PARSER_ID";
    public static final String DISTROS_WITH_PARSER_DATA = "SELECT PACKAGE.ID,PACKAGE.NAME,PARSER.CLASS_NAME,PACKAGE_PARSER.PARSER_PARAM1, PACKAGE_PARSER.PARSER_PARAM2, PACKAGE_PARSER.PARSER_PARAM3 FROM PACKAGE JOIN PACKAGE_PARSER ON PACKAGE_PARSER.PACKAGE_ID = PACKAGE.ID JOIN PARSER ON PARSER.ID = PACKAGE_PARSER.PARSER_ID";
}


/**
 SELECT *
 FROM DISTRIBUTION
 JOIN DISTRIBUTION_PARSER ON DISTRIBUTION_PARSER.DISTRIBUTION_ID = DISTRIBUTION.ID
 JOIN PARSER ON PARSER.ID = DISTRIBUTION_PARSER.DISTRIBUTION_ID
 */