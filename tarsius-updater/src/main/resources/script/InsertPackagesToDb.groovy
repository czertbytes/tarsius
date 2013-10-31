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


package script

import groovy.sql.Sql

if(args.length < 1) {
    throw new Exception("Missing input file!")
}

def sql = Sql.newInstance("jdbc:h2:tcp://localhost/~/tarsius;AUTOCOMMIT=ON", "sa","", "org.h2.Driver" )
def create = new XmlSlurper().parse(args[0])
create.children().each {
    if(it.name() == "package") {
        def packageName = it.@name.text()

        sql.execute("INSERT INTO package (status,name) VALUES (?,?)", 0, packageName)
        def packageIdResult = sql.firstRow("SELECT id FROM package WHERE name=?", packageName)
        def packageId = packageIdResult[0]
        println("PACKAGE: id=${packageId}, name=${packageName}")

        def packageParserIdResult = sql.firstRow("SELECT id FROM parser WHERE class_name=?", it.parser.@class.text())
        def packageParserId = packageParserIdResult[0]
        sql.execute("INSERT INTO package_parser (package_id, parser_id, parser_param1, parser_param2, parser_param3) VALUES (?,?,?,?,?)", packageId, packageParserId, it.parser.param1.text(), it.parser.param2.text(), it.parser?.param3.text())
        println("\tPACKAGE_PARSER: package_id=${packageId}, parser_id=${packageParserId}")

        it.tags.tag.each { tag ->
            def packageTagIdResult = sql.firstRow("SELECT id FROM tag WHERE name=?", tag.text())
            if(packageTagIdResult == null) {
                sql.execute("INSERT INTO tag (name) VALUES (?)", tag.text())
                packageTagIdResult = sql.firstRow("SELECT id FROM tag WHERE name=?", tag.text())
            }
            def packageTagId = packageTagIdResult[0]
            println("\tTAG: id=${packageTagId}, name=${tag.text()}")
        }

        println("")
    } else if(it.name() == "distribution") {
        def distributionName = it.@name.text()
        def distributionVersion = it.@version.text()
        def distributionReleaseName = it.@release_name.text()
        def distributionHomepage = it.@homepage.text()

        sql.execute("INSERT INTO distribution (status,name,homepage,version,release_name) VALUES (?,?,?,?,?)", 0, distributionName, distributionHomepage, distributionVersion, distributionReleaseName)
        def distributionIdResult = sql.firstRow("SELECT id FROM distribution WHERE name=?", distributionName)
        def distributionId = distributionIdResult[0]

        def distributionParserIdResult = sql.firstRow("SELECT id FROM parser WHERE class_name=?", it.parser.@class.text())
        def distributionParserId = distributionParserIdResult[0]
        sql.execute("INSERT INTO distribution_parser (distribution_id, parser_id) VALUES (?,?)", distributionId, distributionParserId)
        println("\tDISTRIBUTION_PARSER: distribution_id=${distributionId}, parser_id=${packageParserId}")


    }
}