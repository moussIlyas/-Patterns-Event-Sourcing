package com.mouss.commands.dto;


import com.mouss.enums.AccountStatus;

public record UpdateAccountStatusRequestDTO(String accountId, AccountStatus status) {
}
