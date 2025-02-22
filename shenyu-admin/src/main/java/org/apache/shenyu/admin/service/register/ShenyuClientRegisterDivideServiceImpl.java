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

package org.apache.shenyu.admin.service.register;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.admin.listener.DataChangedEvent;
import org.apache.shenyu.admin.model.entity.MetaDataDO;
import org.apache.shenyu.admin.model.entity.SelectorDO;
import org.apache.shenyu.admin.service.MetaDataService;
import org.apache.shenyu.admin.service.SelectorService;
import org.apache.shenyu.admin.service.converter.DivideSelectorHandleConverter;
import org.apache.shenyu.admin.utils.CommonUpstreamUtils;
import org.apache.shenyu.common.constant.Constants;
import org.apache.shenyu.common.dto.SelectorData;
import org.apache.shenyu.common.dto.convert.rule.impl.DivideRuleHandle;
import org.apache.shenyu.common.dto.convert.selector.DivideUpstream;
import org.apache.shenyu.common.enums.ConfigGroupEnum;
import org.apache.shenyu.common.enums.DataEventTypeEnum;
import org.apache.shenyu.common.enums.RpcTypeEnum;
import org.apache.shenyu.common.utils.GsonUtils;
import org.apache.shenyu.common.utils.PluginNameAdapter;
import org.apache.shenyu.register.common.dto.MetaDataRegisterDTO;
import org.apache.shenyu.register.common.dto.URIRegisterDTO;
import org.apache.shenyu.register.common.enums.EventType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * spring mvc http service register.
 */
@Service
public class ShenyuClientRegisterDivideServiceImpl extends AbstractContextPathRegisterService {

    @Resource
    private DivideSelectorHandleConverter divideSelectorHandleConverter;

    @Override
    public String rpcType() {
        return RpcTypeEnum.HTTP.getName();
    }

    @Override
    protected String selectorHandler(final MetaDataRegisterDTO metaDataDTO) {
        return "";
    }

    @Override
    protected String ruleHandler() {
        return new DivideRuleHandle().toJson();
    }

    @Override
    protected void registerMetadata(final MetaDataRegisterDTO dto) {
        if (dto.isRegisterMetaData()) {
            MetaDataService metaDataService = getMetaDataService();
            MetaDataDO exist = metaDataService.findByPath(dto.getPath());
            metaDataService.saveOrUpdateMetaData(exist, dto);
        }
    }

    @Override
    protected String buildHandle(final List<URIRegisterDTO> uriList, final SelectorDO selectorDO) {
        List<DivideUpstream> addList = buildDivideUpstreamList(uriList);
        List<DivideUpstream> canAddList = new CopyOnWriteArrayList<>();
        boolean isEventDeleted = uriList.size() == 1 && EventType.DELETED.equals(uriList.get(0).getEventType());
        if (isEventDeleted) {
            addList.get(0).setStatus(false);
        }
        List<DivideUpstream> existList = GsonUtils.getInstance().fromCurrentList(selectorDO.getHandle(), DivideUpstream.class);
        if (CollectionUtils.isEmpty(existList)) {
            canAddList = addList;
        } else {
            List<DivideUpstream> diffList = addList.stream().filter(upstream -> !existList.contains(upstream)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(diffList)) {
                canAddList.addAll(diffList);
                existList.addAll(diffList);
            }
            List<DivideUpstream> diffStatusList = addList.stream().filter(upstream -> !upstream.isStatus()
                    || existList.stream().anyMatch(e -> e.equals(upstream) && e.isStatus() != upstream.isStatus())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(diffStatusList)) {
                canAddList.addAll(diffStatusList);
            }
        }

        if (doSubmit(selectorDO.getId(), canAddList)) {
            return null;
        }

        List<DivideUpstream> handleList;
        if (CollectionUtils.isEmpty(existList)) {
            handleList = addList;
        } else {
            List<DivideUpstream> aliveList;
            if (isEventDeleted) {
                aliveList = existList.stream().filter(e -> e.isStatus() && !e.equals(addList.get(0))).collect(Collectors.toList());
            } else {
                aliveList = addList;
            }
            handleList = divideSelectorHandleConverter.updateStatusAndFilter(existList, aliveList);
        }
        return GsonUtils.getInstance().toJson(handleList);
    }

    private List<DivideUpstream> buildDivideUpstreamList(final List<URIRegisterDTO> uriList) {
        return uriList.stream()
                .map(dto -> CommonUpstreamUtils.buildDivideUpstream(dto.getProtocol(), dto.getHost(), dto.getPort()))
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }
    
    @Override
    public String offline(final String selectorName, final List<URIRegisterDTO> uriList) {
        final SelectorService selectorService = getSelectorService();
        SelectorDO selectorDO = selectorService.findByNameAndPluginName(selectorName, PluginNameAdapter.rpcTypeAdapter(rpcType()));
        if (Objects.isNull(selectorDO)) {
            return Constants.SUCCESS;
        }
        List<URIRegisterDTO> validUriList = uriList.stream()
                .filter(dto -> Objects.nonNull(dto.getPort()) && StringUtils.isNotBlank(dto.getHost()))
                .collect(Collectors.toList());
        final List<DivideUpstream> needToRemove = buildDivideUpstreamList(validUriList);
        List<DivideUpstream> existList = GsonUtils.getInstance().fromCurrentList(selectorDO.getHandle(), DivideUpstream.class);
        existList.removeAll(needToRemove);
        final String handler = GsonUtils.getInstance().toJson(existList);
        selectorDO.setHandle(handler);
        SelectorData selectorData = selectorService.buildByName(selectorName, PluginNameAdapter.rpcTypeAdapter(rpcType()));
        selectorData.setHandle(handler);
        // update db
        selectorService.updateSelective(selectorDO);
        // publish change event.
        getEventPublisher().publishEvent(new DataChangedEvent(ConfigGroupEnum.SELECTOR, DataEventTypeEnum.UPDATE, Collections.singletonList(selectorData)));
        return Constants.SUCCESS;
    }
}
