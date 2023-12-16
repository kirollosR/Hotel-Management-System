package Hotel.Actors

import Hotel.Actors.BookingActor._
import Hotel.Actors.User.LiveTheLife
import Hotel.CRUDs.GuestCRUD.{getAllGuests, getGuestIdByName}
import akka.actor.Actor
import Hotel.Main._
import Hotel.Models.CurrentlyReservedClass
import akka.util.Timeout

import java.time.LocalDate
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import akka.pattern.ask

import scala.io.StdIn



object User {
  case class LiveTheLife()
}
class User extends Actor {
  implicit val timeout: Timeout = Timeout(5.seconds)

//  override def receive: Receive = {
//    case LiveTheLife => {
//      val guestName = "sayed"
//      val GuestId = Await.result(getGuestIdByName(guestName), timeout.duration).get.get
//      println(GuestId)
//      bookingActor ! Book(2, LocalDate.of(2023, 11, 20), LocalDate.of(2023, 11, 25), GuestId)
////      bookingActor ! Book(2, LocalDate.of(2023, 11, 28), LocalDate.of(2023, 11, 30), GuestId)
////      bookingActor ! Cancel(31)
//    }
//  }

  override def receive: Receive = {
    case LiveTheLife => {
      var validGuestName = false

      while (!validGuestName) {
        print("Enter guest name (type 'exit' to quit): ")
        val guestName = StdIn.readLine()

        if (guestName.toLowerCase == "exit") {
          println("Exiting...")
          context.system.terminate()
        } else {
          val allGuests = Await.result(getAllGuests, timeout.duration)

          if (guestName.nonEmpty && allGuests.exists(_.name == guestName)) {
            val guestIdOption = Await.result(getGuestIdByName(guestName), timeout.duration).get
            guestIdOption.foreach { guestId =>
//              println(s"Guest ID for $guestName: $guestId")
              bookingActor ! Book(2, LocalDate.of(2023, 11, 15), LocalDate.of(2023, 11, 18), guestId)
            }
            validGuestName = true // Exit the loop when a valid name is entered
          } else {
            println("Invalid guest name. Please try again.")
          }
        }
      }
    }
  }
}
