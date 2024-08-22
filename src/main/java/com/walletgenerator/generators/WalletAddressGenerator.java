package com.walletgenerator.generators;


import com.walletgenerator.model.Wallet;

import java.util.List;

public interface WalletAddressGenerator {

    List<Wallet> generateAddresses(String mnemonic) throws Exception;

}
