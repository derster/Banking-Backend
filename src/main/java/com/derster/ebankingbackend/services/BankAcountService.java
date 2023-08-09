package com.derster.ebankingbackend.services;

import com.derster.ebankingbackend.entities.BankAccount;
import com.derster.ebankingbackend.entities.CurrentAccount;
import com.derster.ebankingbackend.entities.SavingAccount;
import com.derster.ebankingbackend.repositories.BankAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BankAcountService {
    @Autowired
    private BankAccountRepository bankAccountRepository;

    public void consulter(){
        BankAccount bankAccount = bankAccountRepository.findById("4ef1e3a6-0b24-430e-a2e8-70960155b3ef").orElse(null);

        if (bankAccount != null){

            System.out.println("****************************");

            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getCreatedAt());
            System.out.println(bankAccount.getCustomer().getName());
            System.out.println(bankAccount.getClass().getSimpleName());
            if (bankAccount instanceof CurrentAccount){
                System.out.println("Over Draft =>  "+((CurrentAccount) bankAccount).getOverDraft());
            }else if (bankAccount instanceof SavingAccount){
                System.out.println("Rate => "+((SavingAccount) bankAccount).getInterestRate());
            }

            bankAccount.getAccountOperations().forEach(op->{
                System.out.println(op.getType()+"\t"+op.getOperationDate()+"\t"+op.getAmount()+"\t");
            });
        }
    }
}
