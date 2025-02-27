package com.webank.ppc.iss.config;

import com.webank.ppc.iss.contracts.PpcContractController;
import com.webank.ppc.iss.controller.PpcController;
import com.webank.ppc.iss.service.FiscoBcosClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "contract")
@Slf4j
public class ContractConfig {

    public String ppcContractAddress;

    @Autowired private Client client;

    // TODO: 扩充成多个 针对数据集 算法之类的，加载密钥
    // TODO: 这里做成map，key是群组id，通过群组id得到对应的client
    @Bean
    public FiscoBcosClient fiscoBcosClient() {
        System.out.println("Ppc contract address:" + ppcContractAddress);
        log.info("Ppc contract address:{}", ppcContractAddress);
        PpcContractController ppcContract =
                PpcContractController.load(ppcContractAddress, client, client.getCryptoSuite().getCryptoKeyPair());
        FiscoBcosClient fiscoBcosClient = new FiscoBcosClient(ppcContract);
        log.debug(
                "Account private key:{}",
                client.getCryptoSuite().getCryptoKeyPair().getHexPrivateKey());
        log.info(
                "Account public key:{}",
                client.getCryptoSuite().getCryptoKeyPair().getHexPublicKey());

        return fiscoBcosClient;
    }
}
