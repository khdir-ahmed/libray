-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : dim. 03 mai 2026 à 22:28
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `bookstore`
--

-- --------------------------------------------------------

--
-- Structure de la table `administrator`
--

CREATE TABLE `administrator` (
  `admin_id` int(11) NOT NULL,
  `person_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `administrator`
--

INSERT INTO `administrator` (`admin_id`, `person_id`) VALUES
(2, NULL),
(1, 1),
(3, 3);

-- --------------------------------------------------------

--
-- Structure de la table `author`
--

CREATE TABLE `author` (
  `author_id` int(11) NOT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `nationality` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `author`
--

INSERT INTO `author` (`author_id`, `first_name`, `last_name`, `nationality`) VALUES
(1, 'Robert', 'Martin', 'American'),
(2, 'Douglas', 'Crockford', 'American'),
(3, 'George', 'Orwell', 'British'),
(4, 'Robert', 'Martin', 'American'),
(5, 'Douglas', 'Crockford', 'American'),
(6, 'George', 'Orwell', 'British');

-- --------------------------------------------------------

--
-- Structure de la table `book`
--

CREATE TABLE `book` (
  `book_id` int(11) NOT NULL,
  `genre` varchar(50) DEFAULT NULL,
  `isbn` varchar(20) DEFAULT NULL,
  `year_published` int(11) DEFAULT NULL,
  `publisher_id` int(11) DEFAULT NULL,
  `title` varchar(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `book`
--

INSERT INTO `book` (`book_id`, `genre`, `isbn`, `year_published`, `publisher_id`, `title`) VALUES
(1, 'Programming', '978-0132350884', 2008, 1, 'Clean Code'),
(2, 'Programming', '978-0596517748', 2008, 2, 'JavaScript: Good Parts'),
(3, 'Fiction', '978-0451524935', 1949, 3, '1984'),
(4, '', '1212121221545412', 0, 1, 'ff');

-- --------------------------------------------------------

--
-- Structure de la table `books`
--

CREATE TABLE `books` (
  `id` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `author` varchar(100) NOT NULL,
  `genre` varchar(50) NOT NULL,
  `isbn` varchar(20) NOT NULL,
  `total_copies` int(11) NOT NULL DEFAULT 1,
  `available_copies` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `books`
--

INSERT INTO `books` (`id`, `title`, `author`, `genre`, `isbn`, `total_copies`, `available_copies`) VALUES
(1, 'The Great Gatsby', 'F. Scott Fitzgerald', 'Classic', '978-0743273565', 3, 2),
(2, 'test', 'Harper Lee', 'Fiction', '978-0061935466', 4, 3),
(3, '1984', 'George Orwell', 'Dystopian', '978-0451524935', 4, 4),
(4, 'Pride and Prejudice', 'Jane Austen', 'Romance', '978-0141439518', 5, 5),
(5, 'The Catcher in the Rye', 'J.D. Salinger', 'Fiction', '978-0316769174', 1, 0),
(6, 'Harry Potter & the Sorcerer\'s Stone', 'J.K. Rowling', 'Fantasy', '978-0590353427', 6, 6),
(7, 'The Hobbit', 'J.R.R. Tolkien', 'Fantasy', '978-0547928227', 3, 3),
(8, 'Brave New World', 'Aldous Huxley', 'Dystopian', '978-0060850524', 2, 2),
(9, 'The Alchemist', 'Paulo Coelho', 'Adventure', '978-0062315007', 4, 4),
(10, 'Sapiens', 'Yuval Noah Harari', 'History', '978-0062316097', 3, 3),
(11, 'Atomic Habits', 'James Clear', 'Self-Help', '978-0735211292', 5, 5),
(12, 'Dune', 'Frank Herbert', 'Sci-Fi', '978-0441013593', 2, 2),
(13, 'The Da Vinci Code', 'Dan Brown', 'Thriller', '978-0307474278', 0, 0),
(14, 'Gone Girl', 'Gillian Flynn', 'Mystery', '978-0307588371', 1, 1),
(15, 'The Midnight Library', 'Matt Haig', 'Fiction', '978-0525559474', 3, 3);

-- --------------------------------------------------------

--
-- Structure de la table `book_author`
--

CREATE TABLE `book_author` (
  `author_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `book_author`
--

INSERT INTO `book_author` (`author_id`, `book_id`) VALUES
(1, 1),
(2, 2),
(3, 3);

-- --------------------------------------------------------

--
-- Structure de la table `book_copies`
--

CREATE TABLE `book_copies` (
  `book_id` int(11) NOT NULL,
  `copy_number` int(11) NOT NULL,
  `is_available` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `book_copies`
--

INSERT INTO `book_copies` (`book_id`, `copy_number`, `is_available`) VALUES
(1, 1, 1),
(1, 2, 1),
(1, 3, 0),
(2, 1, 1),
(2, 2, 0),
(2, 3, 1),
(2, 4, 1),
(3, 1, 1),
(3, 2, 1),
(3, 3, 1),
(3, 4, 1),
(4, 1, 1),
(4, 2, 1),
(4, 3, 1),
(4, 4, 1),
(4, 5, 1),
(5, 1, 0),
(5, 2, 1),
(6, 1, 1),
(6, 2, 1),
(6, 3, 1),
(6, 4, 1),
(6, 5, 1),
(6, 6, 1),
(7, 1, 1),
(7, 2, 1),
(7, 3, 1),
(8, 1, 1),
(8, 2, 1),
(9, 1, 1),
(9, 2, 1),
(9, 3, 1),
(9, 4, 1),
(10, 1, 1),
(10, 2, 1),
(10, 3, 1),
(11, 1, 1),
(11, 2, 1),
(11, 3, 1),
(11, 4, 1),
(11, 5, 1),
(12, 1, 1),
(12, 2, 1),
(14, 1, 1),
(15, 1, 1),
(15, 2, 1),
(15, 3, 1);

-- --------------------------------------------------------

--
-- Structure de la table `book_holds`
--

CREATE TABLE `book_holds` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `book_id` int(11) NOT NULL,
  `copy_number` int(11) NOT NULL,
  `expected_date` date NOT NULL,
  `created_date` date NOT NULL,
  `fulfilled` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `book_holds`
--

INSERT INTO `book_holds` (`id`, `username`, `book_id`, `copy_number`, `expected_date`, `created_date`, `fulfilled`) VALUES
(1, 'alice', 5, 1, '2026-05-17', '2026-05-03', 0);

-- --------------------------------------------------------

--
-- Structure de la table `borrow_request`
--

CREATE TABLE `borrow_request` (
  `request_id` int(11) NOT NULL,
  `member_id` int(11) DEFAULT NULL,
  `copy_id` int(11) DEFAULT NULL,
  `request_date` datetime DEFAULT current_timestamp(),
  `status` varchar(20) DEFAULT 'PENDING'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `copy`
--

CREATE TABLE `copy` (
  `copy_id` int(11) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  `book_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `copy`
--

INSERT INTO `copy` (`copy_id`, `status`, `book_id`) VALUES
(1, 'AVAILABLE', 1),
(2, 'AVAILABLE', 1),
(3, 'AVAILABLE', 2),
(4, 'AVAILABLE', 2),
(5, 'AVAILABLE', 3),
(6, 'AVAILABLE', 3),
(7, 'AVAILABLE', 2),
(8, 'AVAILABLE', 4);

-- --------------------------------------------------------

--
-- Structure de la table `loan`
--

CREATE TABLE `loan` (
  `loan_id` int(11) NOT NULL,
  `member_id` int(11) DEFAULT NULL,
  `copy_id` int(11) DEFAULT NULL,
  `loan_date` date DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `return_date` date DEFAULT NULL,
  `amount` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `member`
--

CREATE TABLE `member` (
  `member_id` int(11) NOT NULL,
  `person_id` int(11) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `penalty_count` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `penalty`
--

CREATE TABLE `penalty` (
  `penalty_id` int(11) NOT NULL,
  `loan_id` int(11) DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `issued_date` datetime DEFAULT NULL,
  `paid_status` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `person`
--

CREATE TABLE `person` (
  `person_id` int(11) NOT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` int(11) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `address` varchar(50) DEFAULT NULL,
  `image` varchar(50) DEFAULT NULL,
  `must_change_password` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `person`
--

INSERT INTO `person` (`person_id`, `first_name`, `last_name`, `email`, `password`, `phone`, `birth_date`, `address`, `image`, `must_change_password`) VALUES
(1, 'System', 'Admin', 'admin@library.com', '$2b$10$tk05PI2RSarx3jGyj/12Kuby3AXQ4IUgfZ0.VIYPf9KCm15LUU2gS', NULL, NULL, NULL, NULL, 0),
(3, 'ffd', '', 'delove@gmail.comp', '$2a$10$UvoU76VQ/O/ZVJKLwbfkf.FZ8tSbCobIVHvAjLbfbNFm9jPVZTrM6', 11, NULL, NULL, NULL, 1);

-- --------------------------------------------------------

--
-- Structure de la table `publisher`
--

CREATE TABLE `publisher` (
  `publisher_id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `publisher`
--

INSERT INTO `publisher` (`publisher_id`, `name`) VALUES
(1, 'Prentice Hall'),
(2, 'O\'Reilly'),
(3, 'Signet Classics'),
(4, 'Prentice Hall'),
(5, 'O\'Reilly'),
(6, 'Signet Classics');

-- --------------------------------------------------------

--
-- Structure de la table `reservation`
--

CREATE TABLE `reservation` (
  `reservation_id` int(11) NOT NULL,
  `member_id` int(11) DEFAULT NULL,
  `copy_id` int(11) DEFAULT NULL,
  `reserved_date` date DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `reservations`
--

CREATE TABLE `reservations` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `book_id` int(11) NOT NULL,
  `status` enum('PENDING','ACTIVE','REJECTED','RETURNED','RETURN_PENDING') NOT NULL DEFAULT 'PENDING',
  `reservation_date` date NOT NULL,
  `due_date` date NOT NULL,
  `copy_number` int(11) DEFAULT NULL,
  `penalty_counted` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `reservations`
--

INSERT INTO `reservations` (`id`, `username`, `book_id`, `status`, `reservation_date`, `due_date`, `copy_number`, `penalty_counted`) VALUES
(1, 'mohamed', 1, 'RETURNED', '2026-04-13', '2026-04-27', NULL, 0),
(2, 'mohamed', 1, 'RETURNED', '2026-04-16', '2026-04-30', 1, 0),
(3, 'alice', 1, 'RETURNED', '2026-04-17', '2026-05-01', 1, 1),
(4, 'mohamed', 1, 'RETURNED', '2026-04-17', '2026-05-01', 2, 0),
(5, 'bob', 1, 'ACTIVE', '2026-04-17', '2026-05-01', 3, 1),
(6, 'ayedi', 1, 'REJECTED', '2026-04-17', '2026-05-01', NULL, 0),
(7, 'mohamed', 2, 'RETURNED', '2026-04-20', '2026-05-04', 1, 0),
(8, 'alice', 2, 'RETURN_PENDING', '2026-04-28', '2026-05-12', 2, 0),
(9, 'mohamed', 5, 'ACTIVE', '2026-05-03', '2026-05-17', 1, 0);

-- --------------------------------------------------------

--
-- Structure de la table `return_request`
--

CREATE TABLE `return_request` (
  `request_id` int(11) NOT NULL,
  `loan_id` int(11) DEFAULT NULL,
  `member_id` int(11) DEFAULT NULL,
  `request_date` datetime DEFAULT current_timestamp(),
  `status` varchar(20) DEFAULT 'PENDING'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `is_admin` tinyint(1) NOT NULL DEFAULT 0,
  `phone` varchar(8) DEFAULT NULL,
  `penalty_points` int(11) NOT NULL DEFAULT 0,
  `is_blocked` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`username`, `password`, `is_admin`, `phone`, `penalty_points`, `is_blocked`) VALUES
('admin', 'admin123', 1, '22334455', 0, 0),
('alice', 'alice123', 0, '55667788', 1, 0),
('ayedi', '123456', 0, '14785236', 0, 0),
('bob', 'bob123', 0, '99001122', 1, 0),
('khdir', '123456', 0, '12345678', 0, 0),
('mohamed', '123456', 0, '33445566', 0, 0);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `administrator`
--
ALTER TABLE `administrator`
  ADD PRIMARY KEY (`admin_id`),
  ADD KEY `person_id` (`person_id`);

--
-- Index pour la table `author`
--
ALTER TABLE `author`
  ADD PRIMARY KEY (`author_id`);

--
-- Index pour la table `book`
--
ALTER TABLE `book`
  ADD PRIMARY KEY (`book_id`),
  ADD KEY `publisher_id` (`publisher_id`);

--
-- Index pour la table `books`
--
ALTER TABLE `books`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `book_author`
--
ALTER TABLE `book_author`
  ADD PRIMARY KEY (`author_id`,`book_id`),
  ADD KEY `book_id` (`book_id`);

--
-- Index pour la table `book_copies`
--
ALTER TABLE `book_copies`
  ADD PRIMARY KEY (`book_id`,`copy_number`);

--
-- Index pour la table `book_holds`
--
ALTER TABLE `book_holds`
  ADD PRIMARY KEY (`id`),
  ADD KEY `username` (`username`),
  ADD KEY `book_id` (`book_id`);

--
-- Index pour la table `borrow_request`
--
ALTER TABLE `borrow_request`
  ADD PRIMARY KEY (`request_id`),
  ADD KEY `member_id` (`member_id`),
  ADD KEY `copy_id` (`copy_id`);

--
-- Index pour la table `copy`
--
ALTER TABLE `copy`
  ADD PRIMARY KEY (`copy_id`),
  ADD KEY `book_id` (`book_id`);

--
-- Index pour la table `loan`
--
ALTER TABLE `loan`
  ADD PRIMARY KEY (`loan_id`),
  ADD KEY `member_id` (`member_id`),
  ADD KEY `copy_id` (`copy_id`);

--
-- Index pour la table `member`
--
ALTER TABLE `member`
  ADD PRIMARY KEY (`member_id`),
  ADD KEY `person_id` (`person_id`);

--
-- Index pour la table `penalty`
--
ALTER TABLE `penalty`
  ADD PRIMARY KEY (`penalty_id`),
  ADD KEY `loan_id` (`loan_id`);

--
-- Index pour la table `person`
--
ALTER TABLE `person`
  ADD PRIMARY KEY (`person_id`);

--
-- Index pour la table `publisher`
--
ALTER TABLE `publisher`
  ADD PRIMARY KEY (`publisher_id`);

--
-- Index pour la table `reservation`
--
ALTER TABLE `reservation`
  ADD PRIMARY KEY (`reservation_id`),
  ADD KEY `member_id` (`member_id`),
  ADD KEY `copy_id` (`copy_id`);

--
-- Index pour la table `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `username` (`username`),
  ADD KEY `book_id` (`book_id`);

--
-- Index pour la table `return_request`
--
ALTER TABLE `return_request`
  ADD PRIMARY KEY (`request_id`),
  ADD KEY `member_id` (`member_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`username`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `administrator`
--
ALTER TABLE `administrator`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `author`
--
ALTER TABLE `author`
  MODIFY `author_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pour la table `book`
--
ALTER TABLE `book`
  MODIFY `book_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `books`
--
ALTER TABLE `books`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT pour la table `book_holds`
--
ALTER TABLE `book_holds`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `borrow_request`
--
ALTER TABLE `borrow_request`
  MODIFY `request_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `copy`
--
ALTER TABLE `copy`
  MODIFY `copy_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT pour la table `loan`
--
ALTER TABLE `loan`
  MODIFY `loan_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `member`
--
ALTER TABLE `member`
  MODIFY `member_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `penalty`
--
ALTER TABLE `penalty`
  MODIFY `penalty_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `person`
--
ALTER TABLE `person`
  MODIFY `person_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `publisher`
--
ALTER TABLE `publisher`
  MODIFY `publisher_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pour la table `reservation`
--
ALTER TABLE `reservation`
  MODIFY `reservation_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT pour la table `return_request`
--
ALTER TABLE `return_request`
  MODIFY `request_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `administrator`
--
ALTER TABLE `administrator`
  ADD CONSTRAINT `administrator_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`);

--
-- Contraintes pour la table `book`
--
ALTER TABLE `book`
  ADD CONSTRAINT `book_ibfk_1` FOREIGN KEY (`publisher_id`) REFERENCES `publisher` (`publisher_id`);

--
-- Contraintes pour la table `book_author`
--
ALTER TABLE `book_author`
  ADD CONSTRAINT `book_author_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `author` (`author_id`),
  ADD CONSTRAINT `book_author_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`);

--
-- Contraintes pour la table `book_copies`
--
ALTER TABLE `book_copies`
  ADD CONSTRAINT `book_copies_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `book_holds`
--
ALTER TABLE `book_holds`
  ADD CONSTRAINT `book_holds_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE,
  ADD CONSTRAINT `book_holds_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `borrow_request`
--
ALTER TABLE `borrow_request`
  ADD CONSTRAINT `borrow_request_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
  ADD CONSTRAINT `borrow_request_ibfk_2` FOREIGN KEY (`copy_id`) REFERENCES `copy` (`copy_id`);

--
-- Contraintes pour la table `copy`
--
ALTER TABLE `copy`
  ADD CONSTRAINT `copy_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`);

--
-- Contraintes pour la table `loan`
--
ALTER TABLE `loan`
  ADD CONSTRAINT `loan_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
  ADD CONSTRAINT `loan_ibfk_2` FOREIGN KEY (`copy_id`) REFERENCES `copy` (`copy_id`);

--
-- Contraintes pour la table `member`
--
ALTER TABLE `member`
  ADD CONSTRAINT `member_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`);

--
-- Contraintes pour la table `penalty`
--
ALTER TABLE `penalty`
  ADD CONSTRAINT `penalty_ibfk_1` FOREIGN KEY (`loan_id`) REFERENCES `loan` (`loan_id`);

--
-- Contraintes pour la table `reservation`
--
ALTER TABLE `reservation`
  ADD CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
  ADD CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`copy_id`) REFERENCES `copy` (`copy_id`);

--
-- Contraintes pour la table `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE,
  ADD CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `return_request`
--
ALTER TABLE `return_request`
  ADD CONSTRAINT `return_request_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
