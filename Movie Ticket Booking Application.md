# Movie Ticket Booking Application

## Requirements:

1. A Theatre has Screens that run Shows for different Movies. Each Show has a particular Movie, start time, duration, and is played in a particular Screen in the theatre. Each Screen has an arrangement of Seats that can be booked by Users.
2. Assume all Users are registered, authenticated, and logged in to the Application.
3. Once a User selects a particular show to book tickets for, a UserBookingSession starts. Within this UserBookingSession, a User will be able to get the Available Seats for the show and select the Seats he wishes to book. It is a ‘good to have’ for the Application to have limits on the number of seats a User can book in a Ticket.
4. Once the user has selected a group of seats, these seats should become TEMPORARILY_UNAVAILABLE to all other Users.
5. The User then proceeds to make payment which can either be SUCCESS or FAILURE.
6. If Payment FAILED, user can retry Payment for a maximum number of times. Beyond maximum retries, the seats are made AVAILABLE.
7. If Payment SUCCEEDS, Ticket or Booking Confirmation is generated and made available to the User. The UserBookingSession is closed and the Seats are made PERMANENTLY_UNAVAILABLE.
8. A User can also explicitly close the UserBookingSession after selecting seats and before making payment. In this case, the seats selected are made AVAILABLE once again.

### **Problems:**

Demonstrate the following scenarios:2 concurrent Users U1, U2 in the application. The Users can retrieve Available Shows and select one show.

### **Case 1:**

1. Say U1 and U2 select same show.
2. U1 requests for and gets all Available Seats for this show.
3. U1 selects group of seats and proceeds to pay.
4. U2 requests for and gets all Available Seats for this show. U2 should not see the seats selected by U1 as AVAILABLE. 5 .Payment succeeded for U1.
5. U1 receives Ticket with Seats confirmed.

### **Case 2:**

1. Say U1 and U2 select same show.
2. U1 and U2 requests for and gets all Available Seats for this show.
3. U1 selects group of seats.
4. U1 proceeds to pay.
5. U2 requests for and gets all Available Seats for this show. U2 should NOT see the seats selected by U1 as AVAILABLE.
6. Payment failed for U1. Assume maximum retries as zero just for the demo. Also show in another scenario where U1’s UserBookingSession is explicitly closed by U1 before payment is completed.
7. U2 again requests for and gets all Available Seats for this show. U2 should now see the seats previously selected by U1 as AVAILABLE.

### **Case 3:**

1. Say U1 and U2 select same show.
2. U1 and U2 request for and get all Available Seats for this show.
3. U1 selects group of seats and proceeds to pay.
4. U2 selects overlapping group of seats and proceeds to pay. U2 should be notified that “one or more of the selected seats are not available at this moment”.

### **Bonus:**

Have a configurable timeout for a UserBookingSession. Show that if User selects and Payment is not completed by timeout, then the UserBookingSession is closed and the seats selected are made AVAILABLE.

### **Expectations**

- Create the sample data yourself. You can put it into a file, test case or main driver program itself.
- Code should be demo-able. Either by using a main driver program or test cases.
- Code should be modular. Code should have basic OO design. Please do not jam in responsibilities of one class into another.
- Code should be extensible. Wherever applicable, use interfaces and contracts between different methods. It should be easy to add/remove functionality without re­writing entire codebase.
- Code should handle edge cases properly and fail gracefully.
- Code should be legible, readable and DRY

### **Guidelines**

- Use of DB not expected. You can store data in memory.
- Please discuss the solution with an interviewer
- Please do not access internet for anything EXCEPT syntax
- You are free to use the language of your choice
- All code should be your own
- Please focus on the Bonus questions only after ensuring the required features are complete and demoable.

## Code:

### Exceptions

```java
class AlreadyExistsException extends RuntimeException {
}

class BadRequestException extends RuntimeException {
}

class InvalidStateException extends RuntimeException {
}

class NotFoundException extends RuntimeException {
}

class ScreenAlreadyOccupiedException extends RuntimeException {
}

class SeatPermanentlyUnavailableException extends RuntimeException {
}

class SeatTemporaryUnavailableException extends RuntimeException {
}
```

### Model Classes : Movie, Seat, Screen, Show, Theatre, Booking and SeatLock

```java
@AllArgsConstructor
@Getter
class Movie {
    private final String movieID;
    private final String movieName;
    //Other Metadata depending on requirement asked in an interview
}

@AllArgsConstructor
@Getter
class Seat {

    private final String seatID;
    // In a Screen, seats are placed in a 2*2 matrix format, hence row & col(seatNo)
    private final int rowNo;
    private final int seatNo;
}

@Getter
class Screen {
    private final String screenID;
    private final String screenName;
    private final Theatre theatre; // A Theatre will have multiple Screen but a Screen will be part of single Theatre
    //Other Screen metadata depending on requirement asked in an interview

    private final List<Seat> seats;

    public Screen(final String id, final String name, final Theatre theatre) {
        this.screenID = id;
        this.screenName = name;
        this.theatre = theatre;
        this.seats = new ArrayList<>();
    }

    public void addSeat(final Seat seat) {
        this.seats.add(seat);
    }
}

@Getter
class Theatre {
    private final String theatreID;
    private final String theatreName;
    private final List<Screen> screens;// A Theatre will have multiple Screens like AUDI1, AUDI2, AUDI3 etc
    //Other theatre Metadata depending on requirement asked in an interview

    public Theatre( final String id, final String name) {
        this.theatreID = id;
        this.theatreName = name;
        this.screens = new ArrayList<>();
    }

    public void addScreen( final  Screen screen) {
        screens.add(screen);
    }
}

// Show runs for a movie in a single screen
@AllArgsConstructor
@Getter
class Show {
    private final String showID;
    private final Movie movie;
    private final Screen screen;
    private final Date startTime;
    private final Integer durationInSeconds;
}

enum BookingStatus {
    Created, Confirmed, Expired
}

@Getter
class Booking {
    private final String bookingID;
    private final Show show;
    private final List<Seat> seatsBooked;
    private final String user;
    private BookingStatus bookingStatus;

    // New booking can only be created through this constructor (When new object are made)
    // Their is no explicit method to create a booking.
    public Booking(final String id, final Show show, final String user,
                   final List<Seat> seatsBooked) {
        this.bookingID = id;
        this.show = show;
        this.seatsBooked = seatsBooked;
        this.user = user;
        this.bookingStatus = BookingStatus.Created;
    }

    public boolean isConfirmed() {
        return this.bookingStatus == BookingStatus.Confirmed;
    }

    public void confirmBooking() {
        if (this.bookingStatus != BookingStatus.Created) {
            throw new InvalidStateException();
        }
        this.bookingStatus = BookingStatus.Confirmed;
    }

    public void expireBooking() {
        if (this.bookingStatus != BookingStatus.Created) {
            throw new InvalidStateException();
        }
        this.bookingStatus = BookingStatus.Expired;
    }
}

@AllArgsConstructor
@Getter
class SeatLock {
    private Seat seat;
    private Show show;
    private Integer timeoutInSeconds; // Bonus requirement: Unlock the seat after timeoutInSeconds passed
    private Date lockTime;
    private String lockedBy;

    public boolean isLockExpired() {
        // Java Instant class: https://www.dariawan.com/tutorials/java/java-instant-tutorial-examples/

        final Instant lockInstant = lockTime.toInstant().plusSeconds(timeoutInSeconds);
        final Instant currentInstant = new Date().toInstant();
        return lockInstant.isBefore(currentInstant);
    }
}
```

### Class InMemorySeatLockProvider

```java
class InMemorySeatLockProvider {
    private final Integer lockTimeout; // Bonus feature.
    // Below Map contains only the data about the seats that are currently in lock state.
    private final Map<Show, Map<Seat, SeatLock>> locks;

    public InMemorySeatLockProvider(final Integer lockTimeout) {
        this.locks = new HashMap<>();
        this.lockTimeout = lockTimeout;
    }

    /*
    Check about synchronized keyword in JAVA
    1. https://youtu.be/RH7G-N2pa8M
    2. https://youtu.be/IIgHG_YHXPE
    3. https://www.javatpoint.com/synchronization-in-java
    4. https://www.geeksforgeeks.org/synchronized-in-java/
    */
    synchronized public void lockSeats(final Show show, final List<Seat> seats, final String user) {

        // For list of all the seats selected by the user, we check availability of every seat
        for (Seat seat : seats) {
            if (isSeatLocked(show, seat)) {
                throw new SeatTemporaryUnavailableException();
            }
        }

        // We lock all the seats one by one.
        for (Seat seat : seats) {
            lockSeat(show, seat, user, lockTimeout);
        }
    }

    private boolean isSeatLocked(final Show show, final Seat seat) {
        /*
        1. We first check that the show is valid (Show exits)
        2. Then we check the selected show has seats.
        3. In last we check the seat selected by current user.
        We check is the lock time (lock window) of blocking a seat is stilling TRUE or not.
        */
        return locks.containsKey(show) &&
                locks.get(show).containsKey(seat) &&
                !locks.get(show).get(seat).isLockExpired();
    }

    private void lockSeat(final Show show, final Seat seat, final String user, final Integer timeoutInSeconds) {
        // If non of the seat was blocked for show selected by user, we first need to add that show
        if (!locks.containsKey(show)) {
            locks.put(show, new HashMap<>());
        }

        final SeatLock lock = new SeatLock(seat, show, timeoutInSeconds, new Date(), user);
        locks.get(show).put(seat, lock);
    }

    public void unlockSeats(final Show show, final List<Seat> seats, final String user) {
        for (Seat seat: seats) {
            if (validateLock(show, seat, user)) {
                unlockSeat(show, seat);
            }
        }
    }

    public boolean validateLock(final Show show, final Seat seat, final String user) {
        return isSeatLocked(show, seat) &&
                locks.get(show).get(seat).getLockedBy().equals(user);
    }

    private void unlockSeat(final Show show, final Seat seat) {
        if (!locks.containsKey(show)) {
            return;
        }
        locks.get(show).remove(seat);
    }

    public List<Seat> getLockedSeats(final Show show) {
        if (!locks.containsKey(show)) {
            return ImmutableList.of();
        }
        final List<Seat> lockedSeats = new ArrayList<>();

        for (Seat seat : locks.get(show).keySet()) {
            if (isSeatLocked(show, seat)) {
                lockedSeats.add(seat);
            }
        }
        return lockedSeats;
    }
}
```

### Services: MovieService, ShowService and TheatreService

```java
// Service to keep the data regarding movie collection and added functionality to GET, PUT and manipulate the collection
class MovieService {
    private final Map<String, Movie> movies;

    public MovieService() {
        this.movies = new HashMap<>();
    }

    public Movie getMovie(final String movieId) {
        if (!movies.containsKey(movieId)) {
            throw new NotFoundException();
        }
        return movies.get(movieId);
    }

    public Movie createMovie(final String movieName) {
        String movieId = UUID.randomUUID().toString();
        Movie movie = new Movie(movieId, movieName);
        movies.put(movieId, movie);
        return movie;
    }
}

// Service to keep the data regarding Show collection and added functionality to GET, PUT and manipulate the collection
class ShowService {
    private final Map<String, Show> shows;

    public ShowService() {
        this.shows = new HashMap<>();
    }

    public Show getShow(final String showId) {
        if (!shows.containsKey(showId)) {
            throw new NotFoundException();
        }
        return shows.get(showId);
    }

    public Show createShow(final Movie movie, final Screen screen, final Date startTime,
                           final Integer durationInSeconds) {
        if (!checkIfShowCreationAllowed(screen, startTime, durationInSeconds)) {
            throw new ScreenAlreadyOccupiedException();
        }
        String showId = UUID.randomUUID().toString();
        final Show show = new Show(showId, movie, screen, startTime, durationInSeconds);
        this.shows.put(showId, show);
        return show;
    }

    private List<Show> getShowsForScreen(final Screen screen) {
        final List<Show> response = new ArrayList<>();
        for (Show show : shows.values()) {
            if (show.getScreen().equals(screen)) {
                response.add(show);
            }
        }
        return response;
    }

    private boolean checkIfShowCreationAllowed(final Screen screen, final Date startTime, final Integer durationInSeconds) {
        // TODO: Implement this. This method will return whether the screen is free at a particular time for
        // specific duration. This function will be helpful in checking whether the show can be scheduled in that slot
        // or not.
        return true;
    }
}

// Service to keep the data regarding Theatre collection and added functionality to GET, PUT and manipulate the collection
class TheatreService {
    private final Map<String, Theatre> theatres;
    private final Map<String, Screen> screens;
    private final Map<String, Seat> seats;

    public TheatreService() {
        this.theatres = new HashMap<>();
        this.screens = new HashMap<>();
        this.seats = new HashMap<>();
    }

    public Seat getSeat(final String seatId) {
        if (!seats.containsKey(seatId)) {
            throw new NotFoundException();
        }
        return seats.get(seatId);
    }

    public Theatre getTheatre(final String theatreId) {
        if (!theatres.containsKey(theatreId)) {
            throw new NotFoundException();
        }
        return theatres.get(theatreId);
    }

    public Screen getScreen(final String screenId) {
        if (!screens.containsKey(screenId)) {
            throw new NotFoundException();
        }
        return screens.get(screenId);
    }

    public Theatre createTheatre(final String theatreName) {
        String theatreId = UUID.randomUUID().toString();
        Theatre theatre = new Theatre(theatreId, theatreName);
        theatres.put(theatreId, theatre);
        return theatre;
    }

    public Screen createScreenInTheatre(final String screenName, final Theatre theatre) {
        Screen screen = createScreen(screenName, theatre);
        theatre.addScreen(screen);
        return screen;
    }

    public Seat createSeatInScreen(final Integer rowNo, final Integer seatNo, final Screen screen) {
        String seatId = UUID.randomUUID().toString();
        Seat seat = new Seat(seatId, rowNo, seatNo);
        seats.put(seatId, seat);
        screen.addSeat(seat);

        return seat;
    }

    private Screen createScreen(final String screenName, final Theatre theatre) {
        String screenId = UUID.randomUUID().toString();
        Screen screen = new Screen(screenId, screenName, theatre);
        screens.put(screenId, screen);
        return screen;
    }
}
```

### Service: BookingService and SeatAvailabilityService

```java
class BookingService {
    private final Map<String, Booking> showBookings;
    private final InMemorySeatLockProvider inMemorySeatLockProvider;

    public BookingService(InMemorySeatLockProvider inMemorySeatLockProvider) {
        this.inMemorySeatLockProvider = inMemorySeatLockProvider;
        this.showBookings = new HashMap<>();
    }

    public Booking getBooking(final String bookingId) {
        if (!showBookings.containsKey(bookingId)) {
            throw new NotFoundException();
        }
        return showBookings.get(bookingId);
    }

    public List<Booking> getAllBookings(final Show show) {
        List<Booking> response = new ArrayList<>();
        for (Booking booking : showBookings.values()) {
            if (booking.getShow().equals(show)) {
                response.add(booking);
            }
        }

        return response;
    }

    public Booking createBooking(final String userId, final Show show,
                                 final List<Seat> seats) {
        if (isAnySeatAlreadyBooked(show, seats)) {
            throw new SeatPermanentlyUnavailableException();
        }
        inMemorySeatLockProvider.lockSeats(show, seats, userId);
        final String bookingId = UUID.randomUUID().toString();
        final Booking newBooking = new Booking(bookingId, show, userId, seats);
        showBookings.put(bookingId, newBooking);
        return newBooking;
        // TODO: Create timer for booking expiry
    }

    public List<Seat> getBookedSeats(final Show show) {
        return getAllBookings(show).stream()
                .filter(Booking::isConfirmed)
                .map(Booking::getSeatsBooked)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public void confirmBooking(final Booking booking, final String user) {
        if (!booking.getUser().equals(user)) {
            throw new BadRequestException();
        }

        for (Seat seat : booking.getSeatsBooked()) {
            if (!inMemorySeatLockProvider.validateLock(booking.getShow(), seat, user)) {
                throw new BadRequestException();
            }
        }
        booking.confirmBooking();
    }

    private boolean isAnySeatAlreadyBooked(final Show show, final List<Seat> seats) {
        final List<Seat> bookedSeats = getBookedSeats(show);
        for (Seat seat : seats) {
            if (bookedSeats.contains(seat)) {
                return true;
            }
        }
        return false;
    }
}

class SeatAvailabilityService {
    private final BookingService bookingService;
    private final InMemorySeatLockProvider inMemorySeatLockProvider;

    public SeatAvailabilityService(final BookingService bookingService,
                                   final InMemorySeatLockProvider inMemorySeatLockProvider) {
        this.bookingService = bookingService;
        this.inMemorySeatLockProvider = inMemorySeatLockProvider;
    }

    public List<Seat> getAvailableSeats(final Show show) {
        final List<Seat> allSeats = show.getScreen().getSeats();
        final List<Seat> unavailableSeats = getUnavailableSeats(show);

        final List<Seat> availableSeats = new ArrayList<>(allSeats);
        availableSeats.removeAll(unavailableSeats);
        return availableSeats;
    }

    private List<Seat> getUnavailableSeats(final Show show) {
        final List<Seat> unavailableSeats = bookingService.getBookedSeats(show);
        unavailableSeats.addAll(inMemorySeatLockProvider.getLockedSeats(show));
        return unavailableSeats;
    }
}
```

### Service : PaymentsService

```java
class PaymentsService {
    Map<Booking, Integer> bookingFailures;
    private final Integer allowedRetries; //Seats will be unlocked after retries fails
    private final InMemorySeatLockProvider inMemorySeatLockProvider;

    public PaymentsService(final Integer allowedRetries, InMemorySeatLockProvider inMemorySeatLockProvider) {
        this.allowedRetries = allowedRetries;
        this.inMemorySeatLockProvider = inMemorySeatLockProvider;
        bookingFailures = new HashMap<>();
    }

    public void processPaymentFailed(final Booking booking, final String user) {
        if (!booking.getUser().equals(user)) {
            throw new BadRequestException();
        }
        if (!bookingFailures.containsKey(booking)) {
            bookingFailures.put(booking, 0);
        }
        final Integer currentFailuresCount = bookingFailures.get(booking);
        final Integer newFailuresCount = currentFailuresCount + 1;
        bookingFailures.put(booking, newFailuresCount);
        // In case of more number of payment failure (compared to retry number), then we unlock the blocked seats of current booking
        if (newFailuresCount > allowedRetries) {
            inMemorySeatLockProvider.unlockSeats(booking.getShow(), booking.getSeatsBooked(), booking.getUser());
        }
    }
}
```

### Controllers: MovieController, ShowController, TheatreController, BookingController and PaymentsController

```java
@AllArgsConstructor
class MovieController {
    final private MovieService movieService;

    public String createMovie(final String movieName) {
        return movieService.createMovie(movieName).getMovieID();
    }
}

@AllArgsConstructor
class ShowController {
    private final SeatAvailabilityService seatAvailabilityService;
    private final ShowService showService;
    private final TheatreService theatreService;
    private final MovieService movieService;

    public String createShow(final String movieId, final String screenId, final Date startTime,
                             final Integer durationInSeconds) {
        final Screen screen = theatreService.getScreen(screenId);
        final Movie movie = movieService.getMovie(movieId);
        return showService.createShow(movie, screen, startTime, durationInSeconds).getShowID();
    }

    public List<String> getAvailableSeats(final String showId) {
        final Show show = showService.getShow(showId);
        final List<Seat> availableSeats = seatAvailabilityService.getAvailableSeats(show);
        return availableSeats.stream().map(Seat::getSeatID).collect(Collectors.toList());
    }
}

@AllArgsConstructor
class TheatreController {
    final private TheatreService theatreService;

    public String createTheatre(final String theatreName) {
        return theatreService.createTheatre(theatreName).getTheatreID();
    }

    public String createScreenInTheatre(final String screenName, final String theatreId) {
        final Theatre theatre = theatreService.getTheatre(theatreId);
        return theatreService.createScreenInTheatre(screenName, theatre).getScreenID();
    }

    public String createSeatInScreen(final Integer rowNo, final Integer seatNo, final String screenId) {
        final Screen screen = theatreService.getScreen(screenId);
        return theatreService.createSeatInScreen(rowNo, seatNo, screen).getSeatID();
    }
}

@AllArgsConstructor
class BookingController {
    private final ShowService showService;
    private final BookingService bookingService;
    private final TheatreService theatreService;

    public String createBooking(final String userId, final String showId,
                                final List<String> seatsIds) {
        final Show show = showService.getShow(showId);
        final List<Seat> seats = seatsIds.stream().map(theatreService::getSeat).collect(Collectors.toList());
        return bookingService.createBooking(userId, show, seats).getBookingID();
    }
}

class PaymentsController {
    private final PaymentsService paymentsService;
    private final BookingService bookingService;

    public PaymentsController(PaymentsService paymentsService, BookingService bookingService) {
        this.paymentsService = paymentsService;
        this.bookingService = bookingService;
    }

    public void paymentFailed(final String bookingId, final String user) {
        paymentsService.processPaymentFailed(bookingService.getBooking(bookingId), user);
    }

    public void paymentSuccess(final  String bookingId, final String user) {
        bookingService.confirmBooking(bookingService.getBooking(bookingId), user);
    }
}
```

## Reference:

1. [Movie Ticket Booking Application LLD - Udit](https://www.youtube.com/playlist?list=PL564gOx0bCLpAL7yMJqOuK3_hBuLkyRhn)
2. [GitHub : Ticket Booking Application Like BookMyShow, TicketMaster, etc - Udit](https://github.com/anomaly2104/ticket-booking-low-level-system-design)
3. [Book My Show || Low Level Design Code](https://youtu.be/7LaKmNfMCAo?list=PL12BCqE-Lp650Cg6FZW7SoZwN8Rw1WJI7)
4. [Design a Movie Ticket Booking System - grokking](https://raw.githubusercontent.com/himanshukr-nsit/Object-Oriented-Design-Pattern-Interview/master/2.%20Object%20Oriented%20Design%20Case%20Studies/5.%20Design%20a%20Movie%20Ticket%20Booking%20System/1.1Design%20a%20Movie%20Ticket%20Booking%20System%20-%20Grokking%20the%20Object%20Oriented%20Design%20Interview.html)
