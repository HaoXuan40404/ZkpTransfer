pragma solidity ^0.8.10;
// import "@openzeppelin/contracts/utils/structs/EnumerableMap.sol";
import "./ZkpPrecompiled.sol";
import "./IERC20.sol";

contract ZkTransfer {
    // using EnumerableSet for EnumerableSet.Bytes32Set;

    enum CommitmentStatus {
        NotExist, // 不存在
        Unspent, // 未花费
        Spent // 已花费
    }

    ZkpPrecompiled zkp;
    IERC20 token;
    
    // r = kdf(sk, index)
    // commitment(vG+rH)到 status
    mapping(bytes => CommitmentStatus) private commitmentSet;
    // viewkey((rG)
    // viewkey到noteCipher
    mapping(bytes => bytes) private noteSet;

    constructor(address erc20) public {
        zkp = ZkpPrecompiled(address(0x5100));
        token = IERC20(erc20);
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
        
    }

    function removeCommitment(bytes memory commitment) internal {
        require(
            commitmentSet[commitment] == CommitmentStatus.Unspent,
            "Commitment already spent"
        );
        // 如果 commitment 存在才删除
        commitmentSet[commitment] = CommitmentStatus.Spent;
    }

    // mint
    function mint(
        bytes memory proof,
        bytes memory commitment,
        bytes memory viewKey,
        bytes memory cipher,
        uint64 value
    ) public {
        // 1. 先检查用户是否有足够的代币余额
        require(token.balanceOf(msg.sender) >= value, "Insufficient balance");

        // 2. 验证金额和commitment相等 (提前验证可以节省gas)
        require(
            zkp.verifyValueEqualityProofWithoutBasePoint(
                value,
                commitment,
                proof
            ),
            "Value equality proof verification failed"
        );
        // 3. 先做授权检查
        uint256 allowance = token.allowance(msg.sender, address(this));
        require(allowance >= value, "Insufficient allowance");

        // 4. 转账操作
        bool success = token.transferFrom(msg.sender, address(this), value);
        require(success, "Token transfer failed");

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
        bytes[] memory proof,
        bytes memory commitment,
        uint64 value,
        address account
    ) public {
        require(commitmentSet[commitment] == CommitmentStatus.Unspent, "commitment not exist");
        removeCommitment(commitment);
        // 1. 验证金额和commitment相等
        require(
            zkp.verifyValueEqualityProofWithoutBasePoint(
                value,
                commitment,
                proof[0]
            ),
            "Value equality proof verification failed"
        );
        // 2. 验证所有权
        require(
            zkp.verifyKnowledgeProofWithoutBasePoint(commitment, proof[1]),
            "verifyKnowledgeProof failed"
        );
        // token.transferFrom(address(this), account, value);
        token.transfer(account, value);
    }
}
