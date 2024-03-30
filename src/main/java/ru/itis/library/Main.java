package ru.itis.library;

import ru.itis.library.exceptions.DbException;
import ru.itis.library.model.Book;
import ru.itis.library.model.GivenBook;
import ru.itis.library.model.User;
import ru.itis.library.reposiroty.impl.BookRepository;
import ru.itis.library.reposiroty.impl.GivenBookRepository;
import ru.itis.library.reposiroty.impl.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static BookRepository bookRepository;
    private static GivenBookRepository givenBookRepository;
    private static UserRepository userRepository;

    public static void main(String[] args) throws DbException {
        bookRepository =  new BookRepository();
        givenBookRepository =  new GivenBookRepository();
        userRepository =  new UserRepository();

        while (true) {
            String command = scanner.nextLine();
            switch (command) {
                case "":
                    break;
                case "help":
                    BooksHelp.printHelp();
                    break;
                case "readAllBooks":
                    List<Book> books = bookRepository.getAll();
                    printBooks(books);
                    break;
                case "readAllUsers":
                    List<User> users = userRepository.getAll();
                    printUsers(users);
                    break;
                case "readGivenBooks":
                    List<GivenBook> givenBooks = givenBookRepository.getAll();
                    printGivenBooks(givenBooks);
                    break;
                case "addBook":
                    addBook();
                    break;
                case "addUser":
                    addUser();
                    break;
                case "giveBook":
                    giveBook();
                    break;
                case "quit":
                    System.out.println("Good bye");
                    return;
                default:
                    System.out.println("Unknown command");
            }
        }
    }

    private static void addBook() throws DbException {
        System.out.println("Введите название книги: ");
        String name = scanner.nextLine();
        System.out.println("Введите автора книги: ");
        String author = scanner.nextLine();
        Book book = new Book(null, author, name);
        Book savedBook = bookRepository.save(book);
        System.out.println("Книга сохранена с id " + savedBook.getId());
    }

    private static void addUser() throws DbException {
        System.out.println("Введите имя читателя: ");
        String name = scanner.nextLine();
        System.out.println("Введите возраст читателя: ");
        int age = scanner.nextInt();
        User user = new User(null, name, age);
        User savedUser = userRepository.save(user);
        System.out.println("Сохранен читатель с id " + savedUser.getId());
    }

    private static void giveBook() throws DbException {
        System.out.println("Введите id читателя: ");
        int userId = scanner.nextInt();
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            System.out.println("Такого пользователя нет.");
            return;
        }
        User user = optionalUser.get();

        System.out.println("Введите id книги: ");
        int bookId = scanner.nextInt();
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if(optionalBook.isEmpty()) {
            System.out.println("Такой книги нет.");
            return;
        }
        Book book = optionalBook.get();
        giveBook(user, book, LocalDate.now());
    }

    public static void giveBook(User user, Book book, LocalDate date) throws DbException {
        GivenBook givenBook = new GivenBook(null, user.getId(), book.getId(), date);
        GivenBook savedGivenBook = givenBookRepository.save(givenBook);
        System.out.println("Книга выдана. Id записи: " + savedGivenBook.getId());
    }

    private static void printBooks(List<Book> books) {
        for(Book book: books) {
            System.out.println(book);
        }
    }

    private static void printUsers(List<User> users) {
        for(User user: users) {
            System.out.println(user);
        }
    }

    private static void printGivenBooks(List<GivenBook> givenBooks) throws DbException {
        for(GivenBook givenBook: givenBooks) {
            Optional<Book> optionalBook = bookRepository.findById(givenBook.getBookId());
            Optional<User> optionalUser = userRepository.findById(givenBook.getUserId());
            if(optionalBook.isEmpty() || optionalUser.isEmpty()) {
                throw new DbException("Data corrupted");
            }
            Book book = optionalBook.get();
            User user = optionalUser.get();
            System.out.println("id: " + givenBook.getId() + ". Книга: "
                    + book.getName() + ". Читатель: " + user + ". Дата выдачи: "
                    + givenBook.getCreatedDate());
        }
    }
}
