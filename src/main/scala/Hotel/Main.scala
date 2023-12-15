package Hotel

import Hotel.Actors.User
import akka.actor.{ActorSystem, Props}

object Main extends App {


  val actorSystem = ActorSystem("Hotel-Managment-System")
  val user = actorSystem.actorOf(Props[User], "User")


}
