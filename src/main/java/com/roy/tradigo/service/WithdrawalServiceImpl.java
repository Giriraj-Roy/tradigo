package com.roy.tradigo.service;

import com.roy.tradigo.domain.WithdrawalStatus;
import com.roy.tradigo.model.User;
import com.roy.tradigo.model.Withdrawal;
import com.roy.tradigo.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class WithdrawalServiceImpl implements WithdrawalService{

    @Autowired
    private WithdrawalRepository withdrawalRepository;


    @Override
    public Withdrawal requestWithdrawal(Long amount, User user) {
        Withdrawal withdrawal=new Withdrawal();

        withdrawal.setUser(user);
        withdrawal.setAmount(amount);
        withdrawal.setWithdrawalStatus(WithdrawalStatus.PENDING);
        return withdrawalRepository.save(withdrawal);
    }

    @Override
    public Withdrawal proceedWithdrawal(Long withdrawalId, boolean accept) throws Exception {
        Optional<Withdrawal> withdrawal=withdrawalRepository.findById(withdrawalId);
        if (withdrawal.isEmpty()){
            throw new Exception("Withdrawal not Found");
        }
        Withdrawal withdrawal1=withdrawal.get();
        withdrawal1.setDate(LocalDateTime.now());

        if (accept){
            withdrawal1.setWithdrawalStatus(WithdrawalStatus.SUCCESS);
        }
        else {
            withdrawal1.setWithdrawalStatus(WithdrawalStatus.DECLINE);
        }

        return withdrawalRepository.save(withdrawal1);
    }

    @Override
    public List<Withdrawal> getUsersWithdrawalHistory(User user) {
        return withdrawalRepository.findByUserId(user.getId());
    }

    @Override
    public List<Withdrawal> getAllWithdrawalRequest() {
        return withdrawalRepository.findAll();
    }
}
