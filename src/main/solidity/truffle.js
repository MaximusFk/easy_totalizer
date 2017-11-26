module.exports = {
  contracts_build_directory: "./../../../bin/contracts/",
  solc: {
    optimizer: {
      enabled: true,
      runs: 100
    },
    outputSelection: {
      "*": {
        "*": [ "abi", "evm.bytecode" ]
      }
    }
  }
};
