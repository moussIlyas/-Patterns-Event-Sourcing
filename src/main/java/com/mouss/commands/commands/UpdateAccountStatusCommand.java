package com.mouss.commands.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.mouss.enums.AccountStatus;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter @AllArgsConstructor
public class UpdateAccountStatusCommand {
    @TargetAggregateIdentifier
    private String id;
    private AccountStatus status;
}


