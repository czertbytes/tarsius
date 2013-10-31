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

package cz.anyd.updater.parser.app

import groovyx.gpars.dataflow.DataFlowVariable
import static groovyx.gpars.dataflow.DataFlow.task
import org.apache.commons.net.ftp.FTPClient

class FtpDirectoryListParser {
    String hostname
    String dir
    def regexp = /(.*?)/

    def download() {
        def files = new DataFlowVariable()
        task {
            List tmpFiles = []
            new FTPClient().with {
                connect(hostname)
                enterLocalPassiveMode()
                login('anonymous', '')
                changeWorkingDirectory(dir)
                fileType = FTPClient.BINARY_FILE_TYPE
                listFiles().each {
                    tmpFiles << it.name
                }
                disconnect()
                files << tmpFiles
            }
        }

        return files.val
    }

    def parse(List files) {
        def version = new DataFlowVariable()
        task {
            def versionX = ''
            def filterClosure = {
                it.findAll { file ->
                    if(file ==~ regexp) {
                        return file
                    }
                }
            }

            filterClosure(files).sort().reverse().each {
                def x = it =~ regexp
                if(versionX == '') {
                    versionX = x[0][1]
                }
            }

            version << versionX
        }

        return version.val
    }
}
