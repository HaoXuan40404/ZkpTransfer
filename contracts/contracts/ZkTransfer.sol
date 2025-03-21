pragma solidity ^0.8.10;

import "./ZkpPrecompiled.sol";

contract ZkTransfer {
    // using EnumerableSet for EnumerableSet.Bytes32Set;

    enum CommitmentStatus {
        NotExist, // 不存在
        Unspent, // 未花费
        Spent // 已花费
    }

    ZkpPrecompiled zkp;
    address private deployer;
    
    // r = kdf(sk, index)
    // commitment(vG+rH)到 status
    mapping(bytes => CommitmentStatus) private commitmentSet;
    // viewkey((rG)
    // viewkey到noteCipher
    mapping(bytes => bytes) private noteSet;

    event CommitmentAdded(bytes commitment, bytes viewKey, bytes cipher);
    event CommitmentRemoved(bytes commitment);

    constructor() {
        zkp = ZkpPrecompiled(address(0x5100));
        deployer = msg.sender; // Set the deployer as the owner
    }

    function concat(bytes[] memory data) internal pure returns (bytes memory) {
        // 计算所需总长度
        uint totalLength = 0;
        for(uint i = 0; i < data.length; i++) {
            totalLength += data[i].length;
        }

        // 创建结果bytes
        bytes memory result = new bytes(totalLength);
        uint currentIndex = 0;

        // 复制每个bytes到结果中
        for(uint i = 0; i < data.length; i++) {
            bytes memory element = data[i];
            for(uint j = 0; j < element.length; j++) {
                result[currentIndex] = element[j];
                currentIndex++;
            }
        }

        return result;
    }

    function addCommitment(bytes memory commitment, bytes memory viewKey, bytes memory cipher) internal {
        require(
            commitmentSet[commitment] == CommitmentStatus.NotExist,
            "Commitment already exists"
        );
        require(
            keccak256(noteSet[viewKey]) == keccak256(bytes("")) || keccak256(noteSet[viewKey]) == keccak256(cipher),
            "viewKey already exists"
        );
        // 如果 commitment 不存在才添加
        commitmentSet[commitment] = CommitmentStatus.Unspent;
        noteSet[viewKey] = cipher;
        emit CommitmentAdded(commitment, viewKey, cipher); // Emit event
    }

    function removeCommitment(bytes memory commitment) internal {
        require(
            commitmentSet[commitment] == CommitmentStatus.Unspent,
            "Commitment already spent"
        );
        // 如果 commitment 存在才删除
        commitmentSet[commitment] = CommitmentStatus.Spent;
        emit CommitmentRemoved(commitment); // Emit event
    }

    // mint
    function mint(
        bytes memory commitment,
        bytes memory viewKey,
        bytes memory cipher
    ) public {
        require(msg.sender == deployer, "Caller is not deployer");
        // 5. 记录 commitment
        addCommitment(commitment, viewKey, cipher);

        // 3. TODO: 可能需要增加rangeproof验证 待讨论
        // assert(zkp.verifyRangeProof(commitment, proof, blinding_basepoint));
        // zkp.verifyRangeProof(c_point, proof, blinding_basepoint);
        // return zkp.verifyRangeProof(c_point, proof, blinding_basepoint);
    }

    // transfer
    function transfer(
        bytes[] memory intputCommitments,
        bytes[] memory outputCommitments,
        bytes[] memory outputViewKeys,
        bytes[] memory outputNoteCiphers,
        bytes memory relationshipProof,
        bytes[] memory knowledgeProofs,
        bytes[] memory rangeProofs
    ) public {
        require(rangeProofs.length == outputCommitments.length, "range proof length error");
        require(knowledgeProofs.length == intputCommitments.length, "knwoledge proof length error");
        for(uint i = 0; i < intputCommitments.length; i++)
        {
            require(commitmentSet[intputCommitments[i]] == CommitmentStatus.Unspent, "commitment not exist");
        }

        for(uint i = 0; i < outputCommitments.length; i++)
        {
            require(commitmentSet[outputCommitments[i]] == CommitmentStatus.NotExist, "commitment already exist");
        }

        // 2. 验证 balanceProof
        require(
            zkp.verifyMultiSumProofWithoutBasePoint(
                concat(intputCommitments),
                concat(outputCommitments),
                relationshipProof
            ),
            "verifyMultiSumProof failed"
        );

        // 1. 验证 owerProof
        for(uint i = 0; i < intputCommitments.length; i++)
        {
            require(
            zkp.verifyKnowledgeProofWithoutBasePoint(intputCommitments[i], knowledgeProofs[i]),
            "verifyKnowledgeProof failed"
        );
        removeCommitment(intputCommitments[i]);
        }
        
        // 3. 验证 rangeProof
        for(uint i = 0; i < outputCommitments.length; i++)
        {
            require(
            zkp.verifyRangeProofWithoutBasePoint(outputCommitments[i], rangeProofs[i]),
            "verifyRangeProof failed"
        );
        addCommitment(outputCommitments[i], outputViewKeys[i], outputNoteCiphers[i]);
        }
    }

    // burn
    function burn(
        bytes memory proof,
        bytes memory commitment
    ) public {
        require(msg.sender == deployer, "Caller is not deployer");
        require(commitmentSet[commitment] == CommitmentStatus.Unspent, "commitment not exist");
        // 2. 验证所有权
        require(
            zkp.verifyKnowledgeProofWithoutBasePoint(commitment, proof),
            "verifyKnowledgeProof failed"
        );
        removeCommitment(commitment);
    }

    function queryNoteSetCipherByKey(bytes memory queryKey) public view returns (bytes memory) {
        return noteSet[queryKey];
    }

    function queryCommitmentStatus(bytes memory commitment) public view returns (CommitmentStatus) {
        return commitmentSet[commitment];
    }
}
