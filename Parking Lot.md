# Parking Lot

## First
[Parking Lot Design | Object-Oriented Design Interview Question](https://youtu.be/tVRyb4HaHgw) - **Think Software**

**Requirements:**

1. Parking lot capacity/limit : 10k to 30k spots
2. Number of entrances and exits : 4 entrances & 4 exits
3. Customer collect the ticket at entrance & parking spot is assigned on the ticket
4. Parking spot assigned to the vehicle should be nearest to the entrance.
5. Their will be different type of parking spots : Handicap, Compact, Large, Motorcycle
6. Hourly rate will be applied for parking.
7. Customer can pay by cash/credit card at exit.
8. Monitoring System : monitoring how many car entry/exit the parking lot.

**Approach** : Bottom-up (First design the smallest components and then move towards bigger once)

**Objects & Actors in our System:**

1. Parking lot system
2. Entry/Exit terminal 
    1. Printer (Entry)
    2. Payment Processor (Exit)
3. Parking Spot
4. Ticket
5. Database
6. Monitoring System

Think : why we are not keeping a Vehicle Class?? For different type of vehicle!!

Note: **Open Close Design Principle** : Existing and well tested classes shouldn’t be modified whenever new features need to be built. 

![](/Images/PLD01.jpeg)

If we would needed to add any new type of parking spot we can just extend Parking Spot Class.

![](/Images/PLD02.jpeg)

Our requirement: We have 4 entry points and we need to assign the nearest parking spot to the vehicle from the entrance that it took entry from.

We will implement that by using **MIN HEAP**

We will have:

1. 4 Min heap, for 4 entrance. Heap will contains all the parking spot sorted in increasing order of the distance between the parking spot and the entrance.
2. 1 Set to save available parking spots
3. 1 Set to save reserved parking spots

We will have a map of minHeap with key as entrance Id. Map<EnteranceID, MinHeap>

Think : how will Heap works with Sets when a vehicle entry and exits the Parking Lot.

Computational complexity : k log (n) [k = number of entrance , n = number of parking lot]

![](/Images/PLD03.jpeg)

**Strategy Design Pattern** will be used for Payment Processor.

We have added spotType in argument of calculateTeriff as, their might be cost difference in both, also it can be dependent of weekdays.

## Second

[System Design Interview Question: DESIGN A PARKING LOT](https://youtu.be/koNq99nOV88) - **Reach Goals**

Before System Design we need to look in the followings:

![](/Images/PLD04.png) 

![](/Images/PLD05.png) 

**Step1:** Visualise a parking lot

**Step2:** Identifying of Classes and Methods 

![](/Images/PLD06.png) 

**Step3:** Before drawing the UML capture all requirements in tabular form with :

1. Item/Object/Class
2. Property 
3. Activity/Action/Method
4. Relation between Classes

![](/Images/PLD07.png) 

**Step4:** UML diagram

![](/Images/PLD08.png)

![](/Images/PLD09.png)