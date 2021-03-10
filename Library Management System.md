# Library Management System

## System Requirements

1. Any library member should be able to search books by their title, author, subject category as well by the publication date.
2. Each book will have a unique identification number and other details including a rack number which will help to physically locate the book.
3. There could be more than one copy of a book, and library members should be able to check-out and reserve any copy. We will call each copy of a book, a book item.
4. The system should be able to retrieve information like who took a particular book or what are the books checked-out by a specific library member.
5. There should be a maximum limit (5) on how many books a member can check-out.
6. There should be a maximum limit (10) on how many days a member can keep a book.
7. The system should be able to collect fines for books returned after the due date.
8. Members should be able to reserve books that are not currently available.
9. The system should be able to send notifications whenever the reserved books become available, as well as when the book is not returned within the due date.
10. Each book and member card will have a unique barcode. The system will be able to read barcodes from books and members’ library cards.

## Actors:

1. Librarian
2. Customer 
3. System

## Use-Case:

1. Add, Remove and Edit Books
2. Search (By title, Author, Subject, Publication date)
3. Send Notifications
4. Issue a book
5. Reserve a book
6. Renew a book
7. Return a book

## Relationship b/w Actors & Use-Case:

### Customer

1. Search (By title, Author, Subject, Publication date)
2. Renew a book
3. Reserve a book
4. Return a book
5. Pay Fine

### Librarian

1. Search (By title, Author, Subject, Publication date)
2. Add Book
3. Edit Book
4. Delete Book

### System

1. Send Notifications

## Use case diagram
![](/Images/LibraryManagementLLD.png)

## Code

**Class Library**

```java
class Library {
    String name;
    Address location;
    List<BookItem> books;
}

class Address {
    int pinCode;
    String street;
    String city;
    String state;
    String country;
}
```

**Classes related to Books**

```java
enum BookType {
    SCI_FI, ROMANTIC, FANTASY, DRAMA;
}

enum BookFormat {
    HARDCOVER, PAPERBACK, NEWSPAPER, JOURNAL;
}

enum BookStatus {
    ISSUED, AVAILABLE, RESERVED, LOST;
}

class Rack {
    int number;
    String locationId;
}

class Book {
    String uniqueIdNumber;
    String title;
    List<Author> authors;
    BookType bookType;
}

/*
A book can have multiple publication hence each real life book will be the object of BookItem Class
*/
class BookItem extends Book {
    String barcode;
    Date publicationDate;
    Rack rackLocation;
    BookStatus bookStatus;
    BookFormat bookFormat;
    Date issueDate;
}
```

**Class Author, Member and Librarian**

```java
class Person {
    String firstName;
    String lastName;
}

class Author extends Person {
    List<Book> booksPublished;
}

class Account {
    String userName;
    String password;
    int accountId;
}

class SystemUser extends Person {
    String Email;
    String phoneNumber;
    Account account;
}

class Member extends SystemUser {
    // We can limit this to n number of books
    int totalBookCheckedOut;

    Search searchObj;
    BookIssueService issueService;
}

class Librarian extends SystemUser {
    Search searchObj;
    BookIssueService issueService;

    public void addBookItem(BookItem bookItem) {
        // ...
    };
    public BookItem deleteBookItem(String barcode) {
        // ...
    };
    public BookItem editBookItem(BookItem bookItem) {
        // ...
    };
}
```

**Class Search**

```java
class Search {
    public List<BookItem> geBookByTitle(String title) {
        // ...
    };
    public List<BookItem> geBookByAuthor(Author author) {
        // ...
    };
    public List<BookItem> geBookByType(BookType bookType) {
        // ...
    };
    public List<BookItem> geBookByPublicationDate(Date publicationDate) {
        // ...
    };
}
```

**Class BookIssueService**

```java
class BookIssueService {

    Fine fine;

    public BookReservationDetail getReservationDetail(BookItem book) {
        // ...
    };

    public void updateReservationDetail(BookReservationDetail bookReservationDetail) {
        // ...
    };

    public BookReservationDetail reserveBook(BookItem book, SystemUser user) {
        // ...
    };

    public BookIssueDetail issueBook(BookItem book, SystemUser user) {
        // ...
    };

    // it will internally call the issueBook function after basic validations
    public BookIssueDetail renewBook(BookItem book, SystemUser user) {
        // ...
    };

    public void returnBook(BookItem book, SystemUser user) {
        // ...
    };
}
```

**Class BookLending & Fine**

```java
class BookLending {
    BookItem book;
    Date startDate;
    SystemUser user;
}

class BookReservationDetail extends BookLending {
    ReservationStatus reservationStatus;
}

enum ReservationStatus {
    SUCCESSFUL, NOT_SUCCESSFUL, WAITING_LIST
}

class BookIssueDetail extends BookLending {
    Date dueDate;
}

class Fine {
    Date fineDate;
    BookItem book;
    SystemUser user;

    public double calculateFine(int days) {
        // ...
    };
}
```

## Reference:

1. [Low Level Design of Library Management System - Part 1](https://youtu.be/71W8QTdFWw8?list=PL12BCqE-Lp650Cg6FZW7SoZwN8Rw1WJI7)
2. [Low Level Design of Library Management System - Part 2](https://youtu.be/es4uliuvrTI?list=PL12BCqE-Lp650Cg6FZW7SoZwN8Rw1WJI7)
3. [Design a Library Management System - grokking](https://raw.githubusercontent.com/himanshukr-nsit/Object-Oriented-Design-Pattern-Interview/master/2.%20Object%20Oriented%20Design%20Case%20Studies/1.%20Design%20a%20Library%20Management%20System/1.1Design%20a%20Library%20Management%20System%20-%20Grokking%20the%20Object%20Oriented%20Design%20Interview.html)
