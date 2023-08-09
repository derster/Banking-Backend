package com.derster.ebankingbackend;

import com.derster.ebankingbackend.dtos.BankAccountDTO;
import com.derster.ebankingbackend.dtos.CurrentBankAccountDTO;
import com.derster.ebankingbackend.dtos.CustomerDTO;
import com.derster.ebankingbackend.dtos.SavingBankAccountDTO;
import com.derster.ebankingbackend.entities.*;
import com.derster.ebankingbackend.enums.AccountStatus;
import com.derster.ebankingbackend.enums.OperationType;
import com.derster.ebankingbackend.exceptions.BalanceNotSufficentException;
import com.derster.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.derster.ebankingbackend.exceptions.CustomerNotFoundException;
import com.derster.ebankingbackend.repositories.AccountOperationRepository;
import com.derster.ebankingbackend.repositories.BankAccountRepository;
import com.derster.ebankingbackend.repositories.CustomerRepository;
import com.derster.ebankingbackend.services.BankAccountService;
import com.derster.ebankingbackend.services.BankAcountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbankingBackendApplication.class, args);
	}

	//@Bean
	CommandLineRunner start(CustomerRepository customerRepository,
							BankAccountRepository bankAccountRepository){
		return args -> {

			Stream.of("Modeste", "Marc", "Maxence").forEach(name->{
				Customer customer = new Customer();
				customer.setName(name);
				customer.setEmail(name.toLowerCase()+"@gmail.com");
				customerRepository.save(customer);
			});

			customerRepository.findAll().forEach(customer -> {
				CurrentAccount currentAccount = new CurrentAccount();

				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random()*90000);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(customer);
				currentAccount.setOverDraft(9000);
				bankAccountRepository.save(currentAccount);

				SavingAccount savingAccount = new SavingAccount();
				savingAccount.setId(UUID.randomUUID().toString());
				savingAccount.setBalance(Math.random()*90000);
				savingAccount.setCreatedAt(new Date());
				savingAccount.setStatus(AccountStatus.CREATED);
				savingAccount.setCustomer(customer);
				savingAccount.setInterestRate(5.5);
				bankAccountRepository.save(savingAccount);
			});

		};
	}

	@Bean
	CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
		return args -> {
			Stream.of("Modeste", "Marc", "Maxence").forEach(name -> {
				CustomerDTO customerDTO = new CustomerDTO();
				customerDTO.setName(name);
				customerDTO.setEmail(name.toLowerCase()+"@gmail.com");
				bankAccountService.saveCustomer(customerDTO);
			});

			bankAccountService.listCustomer().forEach(customer -> {
				try {
					bankAccountService.saveCurrentBankAccount(Math.random()*90000, 9000, customer.getId());
					bankAccountService.saveSavingBankAccount(Math.random()*120000, 5.5, customer.getId());


				} catch (CustomerNotFoundException e) {
					e.printStackTrace();
				}
			});


			List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();

			for(BankAccountDTO bankAccount: bankAccounts) {
				for (int i = 0; i<10; i++){
					String accountId;
					if (bankAccount instanceof SavingBankAccountDTO){
						accountId = ((SavingBankAccountDTO) bankAccount).getId();
					}else{
						accountId = ((CurrentBankAccountDTO) bankAccount).getId();
					}
					bankAccountService.credit(accountId, 10000+Math.random()*120000, "Credit");
					bankAccountService.debit(accountId, 1000+Math.random()*9000, "Debit");
				}
			}

		};
	}

}
