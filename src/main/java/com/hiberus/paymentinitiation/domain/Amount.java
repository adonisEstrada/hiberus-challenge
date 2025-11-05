package com.hiberus.paymentinitiation.domain;

import lombok.Value;
import java.math.BigDecimal;

@Value
public class Amount {
    BigDecimal amount;
    String currency;
}
