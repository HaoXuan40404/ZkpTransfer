package com.webank.wedpr.zktransfer.config;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.model.ConfigProperty;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SdkBeanConfig {

    @Autowired private SystemConfig systemConfig;
    @Autowired private AccountConfig accountConfig;

    @Autowired private BcosConfig bcosConfig;

    @Bean
    public Client client() throws Exception {
        ConfigProperty configProperty = new ConfigProperty();
        Map<String, Object> account = new HashMap<>();
        account.put("keyStoreDir", accountConfig.getKeyStoreDir());
        account.put("accountAddress", accountConfig.getAccountAddress());
        account.put("password", accountConfig.getAccountPassword());
        account.put("accountFileFormat", accountConfig.getAccountFileFormat());
        configProperty.setAccount(account);
        configNetwork(configProperty);
        configCryptoMaterial(configProperty);

        ConfigOption configOption = new ConfigOption(configProperty);
        Client client = new BcosSDK(configOption).getClient(systemConfig.getGroupId());

        BigInteger blockNumber = client.getBlockNumber().getBlockNumber();
        log.info("Chain connect successful. Current block number {}", blockNumber);

        configCryptoKeyPair(client);
        String address = client.getCryptoSuite().getCryptoKeyPair().getAddress();
        System.out.println("account address:" + address);
        log.info("account address:{}", address);
        log.info("Your account is Gm:{}", client.getCryptoSuite().cryptoTypeConfig == 1);
        client.setExtraData(systemConfig.getAppId());

        return client;
    }

    public void configNetwork(ConfigProperty configProperty) {
        Map peers = bcosConfig.getNetwork();
        configProperty.setNetwork(peers);
    }

    public void configCryptoMaterial(ConfigProperty configProperty) {
        Map<String, Object> cryptoMaterials = bcosConfig.getCryptoMaterial();
        configProperty.setCryptoMaterial(cryptoMaterials);
    }

    public void configCryptoKeyPair(Client client) {
        String hexPrivateKey = accountConfig.getHexPrivateKey();
        if (hexPrivateKey == null || hexPrivateKey.isEmpty()) {
            return;
        }
        if (hexPrivateKey.startsWith("0x") || hexPrivateKey.startsWith("0X")) {
            hexPrivateKey = hexPrivateKey.substring(2);
        }
        client.getCryptoSuite()
                .setCryptoKeyPair(client.getCryptoSuite().getCryptoKeyPair().createKeyPair(hexPrivateKey));
    }
}
