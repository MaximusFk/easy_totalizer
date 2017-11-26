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
  },
  networks: {
    test: {
      host: "localhost",
      port: 8545,
      network_id: "*"
    }
  }
};
