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
import cz.anyd.updater.parser.Distro
import cz.anyd.common.SQLQuery
import cz.anyd.updater.parser.app.ClosureParser
import cz.anyd.updater.parser.app.HtmlPageParser
import cz.anyd.updater.parser.app.FtpDirectoryListParser

class DistributionsUpdate {
    def sql = Sql.newInstance("jdbc:h2:tcp://localhost/~/tarsius;AUTOCOMMIT=ON", "sa","", "org.h2.Driver" )

    def getDistros() {
        def distros = []

        /**
         * def distributions = []

        //  Debian
        distributions << new Distro(name: DebianRelease.LENNY, parser: new DebianParser())
        distributions << new Distro(name: DebianRelease.SQUEEZE, parser: new DebianParser())
        distributions << new Distro(name: DebianRelease.WHEEZY, parser: new DebianParser())
        distributions << new Distro(name: DebianRelease.SID, parser: new DebianParser())

        //  Ubuntu
        distributions << new Distro(name: UbuntuRelease.KARMIC_KOALA, parser: new UbuntuParser())
        distributions << new Distro(name: UbuntuRelease.LUCID_LYNX, parser: new UbuntuParser())
        distributions << new Distro(name: UbuntuRelease.MAVERICK_MEERKAT, parser: new UbuntuParser())
        distributions << new Distro(name: UbuntuRelease.NATTY_NARWHAL, parser: new UbuntuParser())

        def distroUpdate = new DistroUpdate(distributions: distributions)
        distroUpdate.run()

        return distroUpdate.distributions
         */

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

            distros << new Distro(name: packageName, parser: packageParser)
        }

        return distros
    }

    def run() {
    }
}
