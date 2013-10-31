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

package cz.anyd.updater

import groovy.sql.Sql
import cz.anyd.common.SQLQuery
import cz.anyd.updater.parser.app.ClosureParser
import cz.anyd.updater.parser.app.HtmlPageParser
import cz.anyd.updater.parser.app.FtpDirectoryListParser
import cz.anyd.updater.parser.App
import cz.anyd.updater.parser.AppUpdate
import groovy.xml.MarkupBuilder

class PackagesUpdate {
    def sql = Sql.newInstance("jdbc:h2:tcp://localhost/~/tarsius;AUTOCOMMIT=ON", "sa","", "org.h2.Driver" )

    def getPackages() {
        def packages = []

        sql.eachRow(SQLQuery.PACKAGES_WITH_PARSER_DATA) {
            def packageName = it.name
            def parserClassName = it.class_name
            def packageParser = Class.forName(parserClassName).newInstance()
            def parserParam1 = it.parser_param1
            def parserParam2 = it.parser_param2
            def parserParam3 = it.parser_param3
            if(packageParser instanceof ClosureParser) {
                packageParser.url = parserParam1
                def shell = new GroovyShell()
                packageParser.closure = shell.evaluate("{it -> ${parserParam2} }")
            } else if(packageParser instanceof HtmlPageParser) {
                packageParser.url = parserParam1
                packageParser.regexp = ~parserParam2
            } else if(packageParser instanceof FtpDirectoryListParser) {
                packageParser.hostname = parserParam1
                packageParser.dir = parserParam2
                packageParser.regexp = ~parserParam3
            } else {
                throw new Exception("Unknown packageParser type!")
            }

            packages << new App(name: packageName, parser: packageParser)
        }

        return packages
    }

    def run() {
        def fileNow = String.format('%tY_%<tm_%<td_%<tH_%<tM_%<tS', new GregorianCalendar())
        def now = String.format('%tF %<tT', new GregorianCalendar())
        def appUpdate = new AppUpdate(applications: getPackages())
        appUpdate.run()

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.packages(generated: now) {
            appUpdate.applications.each {
                println("package: ${it.name}, version: ${it.version}")
                "package"(name: it.name, version: it.version)
            }
        }

        new File("${fileNow}_packages.xml") << writer

        appUpdate.applications.each {
            def packageIdResult = sql.firstRow("SELECT id FROM package WHERE name=?", it.name)
            def packageId = packageIdResult[0]
            def oldPackageVersionIdResult = sql.firstRow("SELECT version_id FROM distribution_package_version WHERE distribution_id=1 AND package_id=? AND latest='true'", packageId)

            def packageVersionIdResult = sql.firstRow("SELECT id FROM version WHERE val=?", it.version)
            if(packageVersionIdResult == null) {
                sql.execute("INSERT INTO version (val) VALUES (?)", it.version)
                packageVersionIdResult = sql.firstRow("SELECT id FROM version WHERE val=?", it.version)
            }
            def packageVersionId = packageVersionIdResult[0]

            if(oldPackageVersionIdResult == null) {
                sql.execute("INSERT INTO distribution_package_version (distribution_id,package_id,version_id,latest,created) VALUES (1,?,?,'true',?)", packageId, packageVersionId, new Date())
            } else {
                if(packageVersionId != oldPackageVersionIdResult[0]) {
                    sql.executeUpdate("UPDATE distribution_package_version SET latest='false' WHERE distribution_id=1 AND package_id=? AND latest='true'", packageId)
                    sql.execute("INSERT INTO distribution_package_version (distribution_id,package_id,version_id,latest,created) VALUES (1,?,?,'true',?)", packageId, packageVersionId, new Date())
                }
            }
        }
    }
}
