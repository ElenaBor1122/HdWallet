package com.walletgenerator.generators.impl;

import static com.walletgenerator.utils.Constants.DERIVATION_NUMBER;
import static com.walletgenerator.utils.Constants.ED_25519_SEED;
import static com.walletgenerator.utils.Constants.EMPTY_STRING;
import static com.walletgenerator.utils.Constants.HARDENED_INDEX_OFFSET;
import static com.walletgenerator.utils.Constants.HMAC_SHA512_ALG;
import static com.walletgenerator.utils.Constants.NUM_ADDRESSES;
import static com.walletgenerator.utils.Constants.QUOTE;
import static com.walletgenerator.utils.Constants.SOL_PATH;

import com.walletgenerator.generators.WalletAddressGenerator;
import com.walletgenerator.model.Wallet;
import com.walletgenerator.utils.CommonUtil;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bitcoinj.crypto.ChildNumber;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;
import org.sol4k.PublicKey;


public class SolAddressGenerator implements WalletAddressGenerator {

    public List<Wallet> generateAddresses(String mnemonic) throws Exception {

        byte[] seed = CommonUtil.generateSeedFromMnemonic(mnemonic);

        Ed25519PrivateKeyParameters masterKey = deriveMasterKey(seed);

        return IntStream
                .range(0, NUM_ADDRESSES)
                .mapToObj(i -> getWallet(i, masterKey))
                .collect(Collectors.toList());
    }

    private static Wallet getWallet(int i, Ed25519PrivateKeyParameters masterKey) {
        try {
            String solPath = SOL_PATH + i + (i < DERIVATION_NUMBER ? QUOTE : EMPTY_STRING);

            List<ChildNumber> paths = CommonUtil.parsePathWithHardAndSoft(solPath);

            Ed25519PrivateKeyParameters childKey = deriveKeyFromPath(masterKey, paths);
            Ed25519PublicKeyParameters publicKey = childKey.generatePublicKey();

            return buildSol(childKey, publicKey, solPath);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static Ed25519PrivateKeyParameters deriveMasterKey(byte[] seed) throws Exception {

        byte[] masterKey = hmacSha512(ED_25519_SEED.getBytes(), seed);

        return new Ed25519PrivateKeyParameters(masterKey, 0);

    }

    private static Ed25519PrivateKeyParameters deriveKeyFromPath(Ed25519PrivateKeyParameters masterKey, List<ChildNumber> pathList)
            throws Exception {

        Ed25519PrivateKeyParameters currentKey = masterKey;

        for (ChildNumber childNumber : pathList) {
            currentKey = deriveChildKey(currentKey, childNumber.getI());
        }

        return currentKey;

    }

    private static Ed25519PrivateKeyParameters deriveChildKey(Ed25519PrivateKeyParameters parentKey, int index) throws Exception {

        byte[] indexBytes = intToByteArray(index | HARDENED_INDEX_OFFSET);
        byte[] data = concatenate(parentKey.getEncoded(), indexBytes);

        byte[] childKey = hmacSha512(parentKey.getEncoded(), data);

        return new Ed25519PrivateKeyParameters(childKey, 0);

    }

    private static byte[] hmacSha512(byte[] key, byte[] data) throws Exception {

        Mac hmac = Mac.getInstance(HMAC_SHA512_ALG);
        hmac.init(new SecretKeySpec(key, HMAC_SHA512_ALG));

        return hmac.doFinal(data);

    }

    private static byte[] intToByteArray(int value) {

        return new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};

    }

    private static byte[] concatenate(byte[] a, byte[] b) {

        byte[] result = new byte[a.length + b.length];

        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);

        return result;

    }

    private static Wallet buildSol(Ed25519PrivateKeyParameters privateKey, Ed25519PublicKeyParameters publicKey, String path) {

        String privateKeyHex = Hex.toHexString(privateKey.getEncoded());
        String publicKeyBase58 = new PublicKey(publicKey.getEncoded()).toBase58();

        return Wallet
                .builder()
                .privateKey(privateKeyHex)
                .publicKey(publicKeyBase58)
                .address(publicKeyBase58)
                .path(path)
                .build();

    }

}
