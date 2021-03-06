/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2019-2019 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2019 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.graph.api.info;

import org.opennms.netmgt.graph.api.Vertex;

/**
 * Meta information of a graph.
 * This allows to fetch a minimal set of information (namespace, label, description) without loading the whole graph.
 *
 * @param <V> the vertex type
 */
public interface GraphInfo<V extends Vertex> {

    /** The namespace of the graph. Should be unique over all Graphs */
    String getNamespace();

    /**
     * A short description of the graph to help user's understand what the context of the graph is, e.g.:
     * "This provider shows the hierarchy of the defined Business Services and their computed operational states."
     */
    String getDescription();

    /** A user friendly name/label of the graph, e.g. "Business Service Graph" */
    String getLabel();

    /**
     * Returns the parent type of the vertex within the graph.
     * Vertices in a graph may be of different types, but they should all share the same parent vertex.
     * The vertex type is also relevant for implementing type safe search or enrichment later on.
     * see {@link org.opennms.netmgt.graph.api.aware.NodeRefAware} for a concrete example.
     *
     * @return The parent type of the vertices within the graph
     */
    Class<V> getVertexType();
}
