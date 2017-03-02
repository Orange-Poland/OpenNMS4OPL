/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2002-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.rtc.utils;

import java.util.Date;

import org.opennms.netmgt.events.api.EventConstants;
import org.opennms.netmgt.rtc.DataManager;
import org.opennms.netmgt.rtc.datablock.RTCCategory;
import org.opennms.netmgt.xml.rtc.EuiLevel;
import org.opennms.netmgt.xml.rtc.Header;
import org.opennms.netmgt.xml.rtc.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This takes an object of type 'RTCCategory' and creates an XML
 * (bytearray) stream of the format to be sent to the user using classes
 * generated by castor
 *
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya Nataraj </A>
 * @author <A HREF="http://www.opennms.org">OpenNMS.org </A>
 *
 * @see org.opennms.netmgt.rtc.datablock.RTCCategory
 * @see org.opennms.netmgt.xml.rtc.EuiLevel
 */
public class LegacyEuiLevelMapper {
    private static final Logger LOG = LoggerFactory.getLogger(LegacyEuiLevelMapper.class);
    /**
     * The header to be sent out for the availability xml(rtceui.xsd)
     */
    private final Header m_header;
    private final DataManager m_dataMgr;

    /**
     * Constructor
     */
    public LegacyEuiLevelMapper(DataManager dataMgr) {
        m_dataMgr = dataMgr;
        m_header = new Header();
        m_header.setVer("1.9a");
        m_header.setMstation("");
    }

    /**
     * Convert the 'RTCCategory' object to a 'EuiLevel' object.
     *
     * @param rtcCat
     *            the RTCCategory to be converted
     * @return a {@link org.opennms.netmgt.xml.rtc.EuiLevel} object.
     */
    public EuiLevel convertToEuiLevelXML(RTCCategory rtcCat) {
        // current time
        Date curDate = new Date();
        long curTime = curDate.getTime();

        // get the rolling window
        long rWindow = 24L * 60L * 60L * 1000L;

        LOG.debug("curdate: {}", curDate);

        // create the data
        EuiLevel level = new EuiLevel();

        // set created in m_header and add to level
        m_header.setCreated(EventConstants.formatToString(curDate));
        level.setHeader(m_header);

        org.opennms.netmgt.xml.rtc.Category levelCat = new org.opennms.netmgt.xml.rtc.Category();

        // get a handle to data
        synchronized (m_dataMgr) {
            // category label
            levelCat.setCatlabel(rtcCat.getLabel());

            // availability value for this category
            levelCat.setCatvalue(m_dataMgr.getValue(rtcCat, curTime, rWindow));

            // nodes in this category
            for (int nodeID : m_dataMgr.getNodes(rtcCat)) {

                Node levelNode = new Node();
                levelNode.setNodeid(nodeID);

                // value for this node for this category
                levelNode.setNodevalue(m_dataMgr.getValue(nodeID, rtcCat, curTime, rWindow));

                // node service count
                levelNode.setNodesvccount(m_dataMgr.getServiceCount(nodeID, rtcCat));

                // node service down count
                levelNode.setNodesvcdowncount(m_dataMgr.getServiceDownCount(nodeID, rtcCat));

                // add the node
                levelCat.getNode().add(levelNode);
            }
        }

        // add category
        level.getCategory().add(levelCat);

        return level;
    }
}