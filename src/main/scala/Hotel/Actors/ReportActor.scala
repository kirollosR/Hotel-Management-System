package Hotel.Actors

import Hotel.Actors.ReportActor.{CountAllBookings, CountAvaialableGuests, CountGuests, CountRooms, CountUpcomingBookings}
import Hotel.CRUDs.BookingCRUD.countAllBookings
import Hotel.CRUDs.CurrentlyReservedCRUD.countUpcomingReservations
import Hotel.CRUDs.GuestCRUD.{countAvailableGuests, countGuests}
import Hotel.CRUDs.RoomCRUD.countRooms
import akka.actor.Actor

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration.DurationInt

object ReportActor {
  case class CountRooms()
  case class CountAvaialableGuests()
  case class CountGuests()
  case class CountUpcomingBookings()
  case class CountAllBookings()

}

class ReportActor extends Actor{



  override def receive: Receive = {
    case CountRooms() => {
      val result = Await.result(countRooms(), 5.seconds)
      sender() ! result
    }

    case CountAvaialableGuests() => {
      val result = Await.result(countAvailableGuests(), 5.seconds)
      sender() ! result
    }

    case CountGuests() => {
      val result = Await.result(countGuests(), 5.seconds)
      sender() ! result
    }

    case CountUpcomingBookings() => {
      val result = Await.result(countUpcomingReservations(), 5.seconds)
      sender() ! result
    }

    case CountAllBookings() => {
      val result = Await.result(countAllBookings(), 5.seconds)
      sender() ! result
    }
  }

}
