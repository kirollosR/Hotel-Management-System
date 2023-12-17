package Hotel

import Hotel.Actors._
import Hotel.CRUDs.BookingCRUD.getUpcomingBooking
import Hotel.CRUDs.CurrentlyReservedCRUD.addReservation
import Hotel.CRUDs.GuestCRUD.addGuest
import Hotel.Models.{CurrentlyReservedClass, GuestClass}
import akka.actor.{ActorSystem, Props}

import java.time.LocalDate
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt



object Main extends App {



  val actorSystem = ActorSystem("Hotel-Managment-System")
  val user = actorSystem.actorOf(Props[User], "User")
  val guestActor = actorSystem.actorOf(Props[GuestActor], "GuestActor")
  val bookingActor = actorSystem.actorOf(Props[BookingActor], "BookingActor")
  val reportActor = actorSystem.actorOf(Props[ReportActor], "ReportActor")
  val billActor = actorSystem.actorOf(Props[BillActor], "BillActor")

//  val result = Await.result(getAllRoomsByCapacity(2), 2.seconds)
//  println(result)

//  def getAllRoomsByCapacity(capacity: Int): Seq[RoomClass] = {
//    Await.result(RoomCRUD.getAllRoomsByCapacity(capacity), Duration.Inf)
//  }
//  val rooms = getAllRoomsByCapacity(2)
//
//  def findAvailableRoom(rooms: Seq[RoomClass], startDate: LocalDate, endDate: LocalDate): Future[Either[Boolean, Int]] = {
//    // Create a Future[Boolean] for each room and collect them into a Seq[Future[Boolean]]
//    val roomChecks: Seq[Future[Boolean]] = rooms.map(room => isRoomReserved(room.id, startDate, endDate))
//
//    // Use Future.sequence to transform Seq[Future[Boolean]] into Future[Seq[Boolean]]
//    val allRoomChecks: Future[Seq[Boolean]] = Future.sequence(roomChecks)
//
//    // Use map to process the result when all the room checks are completed
//    allRoomChecks.map { results =>
//      // Find the index of the first false result
//      results.indexOf(false) match {
//        case index if index != -1 => Right(rooms(index).id) // Room is available, return its id
//        case _ => Left(false) // All rooms are reserved
//      }
//    }
//  }
//
//  currenltlyReservedActor ! CurrenltReservedActor.makeReservation(CurrentlyReservedClass(None, 1, LocalDate.of(2023, 12, 20), LocalDate.of(2023, 12, 25), 1))
  user ! User.LiveTheLife

//  val result = Await.result(getUpcomingBookings(), 2.seconds)
//  result.foreach {
//    case (booking, guest) =>
//      println(s"Booking ID: ${booking.id.getOrElse("N/A")}, Room ID: ${booking.roomId}, " +
//        s"Start Date: ${booking.reservationStartDate}, End Date: ${booking.reservationEndDate}, " +
//        s"Guest ID: ${booking.guestId}, " +
//        s"Guest Name: ${guest.name}, Status: ${guest.status}, " +
//        s"Email: ${guest.email}, Phone: ${guest.phone}")
//  }

//  val newGuest = GuestClass(None, "sayed", status = false, "sayeds", "065341859")
//
//// ----ADD GUEST----
//  val addGuestResult = Await.result(addGuest(newGuest), 2.seconds)
//  println(s"Creating user Result: $addGuestResult")
//  val addCurrenltlyReservedResult = Await.result(addReservation(CurrentlyReservedClass(None, 1, LocalDate.of(2023, 12, 20), LocalDate.of(2023, 12, 25), 1)), 2.seconds)
//  println(s"Creating user Result: $addCurrenltlyReservedResult")

//  val result = isRoomReserved(1, LocalDate.of(2023, 12, 20), LocalDate.of(2023, 12, 25))
////  val result = isAvailable(1)
////  println(result)
//  result.onComplete {
//    case scala.util.Success(isReserved) =>
//      println(s"Room 1 availability result: $isReserved")
//
//    case scala.util.Failure(ex) =>
//      println(s"Error checking room availability: $ex")
//  }

//  actorSystem.terminate()
}
