package com.walletgenerator;


import com.walletgenerator.generators.WalletAddressGenerator;
import com.walletgenerator.generators.impl.EthAddressGenerator;
import com.walletgenerator.generators.impl.SolAddressGenerator;
import com.walletgenerator.model.Wallet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Main {

    public static final String MNEMONIC = "moon call borrow staff hood catch else egg famous surround original below resist observe enact";

    public static void main(String[] args) {

        WalletAddressGenerator ethAddressGenerator = new EthAddressGenerator();
        WalletAddressGenerator solAddressGenerator = new SolAddressGenerator();

        List<Wallet> ethAddresses = ethAddressGenerator.generateAddresses(MNEMONIC);
        List<Wallet> solAddresses = solAddressGenerator.generateAddresses(MNEMONIC);

        log.info("ETH Addresses:");
        ethAddresses.forEach(eth -> log.info("Address: {}, path: {}", eth.getAddress(), eth.getPath()));

        log.info("------------------------");

        log.info("SOL Addresses:");
        solAddresses.forEach(sol -> log.info("Address: {}, path: {}", sol.getAddress(), sol.getPath()));
    }

}
