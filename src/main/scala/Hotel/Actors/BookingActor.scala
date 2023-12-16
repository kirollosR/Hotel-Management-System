package Hotel.Actors
import Hotel.Actors.BookingActor.Book
import Hotel.Actors.CurrenltReservedActor.makeReservation
import Hotel.CRUDs.CurrentlyReservedCRUD.{addReservation, findAvailableRoom, isRoomReserved}
import Hotel.CRUDs.RoomCRUD
import Hotel.PrivateExecutor._
import Hotel.Models.Room.RoomTable
import Hotel.Models.{BookingClass, CurrentlyReservedClass, RoomClass}
import Hotel.connection.db
import akka.actor.{Actor, ActorRef, Props}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, DurationInt}
import slick.jdbc.MySQLProfile.api._
import Hotel.Main._
import akka.util.Timeout
import akka.pattern.ask

import java.time.LocalDate
import scala.util.{Failure, Success}

object BookingActor{
  case class Book(roomCapacity: Int, startDate: LocalDate, endDate: LocalDate, guestId: Int)

}


class BookingActor extends Actor {
  implicit val timeout: Timeout = Timeout(5.seconds)

  // Fetch rooms from the database and create RoomActors for each
  def getAllRoomActors: Map[Int, ActorRef] = {
    val roomsFuture: Future[Seq[RoomClass]] = db.run(RoomTable.result)

    val roomActorsFuture: Future[Map[Int, ActorRef]] = roomsFuture.map { rooms =>
      rooms.map { room =>
        room.id -> context.actorOf(Props[RoomActor], s"room${room.id}")
      }.toMap
    }

    Await.result(roomActorsFuture, Duration.Inf) // Blocking operation; consider handling asynchronously
  }

  def receive: Receive = {
    case Book(roomCapacity: Int, startDate: LocalDate, endDate: LocalDate, guestId: Int) => {
      val rooms = getAllRoomsByCapacity(roomCapacity)
      val result: Int = Await.result(Future(roomAvailable(roomCapacity, startDate, endDate)), Duration.Inf)
      println(result)
      if (result != -1) {
        println(s"Room $result is available")
        val currentlyReservedClass = CurrentlyReservedClass(None, result, startDate, endDate, guestId)

        val futureResponse = Await.result(Future(makeReservation(currentlyReservedClass)), timeout.duration)
//        val response = Await.result(futureResponse, timeout.duration)
        println(futureResponse)
//        val roomActor = context.actorOf(Props[RoomActor], s"room$result")
//        roomActor ! RoomActor.Book(startDate, endDate)
      } else {
        println("No Rooms available at this period of time")
      }
//      (currenltReservedActor ? CurrenltReservedActor.findAvailableRoom(rooms, startDate, endDate))
//        .mapTo[Either[Boolean, Int]]
//        .onComplete {
//          case Success(result) =>
//            result match {
//              case Left(true) => println("No Rooms available at this period of time")
//              case Right(roomId) => println(s"Room $roomId is available")
//              case _ => println("Unexpected result")
//            }
//
//          case Failure(exception) => println(s"Error: ${exception.getMessage}")
//        }


      //      }



    }
  }

//  private def roomAvailable(roomCapacity: Int, startDate: LocalDate, endDate: LocalDate): Any = {
//    val rooms = getAllRoomsByCapacity(roomCapacity)
//    println(rooms)
//
//    // Loop through the RoomClass instances
//    rooms.foreach { room =>
//      // Access the id and do something with it
//
//      val futurResponse = (currenltReservedActor ? CurrenltReservedActor.isReserved(room.id, startDate, endDate)).mapTo[Boolean]
//      val isReserved = Await.result(futurResponse, timeout.duration)
//      println(isReserved)
//      if (!isReserved) {
////        sender() ! room.id
//        println(s"Room ${room.id} is available")
//      } else {
//        println("No Rooms available at this period of time")
//      }
//    }
//  }

  private def roomAvailable(roomCapacity: Int, startDate: LocalDate, endDate: LocalDate): Int = {
    val rooms = getAllRoomsByCapacity(roomCapacity)
    println(rooms)
//    val futurResponse = (currenltReservedActor ? CurrenltReservedActor.findAvailableRoom(rooms, startDate, endDate)).mapTo[Any]
//    val isReserved = Await.result(futurResponse, timeout.duration)
    val resultFuture: Future[Either[Boolean, Int]] = findAvailableRoom(rooms, startDate, endDate)

    val result: Int = Await.result(resultFuture, Duration.Inf) match {
      case Left(false) => -1
      case Right(roomId) => roomId
      case _ => 0
    }

    result
  }


  private def getAllRoomsByCapacity(capacity: Int): Seq[RoomClass] = {
    Await.result(RoomCRUD.getAllRoomsByCapacity(capacity), Duration.Inf)
  }

  private def makeReservation(currentlyReserved: CurrentlyReservedClass) = {
    val resultFuture = addReservation(currentlyReserved)

    val result = try {
      Await.result(resultFuture, 2.seconds) // Adjust the timeout duration as needed
    } catch {
      case e: Throwable =>
        println(s"Error waiting for result: $e")
    }
    result
  }

      // Forward the booking request to the corresponding RoomActor
//      getAllRoomActors.get(request.roomId).foreach(_ forward request)
}




