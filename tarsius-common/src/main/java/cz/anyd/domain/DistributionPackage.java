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

package cz.anyd.domain;

import java.util.Date;

public class DistributionPackage {
    private Distribution distribution;
    private ApplicationPackage applicationPackage;
    private String version;
    private Date created;

    public Distribution getDistribution() {
        return distribution;
    }

    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    public ApplicationPackage getApplicationPackage() {
        return applicationPackage;
    }

    public void setApplicationPackage(ApplicationPackage applicationPackage) {
        this.applicationPackage = applicationPackage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "DistributionPackage{" +
                "distribution=" + distribution +
                ", applicationPackage=" + applicationPackage +
                ", version='" + version + '\'' +
                ", created=" + created +
                '}';
    }
}
