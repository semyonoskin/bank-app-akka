package com.github.semyonoskin.pet.repos

import java.sql.{Connection, ResultSet, Statement}
import com.github.semyonoskin.pet.models.Account
import scala.collection.mutable

trait AccountRepo {
  def get(id: Long): Option[Account]

  def updateBalance(id: Long, amount: Long): Unit             // Accounts repository interface

  def put(account: Account): Unit                             // Base operations on accounts
}

object AccountRepo {

  def make(connection: Connection): AccountRepo = new DBImpl(connection)

  final class InMemoryImpl extends AccountRepo {
    private val store: mutable.Map[Long, Account] = mutable.Map.empty

    override def get(id: Long): Option[Account] = store.get(id)                    // Account repository implementation in memory

    override def updateBalance(id: Long, amount: Long): Unit =
      get(id).foreach(acc => store.update(id, acc.copy(balance = acc.balance + amount)))

    override def put(account: Account): Unit = store.put(account.id, account)

  }

  final class DBImpl(connection: Connection) extends AccountRepo {

    def get(id: Long): Option[Account] = {
      val statement: Statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
      val query: ResultSet = statement.executeQuery(s"SELECT * FROM Accounts WHERE id = '$id';")
      if (query.next()) {
        for {
          id <- Option(query.getLong("id"))
          balance <- Option(query.getLong("balance"))                   // Account repository implementation in database
        } yield Account(id, balance)
      }
      else None
    }

    def put(account: Account): Unit = {
      val statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
      statement.executeUpdate(s"INSERT INTO Accounts VALUES ('${account.id}', '${account.balance}');")
    }

    def updateBalance(id: Long, amount: Long): Unit = {
      val statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
      statement.executeUpdate(s"UPDATE accounts SET balance = balance + $amount WHERE id = $id;")
    }

  }

}