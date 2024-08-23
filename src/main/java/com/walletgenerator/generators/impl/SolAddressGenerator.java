package com.walletgenerator.generators.impl;

import com.walletgenerator.exception.WalletGenerationException;
import com.walletgenerator.generators.WalletAddressGenerator;
import com.walletgenerator.model.Wallet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.crypto.ChildNumber;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;
import org.sol4k.PublicKey;


@Slf4j
public class SolAddressGenerator extends WalletAddressGenerator {

    public static final String HMAC_SHA512_ALG = "HmacSHA512";

    public static final String SOL_PATH = "m/44'/501'/0'/0/";

    public static final String ED_25519_SEED = "ed25519 seed";

    public static final int HARDENED_INDEX_OFFSET = 0x80000000;
    public static final String HMAC_SHA_512_GENERATION_FAILED = "hmacSha512 generation failed ";

    public List<Wallet> generateAddresses(String mnemonic) throws WalletGenerationException {

        try {
            byte[] seed = generateSeedFromMnemonic(mnemonic);

            Ed25519PrivateKeyParameters masterKey = deriveMasterKey(seed);

            return IntStream
                    .range(0, NUM_ADDRESSES)
                    .mapToObj(i -> getWallet(i, masterKey))
                    .collect(Collectors.toList());

        } catch (Exception e) {

            log.error("SOLANA WALLET GENERATION FAILED", e);
            throw new WalletGenerationException(ERROR_GENERATING_WALLETS, e);

        }
    }

    private Ed25519PrivateKeyParameters deriveChildKey(Ed25519PrivateKeyParameters parentKey, int index) {

        byte[] indexBytes = intToByteArray(index | HARDENED_INDEX_OFFSET);
        byte[] data = concatenate(parentKey.getEncoded(), indexBytes);

        byte[] childKey = hmacSha512(parentKey.getEncoded(), data);

        return new Ed25519PrivateKeyParameters(childKey, 0);

    }

    private byte[] hmacSha512(byte[] key, byte[] data) {

        try {
            Mac hmac = Mac.getInstance(HMAC_SHA512_ALG);
            hmac.init(new SecretKeySpec(key, HMAC_SHA512_ALG));
            return hmac.doFinal(data);
        } catch (Exception e) {
            log.error(HMAC_SHA_512_GENERATION_FAILED, e);
            throw new WalletGenerationException(e.getMessage());
        }
    }

    private byte[] intToByteArray(int value) {

        return new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};

    }

    private byte[] concatenate(byte[] a, byte[] b) {

        byte[] result = new byte[a.length + b.length];

        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);

        return result;

    }

    private Wallet buildSol(Ed25519PrivateKeyParameters privateKey, Ed25519PublicKeyParameters publicKey, String path) {

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

    private Wallet getWallet(int i, Ed25519PrivateKeyParameters masterKey) {
        String solPath = SOL_PATH + i + (i < DERIVATION_NUMBER ? QUOTE : EMPTY_STRING);

        List<ChildNumber> paths = parsePathWithHardAndSoft(solPath);

        Ed25519PrivateKeyParameters childKey = deriveKeyFromPath(masterKey, paths);
        Ed25519PublicKeyParameters publicKey = childKey.generatePublicKey();

        return buildSol(childKey, publicKey, solPath);
    }

    private Ed25519PrivateKeyParameters deriveMasterKey(byte[] seed) {

        byte[] masterKey = hmacSha512(ED_25519_SEED.getBytes(), seed);

        return new Ed25519PrivateKeyParameters(masterKey, 0);

    }

    private Ed25519PrivateKeyParameters deriveKeyFromPath(Ed25519PrivateKeyParameters masterKey, List<ChildNumber> pathList) {

        Ed25519PrivateKeyParameters currentKey = masterKey;

        for (ChildNumber childNumber : pathList) {
            currentKey = deriveChildKey(currentKey, childNumber.getI());
        }
        return currentKey;
    }
}
