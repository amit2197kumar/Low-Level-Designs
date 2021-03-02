import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.*;

import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedDeque;

/************************************************** Vehicle ***********************************************************/

enum VehicleType {
    CAR, TRUCK, ELECTRIC, VAN, MOTORBIKE
}

@Getter
@Setter
abstract class Vehicle {
    private String licenseNumber; // Having this will help in printing this detail in parking Ticket
    private final VehicleType type;
    private ParkingTicket ticket;

    public Vehicle(String licenseNumber, VehicleType type) {
        this.licenseNumber = licenseNumber;
        this.type = type;
    }
}

class Car extends Vehicle {
    public Car(String licenseNumber) {
        super(licenseNumber, VehicleType.CAR);
    }
}

class Truck extends Vehicle {
    public Truck(String licenseNumber) {
        super(licenseNumber, VehicleType.TRUCK);
    }
}

class Van extends Vehicle {
    public Van(String licenseNumber) {
        super(licenseNumber, VehicleType.VAN);
    }
}

class Electric extends Vehicle {
    public Electric(String licenseNumber) {
        super(licenseNumber, VehicleType.ELECTRIC);
    }
}

class Moterbike extends Vehicle {
    public Moterbike(String licenseNumber) {
        super(licenseNumber, VehicleType.MOTORBIKE);
    }
}

/************************************************** Parking ***********************************************************/

enum ParkingSpotType {
    HANDICAPPED, COMPACT, LARGE, MOTORBIKE, ELECTRIC
}

@Getter
@Setter
abstract class ParkingSpot {
    private String parkingSpotId;
    private boolean isFree;
    private ParkingSpotType parkingSpotType;
    private String assignedVehicleId;

    public ParkingSpot(String parkingSpotId, ParkingSpotType parkingSpotType) {
        this.parkingSpotId = parkingSpotId;
        this.parkingSpotType = parkingSpotType;
    }

    public void assignVehicleToSpot(String vehicleId) {
        this.assignedVehicleId = vehicleId;
        this.isFree = false;
    }

    public void freeSpot() {
        this.isFree = true;
        this.assignedVehicleId = null;
    }
}

class HandicappedParkingSpot extends ParkingSpot {
    public HandicappedParkingSpot(String parkingSpotId) {
        super(parkingSpotId, ParkingSpotType.HANDICAPPED);
    }
}

class CompactParkingSpot extends ParkingSpot {
    public CompactParkingSpot(String parkingSpotId) {
        super(parkingSpotId, ParkingSpotType.COMPACT);
    }
}

class LargeParkingSpot extends ParkingSpot {
    public LargeParkingSpot(String parkingSpotId) {
        super(parkingSpotId, ParkingSpotType.LARGE);
    }
}

class MotorbikeParkingSpot extends ParkingSpot {
    public MotorbikeParkingSpot(String parkingSpotId) {
        super(parkingSpotId, ParkingSpotType.MOTORBIKE);
    }
}

class ElectricParkingSpot extends ParkingSpot {
    public ElectricParkingSpot(String parkingSpotId) {
        super(parkingSpotId, ParkingSpotType.ELECTRIC);
    }
}

class ParkingFloor {
    @Getter
    @Setter
    private String floorId;

    @Getter
    private Map<ParkingSpotType, Deque<ParkingSpot>> parkingSpots = new HashMap<>(); // Why Deque?!
    private Map<String, ParkingSpot> usedParkingSpots = new HashMap<>(); // Implements as a Map and not list, as get operation in Map is O(1)

    /*
    About Java deque: https://www.educative.io/edpresso/how-to-use-a-java-deque
    Why Deque?!
    In the following Parking Lot implementation different parking strategy are not been added.
    But in interview we can be asked to apply a parking strategy. For example: Vehicles that will be parked for more then a day/for months
    should be parked in the end (Max distance) compare to one that are parked for few hours.

    In Deque we can add the parkingSpot based on there distance from the EXIT panel. (Min distance to Max)
    When a Vehicle need to be parked for less hours of time we can get parkingSpot from the front of Deque.
    When a Vehicle need to be parked for Long hours/days/months of time we can get parkingSpot from the end of Deque.

    One requirement can be : We have 4 entry points and we need to assign the nearest parking spot to the vehicle from the entrance that it took entry from.

    We will implement that by using MIN HEAP
    We will have:
    1. 4 Min heap, for 4 entrance. Heap will contains all the parking spot sorted in increasing order of the distance between the parking spot and the entrance.
    2. 1 Set to save available parking spots
    3. 1 Set to save reserved parking spots

    We will have a map of minHeap with key as entrance Id. Map<EnteranceID, MinHeap>
    Think : how will Heap works with Sets when a vehicle entry and exits the Parking Lot.
    Computational complexity : k log (n) [k = number of entrance , n = number of parking lot]

    */

    public ParkingFloor(String id) {
        this.floorId = id;
        parkingSpots.put(ParkingSpotType.HANDICAPPED, new ConcurrentLinkedDeque());
        parkingSpots.put(ParkingSpotType.LARGE, new ConcurrentLinkedDeque());
        parkingSpots.put(ParkingSpotType.COMPACT, new ConcurrentLinkedDeque());
        parkingSpots.put(ParkingSpotType.ELECTRIC, new ConcurrentLinkedDeque());
        parkingSpots.put(ParkingSpotType.MOTORBIKE, new ConcurrentLinkedDeque());
    }
    // About ConcurrentLinkedDeque : https://www.geeksforgeeks.org/concurrentlinkeddeque-in-java-with-examples/

    // This Check isFloorFull() can be done in some easy way as well.
    public boolean isFloorFull() {
        BitSet fullBitSet = new BitSet();
        int bitIndex = 0;
        for (Map.Entry<ParkingSpotType, Deque<ParkingSpot>> entry : parkingSpots.entrySet()) {
            if (entry.getValue().size() == 0) {
                fullBitSet.set(bitIndex++);
            } else {
                break;
            }
        }
        return fullBitSet.cardinality() == fullBitSet.size();
    }

    /*
    Mapping between VehicleType & ParkingSpotType
    In an interview we can be asked a use case: Let's same a case where ParkingSpotType CAR ParkingSpot are full. But as
    ParkingSpotType  LARGE is free, and a Car can fit in, we can assign that as well.
    */
    public static ParkingSpotType getSpotTypeForVehicle(VehicleType vehicleType) {
        switch (vehicleType) {
            case CAR:
                return ParkingSpotType.COMPACT;
            case ELECTRIC:
                return ParkingSpotType.ELECTRIC;
            case MOTORBIKE:
                return ParkingSpotType.MOTORBIKE;
            default:
                return ParkingSpotType.LARGE;
        }
    }

    public boolean canPark(VehicleType vehicleType) {
        return canPark(getSpotTypeForVehicle(vehicleType));
    }

    // return a Parking Spot (if present). Remove that from Deque and add in Map UsedParkingSpots
    public synchronized ParkingSpot getSpot(VehicleType vehicleType) {
        if (!canPark(getSpotTypeForVehicle(vehicleType)))
            return null;

        ParkingSpotType parkingSpotType = getSpotTypeForVehicle(vehicleType);
        ParkingSpot parkingSpot = parkingSpots.get(parkingSpotType)
                .poll();

        usedParkingSpots.put(parkingSpot.getParkingSpotId(), parkingSpot);
        return parkingSpot;
    }

    // Making a Parking-Spot free
    public ParkingSpot vacateSpot(String parkingSpotId) {
        ParkingSpot parkingSpot = usedParkingSpots.remove(parkingSpotId);
        if (parkingSpot != null) {
            parkingSpot.freeSpot();
            parkingSpots.get(parkingSpot.getParkingSpotType())
                    .addFirst(parkingSpot);
            return parkingSpot;
        }
        return null;
    }

    // Checking the Deque of specific ParkingSpotType is empty or not
    public boolean canPark(ParkingSpotType parkingSpotType) {
        return parkingSpots.get(parkingSpotType).size() > 0;
    }
}


@Getter
@Setter
class ParkingLot {
    private String parkingLotId;
    private Address address; // This System is generic can be used anywhere in world, so need this attribute

    private List<ParkingFloor> parkingFloors; // Can Add/Remove ParkingFloor
    private List<EntrancePanel> entrancePanels; // Can Add/Remove EntrancePanel
    private List<ExitPanel> exitPanels; // Can Add/Remove ExitPanel

    /*
    This Parking log is modeled as a Singleton
    We have multiple  entrancePanels & exitPanels but instance of ParkingLot should be single
    */
    public static ParkingLot INSTANCE = new ParkingLot();

    private ParkingLot() {
        this.parkingLotId = UUID.randomUUID().toString();
        parkingFloors = new ArrayList<>();
        entrancePanels = new ArrayList<>();
        exitPanels = new ArrayList<>();
    }

    // Need to check every floor, in turn floor check every parking spot present in that floor
    public boolean isFull() {
        int index = 0;
        BitSet bitSet = new BitSet();
        for (ParkingFloor parkingFloor : parkingFloors) {
            bitSet.set(index++, parkingFloor.isFloorFull());
        }
        return bitSet.cardinality() == bitSet.size();
    }

    // Need to check floor wish, in turn each floor checks do it has a free parking spot of vehicleType
    public boolean canPark(VehicleType vehicleType) {
        for (ParkingFloor parkingFloor : parkingFloors) {
            if (parkingFloor.canPark(ParkingFloor.getSpotTypeForVehicle(vehicleType)))
                return true;
        }
        return false;
    }

    // Need to check floor wish, in turn each floor checks do it has a free parking spot of vehicleType
    // If floor has a free parking spot, it return that back to parking lot
    public ParkingSpot getParkingSpot(VehicleType vehicleType) {
        for (ParkingFloor parkingFloor : ParkingLot.INSTANCE.getParkingFloors()) {
            ParkingSpot parkingSpot = parkingFloor.getSpot(vehicleType);
            if (parkingSpot != null) {
                return parkingSpot;
            }
        }
        return null;
    }

    // Free up a ParkingSpot
    public ParkingSpot vacateParkingSpot(String parkingSpotId) {
        for (ParkingFloor parkingFloor : ParkingLot.INSTANCE.getParkingFloors()) {
            ParkingSpot parkingSpot = parkingFloor.vacateSpot(parkingSpotId);
            if (parkingSpot != null)
                return parkingSpot;
        }
        return null;
    }
}


/************************************************** Ticket ***********************************************************/

enum TicketStatus {
    ACTIVE, LOST
}

@Getter
@Setter
class ParkingTicket {
    private String ticketNumber;
    private String licensePlateNumber;
    private String allocatedSpotId;
    private LocalDateTime issuedAt;
    private LocalDateTime vacatedAt;
    private double charges;
    private TicketStatus ticketStatus;
}

@Getter
class EntrancePanel {
    private String id;

    public EntrancePanel(String id) {
        this.id = id;
    }

    public ParkingTicket getParkingTicket(Vehicle vehicle) {
        if (!ParkingLot.INSTANCE.canPark(vehicle.getType()))
            return null;
        ParkingSpot parkingSpot = ParkingLot.INSTANCE.getParkingSpot(vehicle.getType());
        if (parkingSpot == null)
            return null;
        return buildTicket(vehicle.getLicenseNumber(), parkingSpot.getParkingSpotId());
    }

    private ParkingTicket buildTicket(String vehicleLicenseNumber, String parkingSpotId) {
        ParkingTicket parkingTicket = new ParkingTicket();
        parkingTicket.setIssuedAt(LocalDateTime.now());
        parkingTicket.setAllocatedSpotId(parkingSpotId);
        parkingTicket.setLicensePlateNumber(vehicleLicenseNumber);
        parkingTicket.setTicketNumber(UUID.randomUUID().toString());
        parkingTicket.setTicketStatus(TicketStatus.ACTIVE);
        return parkingTicket;
    }
    // About Java UUID: https://www.javatpoint.com/java-uuid
}

@Getter
@AllArgsConstructor
class ExitPanel {
    private String id;

    public ParkingTicket scanAndVacate(ParkingTicket parkingTicket) {
        ParkingSpot parkingSpot =
                ParkingLot.INSTANCE.vacateParkingSpot(parkingTicket.getAllocatedSpotId());
        parkingTicket.setCharges(calculateCost(parkingTicket, parkingSpot.getParkingSpotType()));
        return parkingTicket;
    }

    private double calculateCost(ParkingTicket parkingTicket, ParkingSpotType parkingSpotType) {
        Duration duration = Duration.between(parkingTicket.getIssuedAt(), LocalDateTime.now());
        long hours = duration.toHours();
        if (hours == 0)
            hours = 1;
        double amount = hours * new HourlyCost().getCost(parkingSpotType);
        return amount;
    }
}

@Getter
class HourlyCost {
    private Map<ParkingSpotType, Double> hourlyCosts = new HashMap<>();

    public HourlyCost() {
        hourlyCosts.put(ParkingSpotType.COMPACT, 20.0);
        hourlyCosts.put(ParkingSpotType.LARGE, 30.0);
        hourlyCosts.put(ParkingSpotType.ELECTRIC, 25.0);
        hourlyCosts.put(ParkingSpotType.MOTORBIKE, 10.0);
        hourlyCosts.put(ParkingSpotType.HANDICAPPED, 25.0);
    }

    public double getCost(ParkingSpotType parkingSpotType) {
        return hourlyCosts.get(parkingSpotType);
    }
}

/************************************************** Payment ***********************************************************/

enum PaymentStatus {
    SUCCESS, FAILED
}

@Getter
class Payment {
    private String id;
    private String ticketId;
    private double amount;

    @Setter
    private LocalDateTime initiatedDate;
    @Setter
    private LocalDateTime completedDate;
    @Setter
    private PaymentStatus paymentStatus;

    public Payment(String id, String ticketId, double amount) {
        this.id = id;
        this.ticketId = ticketId;
        this.amount = amount;
    }

    public void makePayment() {
        this.initiatedDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.completedDate = LocalDateTime.now();
    }
}

@Getter
@AllArgsConstructor
class PaymentPortal {
    private String id;

    public void scanTicket(ParkingTicket parkingTicket) {
        // ..........
    }

    public void makePayment(ParkingTicket parkingTicket) {
        // ..........
    }
}

/************************************************** Account ***********************************************************/

/*
This uses Global Address Model. (As we are implementing a generic Parking Lot System Software that can be used anywhere)
*/
@Getter
@Setter
class Address {
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String street;
    private String city;
    private String state;
    private String country;
    private String pinCode;
}

/*
Used only for Admin employ
*/
@Getter
@Setter
class PersonalInfo {
    private String firstName;
    private String lastName;
    private String middleName;
    private String initials;
    private String dob;
}

class Person {
}

/*
Contact is for people working at parking lot Administration.
But can be applied on User as well.
Lets Say some user wanted to park their vehicle for a month, In that case wee need their Contact.

*/
@Getter
@Setter
class Contact {
    private String phone;
    private String email;
    private Address address;
    private PersonalInfo personalInfo;
}

/*
Account can be for Admin, as well as Users.
Lets Say some user wanted to park their vehicle for a month, In that case wee need their contact and other details as well.
*/
@Getter
@Setter
abstract class Account {
    private String id;
    private String email;
    private String userName;
    private String password;
    private LocalDateTime lastAccessed;
    private Contact contact;
}

class Admin extends Account {
    ParkingLotRepository parkingLotRepository = new ParkingLotRepository();

    /*
    Addition is an idempotent operation.
    Q. What is an idempotent operation?
    A. An idempotent operation can be repeated an arbitrary number of times and the result will be the same as if it had been done only once.
    In arithmetic, adding zero to a number is idempotent.
    See: https://stackoverflow.com/questions/1077412/what-is-an-idempotent-operation
    */

    public void addParkingFloor(ParkingFloor parkingFloor) {
        // We are first checking that is the parkingFloor that we are trying to add is already present or not.
        Optional<ParkingFloor> floor =
                ParkingLot.INSTANCE.getParkingFloors().stream()
                        .filter(pF -> pF.getFloorId().equalsIgnoreCase(parkingFloor.getFloorId()))
                        .findFirst();

        // In case parkingFloor already exists, we simply return here.
        if (floor.isPresent())
            return;
        ParkingLot.INSTANCE.getParkingFloors().add(parkingFloor);
    }

    public void addParkingSpot(String parkingFloorId, ParkingSpot parkingSpot)
            throws InvlaidParkingFloorException {

        // parkingSpot can only be added in an existing parkingFloor, hence checking do we have the desired parkingFloor
        Optional<ParkingFloor> floor =
                ParkingLot.INSTANCE.getParkingFloors().stream()
                        .filter(pF -> pF.getFloorId().equalsIgnoreCase(parkingFloorId))
                        .findFirst();

        // If parkingFloor not present we just through exception & tell to try again with correct Parking Floor ID
        if (!floor.isPresent())
            throw new InvlaidParkingFloorException("Invalid floor");

        // We are first checking that is the ParkingSpot that we are trying to add is already present or not.
        Optional<ParkingSpot> spot =
                floor.get().getParkingSpots().get(parkingSpot.getParkingSpotType())
                        .stream()
                        .filter(pS -> pS.getParkingSpotId().equalsIgnoreCase(parkingSpot.getParkingSpotId()))
                        .findFirst();

        // In case ParkingSpot already exists, we simply return here.
        if (spot.isPresent())
            return;

        floor.get().getParkingSpots().get(parkingSpot.getParkingSpotType())
                .addLast(parkingSpot);
    }

    // EntrancePanel is at a parking lot level and not at Floor level
    public void addEntrancePanel(EntrancePanel entrancePanel) {

        // We are first checking that is the EntrancePanel that we are trying to add is already present or not.
        Optional<EntrancePanel> panel =
                ParkingLot.INSTANCE.getEntrancePanels().stream()
                        .filter(eP -> eP.getId().equalsIgnoreCase(entrancePanel.getId())).findFirst();

        // In case EntrancePanel already exists, we simply return here.
        if (panel.isPresent())
            return;

        ParkingLot.INSTANCE.getEntrancePanels().add(entrancePanel);
    }

    // ExitPanel is at a parking lot level and not at Floor level
    public void addExitPanel(ExitPanel exitPanel) {

        // We are first checking that is the ExitPanel that we are trying to add is already present or not.
        Optional<ExitPanel> panel =
                ParkingLot.INSTANCE.getExitPanels().stream()
                        .filter(eP -> eP.getId().equalsIgnoreCase(exitPanel.getId())).findFirst();

        // In case ExitPanel already exists, we simply return here.
        if (panel.isPresent())
            return;

        ParkingLot.INSTANCE.getExitPanels().add(exitPanel);
    }
}

/************************************************** Exceptions *********************************************************/

class InvalidParkingLotException extends Exception {
    public InvalidParkingLotException(String message) {
        super(message);
    }
}

class InvlaidParkingFloorException extends Exception {
    public InvlaidParkingFloorException(String message) {
        super(message);
    }
}

/************************************************** Repository *********************************************************/

class ParkingLotRepository {
    public static Map<String, ParkingLot> parkingLotMap = new HashMap<>();
    public static List<ParkingLot> parkingLots = new ArrayList<>();


    public ParkingLot addParkingLot(ParkingLot parkingLot) {
        parkingLotMap.putIfAbsent(parkingLot.getParkingLotId(), parkingLot);
        parkingLots.add(parkingLot);
        return parkingLot;
    }

    public ParkingLot getParkingLot(String parkingLotId) {
        return parkingLotMap.get(parkingLotId);
    }

    public ParkingFloor addParkingFloor(String parkingLotId, ParkingFloor parkingFloor)
            throws InvalidParkingLotException {
        ParkingLot parkingLot = parkingLotMap.get(parkingLotId);
        if (parkingLot == null)
            throw new InvalidParkingLotException("Invalid parking lot");

        //Idempotency
        Optional<ParkingFloor> floor = parkingLot.getParkingFloors().stream()
                .filter(pFloor -> pFloor.getFloorId()
                        .equalsIgnoreCase(parkingFloor.getFloorId())).findFirst();

        if (floor.isPresent())
            return floor.get();

        parkingLot.getParkingFloors().add(parkingFloor);
        return parkingFloor;
    }

    public ParkingSpot addParkingSpot(String parkingLotId, String parkingFloorId, ParkingSpot parkingSpot)
            throws InvalidParkingLotException, InvlaidParkingFloorException {
        ParkingLot parkingLot = parkingLotMap.get(parkingLotId);
        if (parkingLot == null)
            throw new InvalidParkingLotException("Invalid parking lot");
        Optional<ParkingFloor> floor = parkingLot.getParkingFloors().stream()
                .filter(pFloor -> pFloor.getFloorId()
                        .equalsIgnoreCase(parkingFloorId)).findFirst();
        if (!floor.isPresent()) {
            throw new InvlaidParkingFloorException("Invalid parking floor");
        }
        Optional<ParkingSpot> spot =
                floor.get().getParkingSpots().get(parkingSpot.getParkingSpotType())
                        .stream().filter(pSpot ->
                        pSpot.getParkingSpotId()
                                .equalsIgnoreCase(parkingSpot.getParkingSpotId())).findFirst();
        if (spot.isPresent())
            return spot.get();

        floor.get().getParkingSpots().get(parkingSpot.getParkingSpotType()).add(parkingSpot);
        return parkingSpot;
    }

    public EntrancePanel addEntryPanel(String parkingLotId, EntrancePanel entrancePanel)
            throws InvalidParkingLotException {
        ParkingLot parkingLot = parkingLotMap.get(parkingLotId);
        if (parkingLot == null)
            throw new InvalidParkingLotException("Invalid parking lot");
        Optional<EntrancePanel> ePanel =
                parkingLotMap.get(parkingLotId)
                        .getEntrancePanels().stream().filter(ep ->
                        ep.getId().equalsIgnoreCase(entrancePanel.getId())).findFirst();
        if (ePanel.isPresent())
            return entrancePanel;
        parkingLotMap.get(parkingLotId)
                .getEntrancePanels().add(entrancePanel);
        return entrancePanel;
    }

    public ExitPanel addExitPanel(String parkingLotId, ExitPanel exitPanel)
            throws InvalidParkingLotException {
        ParkingLot parkingLot = parkingLotMap.get(parkingLotId);
        if (parkingLot == null)
            throw new InvalidParkingLotException("Invalid parking lot");
        Optional<EntrancePanel> ePanel =
                parkingLotMap.get(parkingLotId)
                        .getEntrancePanels().stream().filter(ep ->
                        ep.getId().equalsIgnoreCase(exitPanel.getId())).findFirst();
        if (ePanel.isPresent())
            return exitPanel;
        parkingLotMap.get(parkingLotId)
                .getExitPanels().add(exitPanel);
        return exitPanel;
    }
}

class AdminRepository {
    public static Map<String, Admin> adminMap = new HashMap<>();
    public static List<Admin> admins = new ArrayList<>();

    public Admin addAdmin(Admin admin) {
        adminMap.putIfAbsent(admin.getId(), admin);
        admins.add(admin);
        return admin;
    }

    public Admin getAdminByEmail(String email) {
        Optional<Admin> admin =
                admins.stream().filter(adm -> adm.getEmail().equalsIgnoreCase(email)).findFirst();

        return admin.isPresent() ? admin.get() : null;
    }

    public Admin getAdminById(String id) {
        return adminMap.get(id);
    }
}

/*********************************************** ParkinglotApplication ************************************************/

class ParkinglotApplication {
    public static void main(String[] args) throws InvlaidParkingFloorException {
        ParkingLot parkingLot = ParkingLot.INSTANCE;

        Address address = new Address();
        address.setAddressLine1("Ram parking Complex");
        address.setStreet("BG Road");
        address.setCity("Bangalore");
        address.setState("Karnataka");
        address.setCountry("India");
        address.setPinCode("560075");

        parkingLot.setAddress(address);
        //Admin tests
        Account adminAccount = new Admin();
        //Admin Case 1 - should be able to add parking floor case
        ((Admin) adminAccount).addParkingFloor(new ParkingFloor("1"));
        //Admin Case 2 - should be able to add parking floor case
        ((Admin) adminAccount).addParkingFloor(new ParkingFloor("2"));

        //Admin Case 3 - should be able to add entrance panel
        EntrancePanel entrancePanel = new EntrancePanel("1");
        ((Admin) adminAccount).addEntrancePanel(entrancePanel);

        //Admin Case 4 - should be able to add exit panel
        ExitPanel exitPanel = new ExitPanel("1");
        ((Admin) adminAccount).addExitPanel(exitPanel);

        String floorId = parkingLot.getParkingFloors().get(0).getFloorId();

        ///Admin case 5 - should be able to add car parking spot
        ParkingSpot carSpot1 = new CompactParkingSpot("c1");
        ((Admin) adminAccount).addParkingSpot(floorId, carSpot1);
        ///Admin case 6 - should be able to add bike parking spot
        ParkingSpot bikeSport = new MotorbikeParkingSpot("b1");
        ((Admin) adminAccount).addParkingSpot(floorId, bikeSport);
        ///Admin case 7 - should be able to add car parking spot
        ParkingSpot carSpot2 = new CompactParkingSpot("c2");
        ((Admin) adminAccount).addParkingSpot(floorId, carSpot2);

        // Test case 1 - check for availability of parking lot - TRUE
        System.out.println(ParkingLot.INSTANCE.canPark(VehicleType.CAR));

        // Test case 2 - check for availability of parking lot - FALSE
        System.out.println(ParkingLot.INSTANCE.canPark(VehicleType.MOTORBIKE));

        // Test case 3 - check for availability of parking lot - FALSE
        System.out.println(ParkingLot.INSTANCE.canPark(VehicleType.ELECTRIC));

        // TEST case 4 - Check if full
        System.out.println(ParkingLot.INSTANCE.isFull());

        // Test case 5 - get parking spot
        Vehicle vehicle = new Car("KA05MR2311");
        ParkingSpot availableSpot = ParkingLot.INSTANCE.getParkingSpot(vehicle.getType());
        System.out.println(availableSpot.getParkingSpotType());
        System.out.println(availableSpot.getParkingSpotId());

        // Test case 6 - should not be able to get spot
        Vehicle van = new Van("KA01MR7804");
        ParkingSpot vanSpot = ParkingLot.INSTANCE.getParkingSpot(van.getType());
        System.out.println(null == vanSpot);

        //Test case 7 - Entrance Panel - 1
        System.out.println(ParkingLot.INSTANCE.getEntrancePanels().size());

        // Test case - 8 - Should be able to get parking ticket
        ParkingTicket parkingTicket = entrancePanel.getParkingTicket(vehicle);
        System.out.println(parkingTicket.getAllocatedSpotId());

        ((Admin) adminAccount).addParkingSpot(floorId, carSpot1);
        // Test case - 9 - Should be able to get parking ticket
        Vehicle car = new Car("KA02MR6355");
        ParkingTicket parkingTicket1 = entrancePanel.getParkingTicket(car);

        // Test case 10 - Should not be able to get ticket
        ParkingTicket tkt = entrancePanel.getParkingTicket(new Car("ka04rb8458"));
        System.out.println(null == tkt);

        // Test case 11 - Should be able to get ticket
        ParkingTicket mtrTkt = entrancePanel.getParkingTicket(new Moterbike("ka01ee4901"));
        System.out.println(mtrTkt.getAllocatedSpotId());

        //Test case 12 - vacate parking spot
        mtrTkt = exitPanel.scanAndVacate(mtrTkt);
        System.out.println(mtrTkt.getCharges());
        System.out.println(mtrTkt.getCharges() > 0);

        // Test case 13 - park on vacated spot
        ParkingTicket mtrTkt1 = entrancePanel.getParkingTicket(new Moterbike("ka01ee7791"));
        System.out.println(mtrTkt.getAllocatedSpotId());

        // Test case 14 - park when spot is not available
        ParkingTicket unavaialbemTkt =
                entrancePanel.getParkingTicket(new Moterbike("ka01ee4455"));
        System.out.println(null == unavaialbemTkt);

        // Test cast 15 - vacate car
        parkingTicket = exitPanel.scanAndVacate(parkingTicket);
        System.out.println(parkingTicket.getCharges());
        System.out.println(parkingTicket.getCharges() > 0);

        //Test case 16 - Now should be able to park car
        System.out.println(ParkingLot.INSTANCE.canPark(VehicleType.CAR));

        //Test case 17 - Should be able to vacate parked vehicle
        parkingTicket1 = exitPanel.scanAndVacate(parkingTicket1);
        System.out.println(parkingTicket1.getCharges());
        System.out.println(parkingTicket1.getCharges() > 0);

        //Test case 18 - check for slots count
        System.out.println(ParkingLot.INSTANCE.getParkingFloors()
                .get(0).getParkingSpots().get(ParkingSpotType.COMPACT).size());

        //Test case 19 - Payment
        Payment payment = new Payment(UUID.randomUUID().toString(),
                parkingTicket1.getTicketNumber(), parkingTicket1.getCharges());
        payment.makePayment();
        System.out.println(payment.getPaymentStatus());

        //Test case 20 - vacate motorbike spot
        mtrTkt = exitPanel.scanAndVacate(mtrTkt);
        System.out.println(ParkingLot.INSTANCE.getParkingFloors()
                .get(0).getParkingSpots().get(ParkingSpotType.MOTORBIKE).size());
        System.out.println(mtrTkt.getCharges());
    }
}