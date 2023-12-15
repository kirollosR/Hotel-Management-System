package Hotel

import Hotel.Actors.User
import akka.actor.{ActorSystem, Props}
import Hotel.CRUDs.GuestDao
import Hotel.CRUDs.GuestDao._
import Hotel.CRUDs.PrivateExecutor.ec
import Hotel.Models.GuestClass
import akka.actor.{ActorSystem, Props}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object Main extends App {


  val actorSystem = ActorSystem("Hotel-Managment-System")
  val user = actorSystem.actorOf(Props[User], "User")

  val newGuest = GuestClass(None, "sayed", status = false, "sayed", "06541859")

// ----ADD GUEST----
  val addGuestResult = Await.result(addGuest(newGuest), 2.seconds)
  println(s"Creating user Result: $addGuestResult")

}
