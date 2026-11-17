package com.mouss.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.mouss.enums.AccountStatus;
@Getter @AllArgsConstructor
public class AccountCreatedEvent {
    private String accountId;
    private double initialBalance;
    private AccountStatus status;
    private String currency;

}
