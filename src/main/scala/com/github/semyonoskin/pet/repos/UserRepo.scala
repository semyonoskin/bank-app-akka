package com.github.semyonoskin.pet.repos

import java.sql.{Connection, ResultSet, Statement}
import com.github.semyonoskin.pet.models.{User, UserLogin}
import scala.collection.mutable

trait UserRepo {
  def get(login: UserLogin): Option[User]                       // User repository interface

  def put(user: User): Unit                                     // Base operations on Users
}


object UserRepo {

  def make(connection: Connection): UserRepo = new DBImpl(connection)

  final class InMemoryImpl extends UserRepo {
    private val store: mutable.Map[UserLogin, User] = mutable.Map.empty             // User repository implementation in memory

    override def get(login: UserLogin): Option[User] =
      store.get(login)

    override def put(user: User): Unit = store.put(user.login, user)
  }



  final class DBImpl(connection: Connection) extends UserRepo {

    def get(login: UserLogin): Option[User] = {
      val statement: Statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
      val query: ResultSet = statement.executeQuery(s"SELECT * FROM Users WHERE login = '$login';")
      if (query.next()) {
        for {
          login <- Option(query.getString("login"))
          accountid <- Option(query.getLong("accountid"))             // User repository implementation in database
        } yield User(login, accountid)
      }
      else None
    }

    def put(user: User): Unit = {
      val statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
      statement.executeUpdate(s"INSERT INTO Users VALUES ('${user.login}', '${user.accountId}');")
    }
  }
}
