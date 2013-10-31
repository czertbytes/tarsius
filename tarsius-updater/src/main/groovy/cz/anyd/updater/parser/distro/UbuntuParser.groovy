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

class UbuntuParser extends DebianParser {
    UbuntuParser() {
        super()
        domain = 'packages.ubuntu.com'
        appNamesToPackageNames.remove('firefox')
    }

    def download(name) {
        def releaseName = name.tokenize('_')
        return downloadFile("http://${domain}/${releaseName[0]}/allpackages?format=txt.gz")
    }
}
