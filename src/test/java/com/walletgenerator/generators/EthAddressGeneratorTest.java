package com.walletgenerator.generators;

import static com.walletgenerator.generators.impl.EthAddressGenerator.DERIVATION_NUMBER;
import static com.walletgenerator.generators.impl.EthAddressGenerator.EMPTY_STRING;
import static com.walletgenerator.generators.impl.EthAddressGenerator.ETH_PATH;
import static com.walletgenerator.generators.impl.EthAddressGenerator.QUOTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.walletgenerator.generators.impl.EthAddressGenerator;
import com.walletgenerator.model.Wallet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EthAddressGeneratorTest {
    private static final String TEST_MNEMONIC = "moon call borrow staff hood catch else egg famous surround original below resist observe enact";
    private static final int NUM_ADDRESSES = 10;
    private EthAddressGenerator ethAddressGenerator;

    @BeforeEach
    public void setUp() {
        ethAddressGenerator = new EthAddressGenerator();
    }

    @Test
    public void testGenerateCorrectNumberOfAddresses() {

        List<Wallet> wallets = ethAddressGenerator.generateAddresses(TEST_MNEMONIC);
        assertEquals(NUM_ADDRESSES, wallets.size(), "Should generate the correct number of addresses");
    }

    @Test
    public void testGenerateUniqueAddresses() {

        List<Wallet> wallets = ethAddressGenerator.generateAddresses(TEST_MNEMONIC);
        Set<String> uniqueAddresses = wallets.stream()
                .map(Wallet::getAddress)
                .collect(Collectors.toSet());
        assertEquals(NUM_ADDRESSES, uniqueAddresses.size(), "All generated addresses should be unique");
    }

    @Test
    public void testGenerateCorrectPaths() {

        List<Wallet> wallets = ethAddressGenerator.generateAddresses(TEST_MNEMONIC);
        for (int i = 0; i < NUM_ADDRESSES; i++) {
            String expectedPath = ETH_PATH + i + (i < DERIVATION_NUMBER ? QUOTE : EMPTY_STRING);
            assertEquals(expectedPath, wallets.get(i).getPath(), "Derivation path should match expected format");
        }
    }

    @Test
    public void testGenerateNonEmptyKeys() {

        List<Wallet> wallets = ethAddressGenerator.generateAddresses(TEST_MNEMONIC);
        wallets.forEach(wallet -> {
            assertNotNull(wallet.getPrivateKey(), "Private key should not be null");
            assertNotNull(wallet.getPublicKey(), "Public key should not be null");
            assertNotNull(wallet.getAddress(), "Address should not be null");

            assertFalse(wallet.getPrivateKey().isEmpty(), "Private key should not be empty");
            assertFalse(wallet.getPublicKey().isEmpty(), "Public key should not be empty");
            assertFalse(wallet.getAddress().isEmpty(), "Address should not be empty");
        });
    }

    @Test
    public void testFirstAndLastPaths() {

        List<Wallet> wallets = ethAddressGenerator.generateAddresses(TEST_MNEMONIC);

        String firstPath = wallets.get(0).getPath();
        assertTrue(firstPath.endsWith("0'"), "The first path should end with '0'");

        String lastPath = wallets.get(wallets.size() - 1).getPath();
        assertTrue(lastPath.endsWith("9"), "The last path should end with '9'");
    }

}
