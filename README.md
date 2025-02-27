## 1. 证书拷贝

请将证书拷贝到src/main/resources/conf目录下。

## 2. 配置连接节点

请修改application.properties，该文件包含如下信息：
```
### Java sdk configuration
cryptoMaterial.certPath=conf
network.peers[0]=127.0.0.1:20200
#network.peers[1]=127.0.0.1:20201

### System configuration
system.groupId="group0"
system.hexPrivateKey=

### Springboot configuration
server.port=5840

```
其中：
- java sdk configuration配置部分与[javasdk](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/configuration.html)一致。就本例而言，用户需要：
    * 请将network.peers更换成实际的链节点监听地址。
    * cryptoMaterial.certPath设为conf

- System configuration配置部分，需要配置：
    * system.hexPrivateKey是16进制的私钥明文，可运行测试用例中的[keyGeneration](src/test/java/org/example/demo/Demos.java)生成。该配置允许为空，此时系统会随机生成一个私钥。
    * system.groupId设为目标群组，默认为"group0"
    
- 访问api地址：
http://localhost:5840/swagger-ui/index.html#/ppc-controller

## 3. 部署合约

- 部署合约工厂
```bash
[group0]: /apps> deploy PpcContractFactory
transaction hash: 0x521dd53fe7f701d4384191bcb274f0ee6228579f67815c5229614c770f63039b
contract address: 0x610857669da60d63f4c9e30713bb86a49251fe2a
currentAccount: 0x296b46d77e96999b2d8b8ca9e2ff8aa80e933ff9
- ```
- 使用工厂合约部署数据合约，参数8表示部署8次
```bash
[group0]: /apps> call PpcContractFactory 0x610857669da60d63f4c9e30713bb86a49251fe2a createMultiContract 8
transaction hash: 0xb0c8febeb5a414432a6ba45d35f3b2af59328175484dd814698e53742a0cd765
---------------------------------------------------------------------------------------------
transaction status: 0
description: transaction executed successfully
---------------------------------------------------------------------------------------------
Receipt message: Success
Return message: Success
Return value size:0
Return types: ()
Return values:()
 ```
- 使用上面记录的合约地址部署PpcContractController接口合约，记录地址

- 获取部署好的合约地址
```bash
[group0]: /apps> call PpcContractFactory 0x610857669da60d63f4c9e30713bb86a49251fe2a getDeployedContracts
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: ([ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS] )
Return values:([0xdc521c6d08b2750ebc6c62b65d58d96a1b2c418f, 0xe167f42f461e53fc2b3fbe0bf8ecd59dba62504d, 0xae1ca0002a5968323f176d8d130d3a240d53272c, 0xf321aebc1514ec1e56fe976bd1d62a3258b1a665, 0xc59a531b3a4978dde55a52c4be6a66c2181094bf, 0xae95b2e16217b5f6d20824a1f252cdc5b2b110d3, 0xbd6cbfa56b81cc25025613921c08ec0170a44d25, 0x86af118bdd75c9d2adbfe7e9b4c5d09984275f4b] )
```
- 部署接口合约，获取合约地址
```bash
[group0]: /apps>  deploy PpcContractController 0xc8ead4b26b2c6ac14c9fd90d9684c9bc2cc40085 0xd24180cc0fef2f3e545de4f9aafc09345cd08903 0x37a44585bf1e9618fdb4c62c4c96189a07dd4b48 0x31ed5233b81c79d5adddeef991f531a9bbc2ad01 0x6546c3571f17858ea45575e7c6457dad03e53dbb 0xcceef68c9b4811b32c75df284a1396c7c5509561 0x0102e8b6fc8cdf9626fddc1c3ea8c1e79b3fce94 0x33e56a083e135936c1144960a708c43a661706c0
transaction hash: 0xd5b14ff50fcc9e661872c3065241957a33eed04742a700823939c27bc9b133a1
contract address: 0x2b5dcbae97f9d9178e8b051b08c9fb4089bae71b
currentAccount: 0x296b46d77e96999b2d8b8ca9e2ff8aa80e933ff9
```
