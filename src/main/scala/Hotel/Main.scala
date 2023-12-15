package Hotel

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.typed.ActorSystem

object Main extends App {
  class User extends Actor{
    override def receive: Receive = {
      ???
    }
  }

  val actorSystem = ActorSystem("Hotel-Managment-System")
  val user = actorSystem.actorOf(Props[User], "User")


}
