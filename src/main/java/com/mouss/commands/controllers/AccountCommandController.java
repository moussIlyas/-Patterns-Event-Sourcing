package com.mouss.commands.controllers;

import com.mouss.commands.commands.AddAccountCommand;
import com.mouss.commands.commands.CreditAccountCommand;
import com.mouss.commands.commands.DebitAccountCommand;
import com.mouss.commands.commands.UpdateAccountStatusCommand;
import com.mouss.commands.dto.AddNewAccountRequestDTO;
import com.mouss.commands.dto.CreditAccountRequestDTO;
import com.mouss.commands.dto.DebitAccountRequestDTO;
import com.mouss.commands.dto.UpdateAccountStatusRequestDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
@RequestMapping("/commands/accounts")
public class AccountCommandController {
    private CommandGateway commandGateway;
    private EventStore eventStore;

    public AccountCommandController(CommandGateway commandGateway, EventStore eventStore) {
        this.commandGateway = commandGateway;
        this.eventStore = eventStore;
    }

    @PostMapping("/add")
    public CompletableFuture<String> addNewAccount(@RequestBody AddNewAccountRequestDTO request){
         CompletableFuture<String> resposne =  commandGateway.send(new AddAccountCommand(
                UUID.randomUUID().toString(),
                request.initialBalance(),
                request.currency()
        ));
         return resposne;

    }
    @PostMapping("/credit")
    public CompletableFuture<String> creditAccount(@RequestBody CreditAccountRequestDTO request){
        CompletableFuture<String> resposne =  commandGateway.send(new CreditAccountCommand(
                request.accountId(),
                request.amount(),
                request.currency()
        ));
        return resposne;

    }
    @PostMapping("/debit")
    public CompletableFuture<String> debitAccount(@RequestBody DebitAccountRequestDTO request){
        CompletableFuture<String> resposne =  commandGateway.send(new DebitAccountCommand(
                request.accountId(),
                request.amount(),
                request.currency()
        ));
        return resposne;

    }
    @PutMapping("/updateStatus")
    public CompletableFuture<String> debitAccount(@RequestBody UpdateAccountStatusRequestDTO request){
        CompletableFuture<String> resposne =  commandGateway.send(new UpdateAccountStatusCommand(
                request.accountId(),
                request.status()
        ));
        return resposne;

    }


    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e){
        return e.getMessage();
    }

    @GetMapping("/events/{accountId}")
    public Stream eventStore(@PathVariable String accountId){
        return eventStore.readEvents(accountId).asStream();
    }
}
