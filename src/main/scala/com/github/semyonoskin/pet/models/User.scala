package com.github.semyonoskin.pet.models

import spray.json.DefaultJsonProtocol.{LongJsonFormat, StringJsonFormat, jsonFormat2}
import spray.json.RootJsonFormat


final case class User(login: UserLogin, accountId: Long)

object User {

  implicit val userFormat: RootJsonFormat[User] = jsonFormat2(User(_, _))
}