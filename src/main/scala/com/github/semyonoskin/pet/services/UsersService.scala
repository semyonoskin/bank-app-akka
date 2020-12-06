package com.github.semyonoskin.pet.services

import com.github.semyonoskin.pet.models.{Account, User, UserLogin}
import com.github.semyonoskin.pet.repos.{AccountRepo, UserRepo}

import scala.util.Random

trait UsersService {

  def createUser(login: UserLogin, balance: Long): Unit

  def getBalance(login: UserLogin): Option[Long]               // User service interface

  def getUser(login: UserLogin): Option[User]                // Concrete methods on Users, builded on base operations of User and Account repository
}


object UsersService {

  def make(users: UserRepo, accounts: AccountRepo): UsersService = new Impl(users, accounts)

  final class Impl(users: UserRepo, accounts: AccountRepo) extends UsersService {

    override def createUser(login: UserLogin, balance: Long): Unit = {
      val account = Account(Random.nextLong(), balance)
      val user = User(login, account.id)                                                // User service implementation
      users.put(user)
      accounts.put(account)
    }

    override def getBalance(login: UserLogin): Option[Long] =
      users.get(login).flatMap(user => accounts.get(user.accountId)).map(_.balance)


    override def getUser(login: UserLogin): Option[User] =
      users.get(login)

  }
}