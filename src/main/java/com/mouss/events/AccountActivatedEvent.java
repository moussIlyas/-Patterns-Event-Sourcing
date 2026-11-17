package com.mouss.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.mouss.enums.AccountStatus;

@Getter @AllArgsConstructor
public class AccountActivatedEvent {
    private String accountId;
    private AccountStatus status;
}
