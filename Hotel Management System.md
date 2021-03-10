# Hotel Management System

## System Requirements:

1. Guests should be able to search the room inventory and book any available room.
2. The system should support the booking of different room types like standard, deluxe, family suite, etc.
3. The system should be able to retrieve information, such as who booked a particular room, or what rooms were booked by a specific customer.
4. The system should allow customers to cancel their booking - and provide them with a full refund if the cancelation occurs before 24 hours of the check-in date.
5. The system should be able to send notifications whenever the booking is nearing the check-in or check-out date.
6. The system should maintain a room housekeeping log to keep track of all housekeeping tasks.
7. Any customer should be able to add room services and food items.
8. Customers can ask for different amenities.
9. The customers should be able to pay their bills through credit card, check or cash.

## Actors:

1. Guest
2. Receptionist 
3. Admin
4. Housekeeping 
5. System or Server

## Use-case:

1.  Add, Remove or Edit room
2. Search Room
3. Book Room
4. Cancel Booking
5. Check-In
6. Check-Out
7. Add Room Charge
8. Update Housekeeping Logs

## Relationship b/w Actors & Use-Case:

### Receptionist:

1. Search Room
2. Book Room
3. Check-In
4. Check-Out
5. Cancel Booking

### Guest:

1. Search Room
2. Book Room
3. Cancel Booking

### System or Server:

1. Send Notification
2. Bill Generation

### Housekeeping :

1. Add and update housekeeping status

### Admin:

1. Add Rooms
2. Remove Rooms
3. Edit Rooms(Type)

## Use case diagram
![](/Images/HotelManegementLLD.png)

## Code

**Classes related to Hotel**

```java
class Hotel {
    String Name;
    Integer id; //Can use Java UUID
    Address address;
    List<Room> roomList;
}

class Address {
    Location location;
    int pinCode;
    String street;
    String area;
    String city;
    String country;
}

// Always better to have Location class in form of longitude & latitude as that will help in using MAPS
class Location {
    Double longitude;
    Double latitude;
}
```

**Classes related to Rooms**

```java
enum RoomStyle {
    STANDARD, DELUX, FAMILY_SUITE;
}

enum RoomStatus {
    AVAILABLE, RESERVED, NOT_AVAILBLE, OCCUPIED, SERVICE_IN_PROGRESS;
}

class Room {
    String roomNumber;
    RoomStyle roomStyle;
    RoomStatus roomStatus;
    Double bookingPrice;
    List<RoomKey> roomKeys;
    List<HouseKeepingLog> houseKeepingLogs;
}

class RoomKey {
    String keyId;
    String barCode;
    Date issuedAt;
    Boolean isActive;
    Boolean isMaster;

    /*
    Idea to have below method: When ever we initialize a RoomKey object we also assign that RoomKey
    to a room
    */
    public void assignRoom(Room room) {
        // ...
    }

}
```

**Class HouseKeepingLog (As we have a requirement to keep the logs for housekeeping activities)**

```java
class HouseKeepingLog {
    String description;
    Date startDate;
    int duration;
    HouseKeeper housKeeper;

    /*
    Idea to have below method: Adding HouseKeepingLog object to the room
    */
    public void addRoom(Room room) {
        // ...
    }
}
```

**Classes related to different type Accounts(Actors)**

```java
enum AccountStatus {
    ACTIVE, CLOSED, BLOCKED;
}

public class Account {
    String username;
    String password;

    AccountStatus accountStatus;

}

abstract class Person {
    String name;
    Account accountDetail;
    String phone;
}

class HouseKeeper extends Person {
    // Get all the room serviced by current HouseKeeper in this range
    public List<Room> public getRoomsServiced(Date startDate, Date endDate) {
        // ...
    };
}

/*
Guest can search for different kind of rooms an do the booking for the same
For a guest we also need to keep the data about booking done by them
*/
class Guest extends Person {
    Search searchObj;
    RoomBookingService roomBookingService;

    public List<RoomBooking> getAllRoomBookings() {
        // ...
    };
}

/*
Receptionist can search for different kind of rooms an do the booking for the same
They also make Guest do check-in and check-out
*/
class Receptionist extends Person {
    Search searchObj;
    RoomBookingService roomBookingService;

    public void checkInGuest(Guest guest, RoomBooking bookingInfo) {
        // ...
    };
    public void checkOutGuest(Guest guest, RoomBooking bookingInfo) {
        // ...
    };
}

class Admin extends Person {
    public void addRoom(Room roomDetail) {
        // ...
    };
    public Room deleteRoom(String roomId) {
        // ...
    };
    public void editRoom(Room roomDetail) {
        // ...
    };
}
```

**Search, Payments and RoomBookingService**

```java
class Search {
    public List<Room> searchRoom(RoomStyle roomStyle, Date startDate, int duration) {
        // ...
    };
}

enum BookingStatus {
    REQUESTED, PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
}

enum PaymentStatus {
    UNPAID, PENDING, COMPLETE, DECLINED, CANCELLED, REFUNDED
}

class RoomBooking {
    String bookingId;
    Date startDate;
    Integer durationInDays;
    BookingStatus bookingStatus;
    List<Guest> guestList;
    List<Room> roomInfo;
    BaseRoomCharge totalRoomCharges;
    PaymentStatus paymentStatus;
}

class RoomBookingService {
    public RoomBooking createBooking(Guest guestInfo) {
        // ...
    };
    public RoomBooking cancelBooking(int bookingId) {
        // ...
    };
}
```

**Calculate Charge for Hotel: Below code used Decorator design pattern, but in interviews we can go with normal RoomCharge itself.**

```java
/**
 *	Decorator pattern is used to decorate the prices here.
 **/

interface BaseRoomCharge {
    Double getCost();
    void setCost(double v);
}

class RoomCharge implements BaseRoomCharge {
    double cost;
    Double getCost() {
        return cost;
    }
    void setCost(double cost) {
        this.cost = cost;
    }
}

class RoomServiceCharge implements BaseRoomCharge {
    double cost;
    BaseRoomCharge baseRoomCharge;
    Double getCost() {
        baseRoomCharge.setCost(baseRoomCharge.getCost() + cost);
        return baseRoomCharge.getCost();
    }
}

class InRoomPurchaseCharges implements BaseRoomCharge {
    double cost;
    BaseRoomCharge baseRoomCharge;
    Double getCost() {
        baseRoomCharge.setCost(baseRoomCharge.getCost() + cost);
        return baseRoomCharge.getCost();
    }
}
```

# Reference:

1. [Low Level Design - Hotel Management System | Part 1](https://youtu.be/5VWycK8KmW0?list=PL12BCqE-Lp650Cg6FZW7SoZwN8Rw1WJI7)
2. [Low Level Design - Hotel Management System | Part 2](https://youtu.be/Hb6WePtPQhg?list=PL12BCqE-Lp650Cg6FZW7SoZwN8Rw1WJI7)
3. [Design a Hotel Management System - grokking](https://raw.githubusercontent.com/himanshukr-nsit/Object-Oriented-Design-Pattern-Interview/master/2.%20Object%20Oriented%20Design%20Case%20Studies/9.%20Design%20a%20Hotel%20Management%20System/1.1Design%20a%20Hotel%20Management%20System%20-%20Grokking%20the%20Object%20Oriented%20Design%20Interview.html)
