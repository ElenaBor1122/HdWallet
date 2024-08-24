package com.walletgenerator.generators;


import static com.walletgenerator.generators.WalletAddressGenerator.DERIVATION_NUMBER;
import static com.walletgenerator.generators.WalletAddressGenerator.EMPTY_STRING;
import static com.walletgenerator.generators.WalletAddressGenerator.NUM_ADDRESSES;
import static com.walletgenerator.generators.WalletAddressGenerator.QUOTE;
import static com.walletgenerator.generators.impl.SolAddressGenerator.SOL_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.walletgenerator.exception.WalletGenerationException;
import com.walletgenerator.generators.impl.SolAddressGenerator;
import com.walletgenerator.model.Wallet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SolAddressGeneratorTest {

    private static final String TEST_MNEMONIC =
            "moon call borrow staff hood catch else egg famous surround original below resist observe enact";
    private SolAddressGenerator solAddressGenerator;

    @BeforeEach
    void setUp() {
        solAddressGenerator = new SolAddressGenerator();
    }

    @Test
    void testGenerateCorrectNumberOfAddresses() {
        List<Wallet> wallets = solAddressGenerator.generateAddresses(TEST_MNEMONIC);
        assertEquals(NUM_ADDRESSES, wallets.size(), "Should generate the correct number of addresses");
    }

    @Test
    void testGenerateUniqueAddresses() {
        List<Wallet> wallets = solAddressGenerator.generateAddresses(TEST_MNEMONIC);
        Set<String> uniqueAddresses = wallets
                .stream()
                .map(Wallet::getAddress)
                .collect(Collectors.toSet());
        assertEquals(NUM_ADDRESSES, uniqueAddresses.size(), "All generated addresses should be unique");
    }

    @Test
    void testGenerateCorrectPaths() {
        List<Wallet> wallets = solAddressGenerator.generateAddresses(TEST_MNEMONIC);
        for (int i = 0; i < NUM_ADDRESSES; i++) {
            String expectedPath = SOL_PATH + i + (i < DERIVATION_NUMBER ? QUOTE : EMPTY_STRING);
            assertEquals(expectedPath, wallets
                    .get(i)
                    .getPath(), "Derivation path should match expected format");
        }
    }

    @Test
    void testGenerateNonEmptyKeys() {
        List<Wallet> wallets = solAddressGenerator.generateAddresses(TEST_MNEMONIC);
        wallets.forEach(wallet -> {
            assertNotNull(wallet.getPrivateKey(), "Private key should not be null");
            assertNotNull(wallet.getPublicKey(), "Public key should not be null");
            assertNotNull(wallet.getAddress(), "Address should not be null");

            assertFalse(wallet
                                .getPrivateKey()
                                .isEmpty(), "Private key should not be empty");
            assertFalse(wallet
                                .getPublicKey()
                                .isEmpty(), "Public key should not be empty");
            assertFalse(wallet
                                .getAddress()
                                .isEmpty(), "Address should not be empty");
        });
    }

    @Test
    void testFirstAndLastPaths() {
        List<Wallet> wallets = solAddressGenerator.generateAddresses(TEST_MNEMONIC);

        String firstPath = wallets
                .get(0)
                .getPath();
        assertTrue(firstPath.endsWith("0'"), "The first path should end with '0'");

        String lastPath = wallets
                .get(wallets.size() - 1)
                .getPath();
        assertTrue(lastPath.endsWith("9"), "The last path should end with '9'");
    }

    @Test
    void testGenerateAddressesWithEmptyMnemonicThrowsWalletGenerationException() {

        String emptyMnemonic = "";

        assertThrows(WalletGenerationException.class, () -> solAddressGenerator.generateAddresses(emptyMnemonic),
                     "Expected WalletGenerationException to be thrown due to an empty mnemonic");
    }

    @Test
    void testGenerateAddressesWithNullMnemonicThrowsWalletGenerationException() {
        assertThrows(WalletGenerationException.class, () -> solAddressGenerator.generateAddresses(null),
                     "Expected WalletGenerationException to be thrown due to an empty mnemonic");
    }

}
