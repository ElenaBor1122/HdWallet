package com.walletgenerator.utils;

import org.bitcoinj.crypto.ChildNumber;
import org.web3j.crypto.MnemonicUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.walletgenerator.utils.Constants.MASTER_NODE_IDENTIFIER;
import static com.walletgenerator.utils.Constants.PATH_SEPARATOR;
import static com.walletgenerator.utils.Constants.QUOTE;

public class CommonUtil {

    public static byte[] generateSeedFromMnemonic(String mnemonic) {

        return MnemonicUtils.generateSeed(mnemonic, null);

    }


    public static List<ChildNumber> parsePathWithHardAndSoft(String path) {

        return Arrays.stream(path.split(PATH_SEPARATOR))
                .filter(component -> !component.equals(MASTER_NODE_IDENTIFIER))
                .map(component -> {

                    boolean hard = component.endsWith(QUOTE);
                    int index = Integer.parseInt(hard ? component.substring(0, component.length() - 1) : component);
                    return new ChildNumber(index, hard);

                })
                .collect(Collectors.toList());
    }

}
