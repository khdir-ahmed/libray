# 📚 Library Book Reservation System

A Java **Swing** desktop application for managing a library: browsing books, reserving them, handling returns, holds and penalties. Data is stored in **MySQL** through JDBC.

## Features

**Members**
- Log in and browse or search the catalogue by title and genre
- Request to borrow a book, then follow its status (pending, approved, returned)
- Request a return, or place a **hold** on a book with no free copies, which shows the expected availability date
- Penalty points for overdue books. Users with too many points are blocked.

**Administrators**
- Add, edit and delete books and manage their copies
- Approve or reject borrow requests and confirm returns
- Manage users (add, delete, reset penalties)
- See all reservations in one view

## Tech stack

| Layer    | Technology                         |
|----------|------------------------------------|
| Language | Java 11                            |
| UI       | Java Swing                         |
| Database | MySQL / MariaDB (XAMPP)            |
| Driver   | MySQL Connector/J 8.1 (in `lib/`)  |
| Build    | Maven, or the included `.bat` scripts |

## Getting started

### 1. Database
1. Start **MySQL** (for example from the XAMPP control panel).
2. Open phpMyAdmin, create a database named `bookstore` and import `bookstore.sql`.

The connection settings are in `src/main/java/com/java/libray/DataManager.java`:

```java
jdbc:mysql://localhost:3306/bookstore   user: root   password: (empty)
```

### 2. Run

**Option A: batch scripts (Windows)**
```bat
compile.bat
run.bat
```

**Option B: Maven**
```bash
mvn compile exec:java
```

## Demo accounts

| Role   | Username | Password   |
|--------|----------|------------|
| Admin  | `admin`  | `admin123` |
| Member | `alice`  | `alice123` |
| Member | `bob`    | `bob123`   |

## Project structure

```
libray/
├── bookstore.sql                 Database dump (schema + sample data)
├── compile.bat / run.bat         Build & run without Maven
├── lib/mysql-connector-j-8.1.0.jar
├── pom.xml
└── src/main/java/com/java/libray/
    ├── Launcher.java             Entry point
    ├── LoginFrame.java           Login window
    ├── MainFrame.java            Main window with tabs per role
    ├── BooksPanel.java           Catalogue + search + reserve
    ├── MyReservationsPanel.java  Member's reservations & holds
    ├── ManageBooksPanel.java     Admin: book CRUD
    ├── ManageUsersPanel.java     Admin: user management
    ├── AllReservationsPanel.java Admin: approve / reject / returns
    ├── DataManager.java          JDBC data access (singleton)
    └── Book.java, Reservation.java, BookHold.java   Models
```
