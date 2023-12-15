package Hotel

import Hotel.Actors.User
import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.typed.ActorSystem

object Main extends App {


  val actorSystem = ActorSystem("Hotel-Managment-System")
  val user = actorSystem.actorOf(Props[User], "User")


}
