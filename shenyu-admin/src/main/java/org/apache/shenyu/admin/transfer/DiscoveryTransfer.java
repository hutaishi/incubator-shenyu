/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shenyu.admin.transfer;

import org.apache.shenyu.admin.model.dto.DiscoveryHandlerDTO;
import org.apache.shenyu.admin.model.dto.DiscoveryUpstreamDTO;
import org.apache.shenyu.admin.model.dto.ProxySelectorDTO;
import org.apache.shenyu.admin.model.entity.DiscoveryHandlerDO;
import org.apache.shenyu.admin.model.entity.DiscoveryUpstreamDO;
import org.apache.shenyu.admin.model.entity.ProxySelectorDO;
import org.apache.shenyu.common.dto.DiscoveryUpstreamData;
import org.apache.shenyu.common.dto.ProxySelectorData;

import java.util.Properties;

/**
 * DiscoveryTransfer.
 */
public enum DiscoveryTransfer {
    /**
     * The constant INSTANCE.
     */
    INSTANCE;

    /**
     * mapToDo.
     *
     * @param discoveryUpstreamData discoveryUpstreamData
     * @return DiscoveryUpstreamDO
     */
    public DiscoveryUpstreamDO mapToDo(DiscoveryUpstreamData discoveryUpstreamData) {
        return DiscoveryUpstreamDO.builder()
                .discoveryHandlerId(discoveryUpstreamData.getDiscoveryHandlerId())
                .id(discoveryUpstreamData.getId())
                .protocol(discoveryUpstreamData.getProtocol())
                .status(discoveryUpstreamData.getStatus())
                .weight(discoveryUpstreamData.getWeight())
                .props(discoveryUpstreamData.getProps())
                .url(discoveryUpstreamData.getUrl())
                .dateUpdated(discoveryUpstreamData.getDateUpdated())
                .dateCreated(discoveryUpstreamData.getDateCreated()).build();
    }

    /**
     * mapToData.
     *
     * @param discoveryUpstreamDO discoveryUpstreamDO
     * @return DiscoveryUpstreamData
     */
    public DiscoveryUpstreamData mapToData(DiscoveryUpstreamDO discoveryUpstreamDO) {
        DiscoveryUpstreamData discoveryUpstreamData = new DiscoveryUpstreamData();
        discoveryUpstreamData.setId(discoveryUpstreamDO.getId());
        discoveryUpstreamData.setProtocol(discoveryUpstreamDO.getProtocol());
        discoveryUpstreamData.setUrl(discoveryUpstreamDO.getUrl());
        discoveryUpstreamData.setStatus(discoveryUpstreamDO.getStatus());
        discoveryUpstreamData.setDiscoveryHandlerId(discoveryUpstreamDO.getDiscoveryHandlerId());
        discoveryUpstreamData.setWeight(discoveryUpstreamDO.getWeight());
        discoveryUpstreamData.setProps(discoveryUpstreamDO.getProps());
        discoveryUpstreamData.setDateUpdated(discoveryUpstreamDO.getDateUpdated());
        discoveryUpstreamData.setDateCreated(discoveryUpstreamDO.getDateCreated());
        return discoveryUpstreamData;
    }


    /**
     * mapToData.
     *
     * @param discoveryUpstreamDTO discoveryUpstreamDTO
     * @return DiscoveryUpstreamData
     */
    public DiscoveryUpstreamData mapToData(DiscoveryUpstreamDTO discoveryUpstreamDTO) {
        DiscoveryUpstreamData discoveryUpstreamData = new DiscoveryUpstreamData();
        discoveryUpstreamData.setId(discoveryUpstreamDTO.getId());
        discoveryUpstreamData.setProtocol(discoveryUpstreamDTO.getProtocol());
        discoveryUpstreamData.setUrl(discoveryUpstreamDTO.getUrl());
        discoveryUpstreamData.setStatus(discoveryUpstreamDTO.getStatus());
        discoveryUpstreamData.setDiscoveryHandlerId(discoveryUpstreamDTO.getDiscoveryHandlerId());
        discoveryUpstreamData.setWeight(discoveryUpstreamDTO.getWeight());
        discoveryUpstreamData.setProps(discoveryUpstreamDTO.getProps());
        return discoveryUpstreamData;
    }


    public ProxySelectorData mapToData(ProxySelectorDTO proxySelectorDTO){
        ProxySelectorData proxySelectorData = new ProxySelectorData();
        proxySelectorData.setId(proxySelectorDTO.getId());
        proxySelectorData.setName(proxySelectorDTO.getName());
        proxySelectorData.setPluginName(proxySelectorDTO.getPluginName());
        proxySelectorData.setType(proxySelectorDTO.getType());
        proxySelectorData.setForwardPort(proxySelectorDTO.getForwardPort());
        return proxySelectorData;
    }

    public ProxySelectorDTO mapToDTO(ProxySelectorDO proxySelectorDO) {
        ProxySelectorDTO proxySelectorDTO = new ProxySelectorDTO();
        proxySelectorDTO.setName(proxySelectorDO.getName());
        proxySelectorDTO.setType(proxySelectorDO.getType());
        proxySelectorDTO.setProps(proxySelectorDO.getProps());
        proxySelectorDTO.setForwardPort(proxySelectorDO.getForwardPort());
        proxySelectorDTO.setPluginName(proxySelectorDO.getPluginName());
        return proxySelectorDTO;
    }

    public DiscoveryHandlerDTO mapToDTO(DiscoveryHandlerDO discoveryHandlerDO) {
        DiscoveryHandlerDTO discoveryHandlerDTO = new DiscoveryHandlerDTO();
        discoveryHandlerDTO.setDiscoveryId(discoveryHandlerDO.getDiscoveryId());
        discoveryHandlerDTO.setHandler(discoveryHandlerDO.getHandler());
        discoveryHandlerDTO.setProps(discoveryHandlerDO.getProps());
        discoveryHandlerDTO.setListenerNode(discoveryHandlerDO.getListenerNode());
        discoveryHandlerDTO.setId(discoveryHandlerDO.getId());
        return discoveryHandlerDTO;
    }

    public DiscoveryUpstreamDTO mapToDTO(DiscoveryUpstreamDO discoveryUpstreamDO) {
        DiscoveryUpstreamDTO discoveryUpstreamDTO = new DiscoveryUpstreamDTO();
        discoveryUpstreamDTO.setProps(discoveryUpstreamDO.getProps());
        discoveryUpstreamDTO.setStatus(discoveryUpstreamDO.getStatus());
        discoveryUpstreamDTO.setUrl(discoveryUpstreamDO.getUrl());
        discoveryUpstreamDTO.setDiscoveryHandlerId(discoveryUpstreamDO.getDiscoveryHandlerId());
        discoveryUpstreamDTO.setProtocol(discoveryUpstreamDO.getProtocol());
        discoveryUpstreamDTO.setId(discoveryUpstreamDO.getId());
        discoveryUpstreamDTO.setWeight(discoveryUpstreamDO.getWeight());
        return discoveryUpstreamDTO;
    }
}
