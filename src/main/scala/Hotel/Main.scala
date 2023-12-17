package Hotel
import Hotel.Actors._
import akka.actor.{ActorSystem, Props}

object Main extends App {

  val actorSystem = ActorSystem("Hotel-Managment-System")
  val user = actorSystem.actorOf(Props[User], "User")
  val guestActor = actorSystem.actorOf(Props[GuestActor], "GuestActor")
  val bookingActor = actorSystem.actorOf(Props[BookingActor], "BookingActor")
  val reportActor = actorSystem.actorOf(Props[ReportActor], "ReportActor")
  val billActor = actorSystem.actorOf(Props[BillActor], "BillActor")

  user ! User.LiveTheLife
}
