package com.mouss.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.mouss.enums.AccountStatus;

@Getter @AllArgsConstructor
public class AccountCreditedEvent {
    private String accountId;
    private double amount;
    private String currency;

}
