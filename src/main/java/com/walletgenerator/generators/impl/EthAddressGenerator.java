package com.walletgenerator.generators.impl;

import static com.walletgenerator.utils.Constants.DERIVATION_NUMBER;
import static com.walletgenerator.utils.Constants.EMPTY_STRING;
import static com.walletgenerator.utils.Constants.ETH_PATH;
import static com.walletgenerator.utils.Constants.NUM_ADDRESSES;
import static com.walletgenerator.utils.Constants.QUOTE;
import static com.walletgenerator.utils.Constants.RADIX;

import com.walletgenerator.generators.WalletAddressGenerator;
import com.walletgenerator.model.Wallet;
import com.walletgenerator.utils.CommonUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bitcoinj.crypto.ChildNumber;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;

public class EthAddressGenerator implements WalletAddressGenerator {

    @Override
    public List<Wallet> generateAddresses(String mnemonic) {

        byte[] seed = CommonUtil.generateSeedFromMnemonic(mnemonic);

        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);

        return IntStream
                .range(0, NUM_ADDRESSES)
                .mapToObj(i -> getWallet(i, masterKeypair))
                .collect(Collectors.toList());
    }

    private static Wallet getWallet(int i, Bip32ECKeyPair masterKeypair) {
        String ethPath = ETH_PATH + i + (i < DERIVATION_NUMBER ? QUOTE : EMPTY_STRING);

        List<ChildNumber> paths = CommonUtil.parsePathWithHardAndSoft(ethPath);

        Bip32ECKeyPair derivedKeypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, Arrays
                .stream(paths.toArray(new ChildNumber[0]))
                .mapToInt(ChildNumber::getI)
                .toArray());

        Credentials credentials = Credentials.create(derivedKeypair);

        return buildEth(derivedKeypair, credentials, ethPath);
    }

    private static Wallet buildEth(Bip32ECKeyPair derivedKeypair, Credentials credentials, String path) {

        return Wallet
                .builder()
                .privateKey(derivedKeypair
                                    .getPrivateKey()
                                    .toString(RADIX))
                .publicKey(derivedKeypair
                                   .getPublicKey()
                                   .toString(RADIX))
                .address(credentials.getAddress())
                .path(path)
                .build();

    }

}
