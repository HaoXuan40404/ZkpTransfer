package com.webank.ppc.iss.config;

import com.webank.ppc.iss.common.EnumResponseStatus;
import com.webank.ppc.iss.common.EventEnum;
import com.webank.ppc.iss.common.PpcException;
import com.webank.ppc.iss.entity.BlockChain;
import com.webank.ppc.iss.eventsub.RemoveSubscribeEvent;
import com.webank.ppc.iss.service.PpcService;
import lombok.extern.slf4j.Slf4j;

import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.abi.tools.TopicTools;
import org.fisco.bcos.sdk.v3.eventsub.EventSubParams;
import org.fisco.bcos.sdk.v3.eventsub.EventSubscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.util.ArrayList;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "service.data-algorithm-event-enabled", havingValue = "true")
public class DatabaseRemoveEventConfig {

    @Autowired private Client client;

    @Autowired private ServiceConfig serviceConfig;

    @Autowired private ContractConfig contractConfig;

    @Autowired
    private PpcService ppcService;

    @Bean
    public void registerBcosDatabaseRemoveEvent() {
        // 参数设置
        EventSubParams params = new EventSubParams();

        // 最新Event fromBlock设置为"latest"
        BlockChain blockChain = ppcService.getEventStatus(EventEnum.REMOVE.getValue());
        if(blockChain == null) {
            // 如果未找到从头开始
            params.setFromBlock(BigInteger.valueOf(1));
        }
        else {
            params.setFromBlock(BigInteger.valueOf(blockChain.getMaxBlockNumber()));
        }

        // toBlock设置为"latest"，处理至最新区块继续等待新的区块
        params.setToBlock(BigInteger.valueOf(-1));

        // 设置合约地址
        params.addAddress(contractConfig.ppcContractAddress);

        // topics设置为空数组，匹配所有的Event
        ArrayList<Object> topics = new ArrayList<>();
        TopicTools topicTools = new TopicTools(client.getCryptoSuite());
        params.addTopic(0, topicTools.stringToTopic("removeMetaEvent(string,string)"));

        // 注册事件
        RemoveSubscribeEvent callback = new RemoveSubscribeEvent(client, serviceConfig, ppcService);
        EventSubscribe eventSubscribe;
        try {
            eventSubscribe = EventSubscribe.build(client);
        } catch (JniException e) {
            log.error("registerBcosDatabaseRemoveEvent error, " + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "registerBcosDatabaseRemoveEvent failed:" + e.getMessage());
        }
        eventSubscribe.start();
        String registerId = eventSubscribe.subscribeEvent(params, callback);
        log.info("Register remove event successful, registerId = " + registerId);
    }
}
