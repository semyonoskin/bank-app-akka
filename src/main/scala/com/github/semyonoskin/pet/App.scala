package com.github.semyonoskin.pet

import java.sql.DriverManager

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.github.semyonoskin.pet.repos.{AccountRepo, UserRepo}
import com.github.semyonoskin.pet.routes.Routes.Routes
import com.github.semyonoskin.pet.services.{PaymentService, UsersService}

import scala.concurrent.ExecutionContextExecutor

object App extends App {

  val conn_str = "jdbc:postgresql://localhost:5432/test?user=test_admin&password=1234"

  classOf[org.postgresql.Driver]

  val conn = DriverManager.getConnection(conn_str)


  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "SprayExample")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext


  val userRepo = UserRepo.make(conn)
  val accountRepo = AccountRepo.make(conn)
  val paymentService = PaymentService.make(userRepo, accountRepo)
  val createUserService = UsersService.make(userRepo, accountRepo)

  val g = new Routes(createUserService, paymentService)


  Http().bindAndHandle(g.routes, "localhost", 8000)

  sys.addShutdownHook(conn.close())

}
