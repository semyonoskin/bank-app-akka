package com.github.semyonoskin.pet.services

import com.github.semyonoskin.pet.models.UserLogin
import com.github.semyonoskin.pet.repos.{AccountRepo, UserRepo}
import scala.util.{Failure, Success, Try}


trait PaymentService {

  def transfer(sender: UserLogin, receiver: UserLogin, amount: Long): Try[Unit]
}

object PaymentService {

  def make(users: UserRepo, accounts: AccountRepo): PaymentService = new Impl(users, accounts)

  final class Impl(users: UserRepo, accounts: AccountRepo) extends PaymentService {
    def transfer(sender: UserLogin, receiver: UserLogin, amount: Long): Try[Unit] = {
      val result0 = users.get(sender).flatMap { sender =>
        users.get(receiver).flatMap { receiver =>
          accounts.get(sender.accountId).flatMap { senderAccount =>
            accounts.get(receiver.accountId).map { receiverAccount =>
              (senderAccount, receiverAccount)
            }
          }
        }
      }
      val result = for {
        sender          <- users.get(sender)
        receiver        <- users.get(receiver)
        senderAccount   <- accounts.get(sender.accountId)
        receiverAccount <- accounts.get(receiver.accountId)
      } yield (senderAccount, receiverAccount)
      result match {
        case Some((senderAccount, receiverAccount)) if senderAccount.balance >= amount =>
          accounts.updateBalance(senderAccount.id, -amount)
          accounts.updateBalance(receiverAccount.id, amount)
          Success(())
        case Some((senderAccount, receiverAccount)) =>
          Failure(new Exception("Cannot satisfy transfer amount"))
        case None =>
          Failure(new Exception("No such users"))
      }
    }
  }
}