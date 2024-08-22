package com.walletgenerator;


import com.walletgenerator.generators.impl.EthAddressGenerator;
import com.walletgenerator.generators.impl.SolAddressGenerator;
import com.walletgenerator.generators.WalletAddressGenerator;
import com.walletgenerator.model.Wallet;

import static com.walletgenerator.utils.Constants.MNEMONIC;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        WalletAddressGenerator ethUtil = new EthAddressGenerator();
        WalletAddressGenerator solUtil = new SolAddressGenerator();

        List<Wallet> ethAddresses = ethUtil.generateAddresses(MNEMONIC);
        List<Wallet> solAddresses = solUtil.generateAddresses(MNEMONIC);


        System.out.println("ETH Addresses:");
        ethAddresses.forEach(eth ->
                System.out.println("Address: " + eth.getAddress() + "path: " + eth.getPath())
        );

        System.out.println("------------------------");


        System.out.println("SOL Addresses:");
        solAddresses.forEach(sol ->
                System.out.println("Address: " + sol.getAddress() + "path: " + sol.getPath())
        );
    }

}
