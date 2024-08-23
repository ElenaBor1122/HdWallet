package com.walletgenerator.generators.impl;

import com.walletgenerator.exception.WalletGenerationException;
import com.walletgenerator.generators.WalletAddressGenerator;
import com.walletgenerator.model.Wallet;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.crypto.ChildNumber;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;

@Slf4j
public class EthAddressGenerator extends WalletAddressGenerator {

    public static final String ETH_PATH = "m/44'/60'/0'/0/";
    public static final int RADIX = 16;


    public List<Wallet> generateAddresses(String mnemonic) throws WalletGenerationException {

        try {

            byte[] seed = generateSeedFromMnemonic(mnemonic);

            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);

            return IntStream
                    .range(0, NUM_ADDRESSES)
                    .mapToObj(i -> getWallet(i, masterKeypair))
                    .collect(Collectors.toList());

        } catch (Exception e) {

            log.error("ETHEREUM WALLET GENERATION FAILED", e);
            throw new WalletGenerationException(ERROR_GENERATING_WALLETS, e);

        }
    }

    private Wallet getWallet(int i, Bip32ECKeyPair masterKeypair) {
        String ethPath = ETH_PATH + i + (i < DERIVATION_NUMBER ? QUOTE : EMPTY_STRING);

        List<ChildNumber> paths = parsePathWithHardAndSoft(ethPath);

        Bip32ECKeyPair derivedKeypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, Arrays
                .stream(paths.toArray(new ChildNumber[0]))
                .mapToInt(ChildNumber::getI)
                .toArray());

        Credentials credentials = Credentials.create(derivedKeypair);

        return buildEth(derivedKeypair, credentials, ethPath);
    }

    private Wallet buildEth(Bip32ECKeyPair derivedKeypair, Credentials credentials, String path) {

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
