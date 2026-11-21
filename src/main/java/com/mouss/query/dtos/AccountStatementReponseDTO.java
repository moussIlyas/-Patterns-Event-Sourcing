package com.mouss.query.dtos;

import com.mouss.query.entities.Account;
import com.mouss.query.entities.AccountOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementReponseDTO {
    private Account account;
    private List<AccountOperation> operations;
}
