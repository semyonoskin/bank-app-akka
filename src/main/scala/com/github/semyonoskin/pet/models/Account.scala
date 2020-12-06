package com.github.semyonoskin.pet.models

import spray.json.DefaultJsonProtocol.{LongJsonFormat, StringJsonFormat, jsonFormat2}
import spray.json.RootJsonFormat

final case class Account(id: Long, balance: Long)

object Account {
  implicit val accFormat: RootJsonFormat[Account] = jsonFormat2(Account(_, _))
}
