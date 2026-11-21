package com.mouss.query.handlers;

import com.mouss.query.dtos.AccountStatementReponseDTO;
import com.mouss.query.entities.Account;
import com.mouss.query.entities.AccountOperation;
import com.mouss.query.queries.GetAccountStatementQuery;
import com.mouss.query.queries.GetAllAccountsQuery;
import com.mouss.query.queries.WatchEventQuery;
import com.mouss.query.repository.AccountRepository;
import com.mouss.query.repository.OperationRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountQueryHandler {
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;

    public AccountQueryHandler(AccountRepository accountRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }
    @QueryHandler
    public List<Account> on(GetAllAccountsQuery query){
        return accountRepository.findAll();
    }
    @QueryHandler
    public AccountStatementReponseDTO on(GetAccountStatementQuery query){
        Account account=accountRepository.findById(query.getAccountId()).get();
        List<AccountOperation> operations=operationRepository.findByAccountId(query.getAccountId());
        return new AccountStatementReponseDTO(account, operations);
    }
    @QueryHandler
    public AccountOperation on(WatchEventQuery query){
        return AccountOperation.builder().build();
    }
}
