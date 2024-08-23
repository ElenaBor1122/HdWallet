package com.walletgenerator.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Wallet {

    private String privateKey;

    private String publicKey;

    private String address;

    private String path;
}
