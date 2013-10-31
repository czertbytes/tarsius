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

package cz.anyd.updater.parser.distro

import java.util.zip.GZIPInputStream

class DebianParser extends DistroBaseParser {
    def domain = 'packages.debian.org'
    def appNamesToPackageNames = ['mysql':'mysql-server',
            'redis':'redis-server',
            'firebird':'firebird2.5-classic',
            'kde':'kdebase-bin',
            'xfce':'xfce4',
            'firefox':'iceweasel',
            'chromium':'chromium-browser',
            'mono':'mono-complete',
            'haskell':'haskell-platform',
            'php':'php5',
            'lua':'lua5.1']

    def download(name) {
        this.name = name
        return downloadFile("http://${domain}/${name}/allpackages?format=txt.gz")
    }

    def parse(File file) {
        if(name == DebianRelease.LENNY) {
            appNamesToPackageNames.put('firebird', 'firebird2.0-classic')
        }
        appNamePkgNames = appNamesToPackageNames

        Map<String, String> packagesWithVersion = appNamesToPackageNamesWithVersions([:])
        def pkgNames = packagesWithVersion.keySet().toList()

        def VERSIONS_BLOCK_RE = /\(([^)]*)\) /
        def VERSION_BLOCK_STRING_RE = /([^\[\]]*?)/
        def VERSION_ARCHITECTURES_RE = /(?:, ?)?(?:([^\]]*?) \[([^\]]*?)\])/
        def VERSION_RE = /^(?:(?:[^:]+):)?([^~\-\+]*)/

        def parsePackageLine = {packageName, line ->
            def version
            def versionsBlock = line =~ VERSIONS_BLOCK_RE
            def versionBlockString = versionsBlock[0][-1] as String

            if(versionBlockString ==~ VERSION_BLOCK_STRING_RE) {
                def x = versionBlockString =~ VERSION_RE
                version = x[0][1]
            } else {
                def versionsArchitectures = versionsBlock[0][-1] =~ VERSION_ARCHITECTURES_RE
                (0..versionsArchitectures.groupCount()-1).each {
                    def architectures = versionsArchitectures[it][-1] as String
                    if(architectures.split(", ").find { ai -> ai == 'i386'} != null) {
                        def x = versionsArchitectures[it][-2] as String =~ VERSION_RE
                        version = x[0][1]
                    }
                }
            }

            if(version != null) {
                packagesWithVersion.put(packageName, version)
            }
        }

        def parseAllPackagesLine = { line ->
            (0..<pkgNames.size()).each { i ->
                if(line.startsWith("${pkgNames[i]} ")) {
                    parsePackageLine(pkgNames[i], line)
                    pkgNames.remove(pkgNames[i])
                }
            }
        }

        new GZIPInputStream(new FileInputStream(file)).withStream {
            it.eachLine {
                def line = it.replaceAll(".dfsg","~")  //  TODO: fixme, replace .dfsg with ~ for easy parsing
                parseAllPackagesLine(line)
            }
        }

        return packageNamesToAppNamesWithVersions(packagesWithVersion)
    }
}