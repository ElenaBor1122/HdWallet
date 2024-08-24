package com.walletgenerator.generators;

import com.walletgenerator.exception.WalletGenerationException;
import com.walletgenerator.model.Wallet;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bitcoinj.crypto.ChildNumber;
import org.web3j.crypto.MnemonicUtils;

public abstract class WalletAddressGenerator {

    private static final String PATH_SEPARATOR = "/";
    private static final String MASTER_NODE_IDENTIFIER = "m";
    protected static final String ERROR_GENERATING_WALLETS = "Error generating wallets: ";
    public static final int NUM_ADDRESSES = 10;
    public static final int DERIVATION_NUMBER = 3;

    public static final String QUOTE = "'";
    public static final String EMPTY_STRING = "";

    public abstract List<Wallet> generateAddresses(String mnemonic) throws WalletGenerationException;

    protected List<ChildNumber> parsePathWithHardAndSoft(String path) {

        return Arrays
                .stream(path.split(PATH_SEPARATOR))
                .filter(component -> !component.equals(MASTER_NODE_IDENTIFIER))
                .map(this::getChildNumber)
                .collect(Collectors.toList());
    }

    private ChildNumber getChildNumber(String component) {
        boolean hard = component.endsWith(QUOTE);
        int index = Integer.parseInt(hard ? component.substring(0, component.length() - 1) : component);
        return new ChildNumber(index, hard);
    }

    protected byte[] generateSeedFromMnemonic(String mnemonic) {
        return MnemonicUtils.generateSeed(mnemonic, null);
    }
}
