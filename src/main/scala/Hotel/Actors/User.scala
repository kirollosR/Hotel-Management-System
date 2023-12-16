package Hotel.Actors

import Hotel.Actors.BookingActor._
import Hotel.Actors.User.LiveTheLife
import akka.actor.Actor
import Hotel.Main._
import Hotel.Models.CurrentlyReservedClass
import akka.util.Timeout

import java.time.LocalDate
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import akka.pattern.ask



object User {
  case class LiveTheLife()
}
class User extends Actor {
  implicit val timeout: Timeout = Timeout(5.seconds)

  override def receive: Receive = {
    case LiveTheLife => {
//      val futureResponse = (bookingActor ? Book(2, LocalDate.of(2023, 12, 15), LocalDate.of(2023, 12, 19))).mapTo[Boolean]
//      val result = Await.result(futureResponse, timeout.duration)
//      println(result)
      bookingActor ! Book(2, LocalDate.of(2023, 11, 20), LocalDate.of(2023, 11, 25), 1)
    }
  }
}
