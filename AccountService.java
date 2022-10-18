package se.irori.bank.rest.jobs;

import static se.irori.bank.model.Account;
import static se.irori.bank.ports.Account;

import org.springframework.beans.factory.annotation.Autowired;

public class AccountService {

  @Autowired
  AccountPortFactory portFactory;

  public Account findAccountById(String accountIdentifier) {
	 return portFactory
				.createPort(accountIdentifier)
				.getAccountById(accountIdentifier);
  }
}
