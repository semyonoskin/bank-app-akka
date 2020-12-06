package com.github.semyonoskin.pet.routes

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives.{pathPrefix, _}
import akka.http.scaladsl.server.Route
import com.github.semyonoskin.pet.models.UserLogin
import com.github.semyonoskin.pet.services.{PaymentService, UsersService}
import spray.json.DefaultJsonProtocol.{LongJsonFormat, StringJsonFormat, jsonFormat2, jsonFormat3}
import spray.json.RootJsonFormat

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}


object Routes {


  final case class UserAccount(login: UserLogin, balance: Long)

  object UserAccount {
    implicit val userAccFormat: RootJsonFormat[UserAccount] = jsonFormat2(UserAccount(_, _)) // JSON Format for returning users's balance and login
  }


  final case class CreateUserRequest(login: UserLogin, initBalance: Long)

  object CreateUserRequest { // JSON Format request for creating user
    implicit val userFormat: RootJsonFormat[CreateUserRequest] = jsonFormat2(CreateUserRequest(_, _))
  }


  final case class TransferAmountRequest(sender: UserLogin, receiver: UserLogin, amount: Long)

  object TransferAmountRequest {
    implicit val amountRequest: RootJsonFormat[TransferAmountRequest] = jsonFormat3(TransferAmountRequest(_, _, _))
  }


  final class Routes(usersService: UsersService, paymentService: PaymentService)(implicit system: ActorSystem[Nothing], ec: ExecutionContextExecutor) {

    val routes: Route = concat(
      get {
        pathPrefix("user" / Segment / "account") { login =>
          val us = Future(usersService.getBalance(login)).map(balanceOpt => balanceOpt.map(UserAccount(login, _)))
          onSuccess(us) {
            case Some(user) => complete(user)
            case None => complete(s"User $login not found")
          } // Routes that describes requests to server with all operations
        }
      },
      get {
        pathPrefix("user" / Segment) { login =>
          val us = Future(usersService.getUser(login))
          onSuccess(us) {
            case Some(user) => complete(user)
            case None => complete(s"User $login not found")
          }
        }
      },

      post {
        path("create-user") {
          entity(as[CreateUserRequest]) { req =>
            val action = Future(usersService.createUser(req.login, req.initBalance))
            onSuccess(action)(complete("User created"))
          }
        }
      },
      post {
        path("transfer") {
          entity(as[TransferAmountRequest]) { req =>
            onSuccess(Future(paymentService.transfer(req.sender, req.receiver, req.amount))) {
              case Success(value) => complete("transfered")
              case Failure(_) => complete("failure")
            }
          }
        }
      }
    )
  }
}