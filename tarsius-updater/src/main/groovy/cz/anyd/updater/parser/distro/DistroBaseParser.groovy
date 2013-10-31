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

import groovyx.gpars.dataflow.DataFlowVariable
import static groovyx.gpars.dataflow.DataFlow.task
//import cz.anyd.common.Application

abstract class DistroBaseParser {
    def name = ''
    protected def appNamePkgNames = [:]

    def downloadFile(address) {
        def file = new DataFlowVariable()
        task {
            def fileName = File.createTempFile("tarsius", ".tmp")
            def bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileName))
            bufferedOutputStream << new URL(address).openStream()
            bufferedOutputStream.close()
            file << fileName
        }
        return file.val
    }

    def appNamesToPackageNamesWithVersions(packageNamesWithVersions) {
        def namesWithVersions = [:]
        for(def appName : Application.ALL) {
            String tmpPackageName = appNamePkgNames.get(appName.toString())
            if(tmpPackageName != null) {
                namesWithVersions.put(tmpPackageName, packageNamesWithVersions.get(tmpPackageName))
            } else {
                namesWithVersions.put(appName, packageNamesWithVersions.get(appName));
            }
        }

        return namesWithVersions
    }

    def packageNamesToAppNamesWithVersions(packageNamesWithVersions) {
        def namesWithVersions = [:]
        for(def appName : Application.ALL) {
            String tmpAppName = appNamePkgNames.get(appName)
            if(tmpAppName != null) {
                namesWithVersions.put(appName, packageNamesWithVersions.get(tmpAppName))
            } else {
                namesWithVersions.put(appName, packageNamesWithVersions.get(appName))
            }
        }

        return namesWithVersions
    }
}
