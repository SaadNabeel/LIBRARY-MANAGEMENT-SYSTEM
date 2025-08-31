import java.io.*;
import java.lang.reflect.Member;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

import java.util.*;

public class LibraryApp {

    private static final String DATA_FILE = "library.dat";

    private static final Scanner IN = new Scanner(System.in);

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {

        Library library = Library.load(DATA_FILE).orElseGet(Library::new);

        printlnHeader();

        boolean running = true;

        while (running) {

            printMenu();

            String choice = prompt("Choose an option").trim();

            try {

                switch (choice) {

                    case "1": addBookFlow(library); break;

                    case "2": listBooksFlow(library); break;

                    case "3": registerMemberFlow(library); break;

                    case "4": listMembersFlow(library); break;

                    case "5": borrowBookFlow(library); break;

                    case "6": returnBookFlow(library); break;

                    case "7": searchBooksFlow(library); break;

                    case "8": listActiveLoansFlow(library); break;

                    case "9": listOverdueFlow(library); break;

                    case "0":

                        library.save(DATA_FILE);

                        System.out.println("Data saved. Exiting. Goodbye.");

                        running = false;
                        break;

                    default:

                        System.out.println("Invalid option. Please try again.");

                }

            } catch (Exception ex) {

                System.out.println("Operation failed: " + ex.getMessage());

            }
        }
    }
}
private static void addBookFlow(Library library) {

    System.out.println("\n--- Add Book ---");

    String title = prompt("Title");

    String author = prompt("Author");

    String isbn = prompt("ISBN");

    int copies = promptInt("Total copies (integer >= 1)", 1, Integer.MAX_VALUE);

    Book book = library.addBook(title, author, isbn, copies);

    System.out.println("Book added with ID: " + book.getId());

}

private static void listBooksFlow(Library library) {

    System.out.println("\n--- Books ---");

    List<Book> books = library.listBooks();

    if (books.isEmpty()) {

        System.out.println("No books in catalog.");

        return;
    }

    books.forEach(b ->
        System.out.printf("ID=%d | \"%s\" by %s | ISBN=%s | Available=%d/%d%n",
            b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(),
            b.getCopiesAvailable(), b.getCopiesTotal()))
    ;
}
private static void registerMemberFlow(Library library) {

    System.out.println("\n--- Register Member ---");

    String name = prompt("Full name");

    String email = prompt("Email");

    Member m = library.registerMember(name, email);

    System.out.println("Member registered with ID: " + m.getId());

}

private static void listMembersFlow(Library library) {

    System.out.println("\n--- Members ---");

    List<Member> members = library.listMembers();

    if (members.isEmpty()) {

        System.out.println("No members registered.");

        return;

    }

    members.forEach(m ->
        System.out.printf("ID=%d | %s | %s%n", m.getId(), m.getName(), m.getEmail())
    );

}
private static void borrowBookFlow(Library library) {

    System.out.println("\n--- Borrow Book ---");

    int memberId = promptInt("Member ID", 1, Integer.MAX_VALUE);

    int bookId = promptInt("Book ID", 1, Integer.MAX_VALUE);

    int termDays = promptInt("Loan term in days (e.g., 14)", 1, 365);

    Loan loan = library.borrowBook(memberId, bookId, termDays);

    System.out.printf("Loan created. Due on %s. LoanID=%s%n", loan.getDueDate().format(DF),
            loan.getLoanKey());

}

private static void returnBookFlow(Library library) {

    System.out.println("\n--- Return Book ---");

    String loanKey = prompt("Enter Loan ID (shown at borrow time; format M<memberId>-B<bookId>-<yyyyMMdd>)");

    library.returnBook(loanKey);

    System.out.println("Return processed.");

}
private static void searchBooksFlow(Library library) {

    System.out.println("\n--- Search Books ---");

    String q = prompt("Search text (title/author/ISBN)");

    List<Book> results = library.searchBooks(q);

    if (results.isEmpty()) {

        System.out.println("No matches.");
        return;

    }

    results.forEach(b ->
        System.out.printf("ID=%d | \"%s\" by %s | ISBN=%s | Available=%d/%d%n",
            b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(),
            b.getCopiesAvailable(), b.getCopiesTotal())
    );

}

private static void listActiveLoansFlow(Library library) {

    System.out.println("\n--- Active Loans ---");

    List<Loan> loans = library.listActiveLoans();

    if (loans.isEmpty()) {
        System.out.println("No active loans.");
        return;
    }

    loans.forEach(l -> {
        Book b = library.getBook(l.getBookId()).orElse(null);
        Member m = library.getMember(l.getMemberId()).orElse(null);

        String title = b != null ? b.getTitle() : "(book missing)";
        String name = m != null ? m.getName() : "(member missing)";

        System.out.printf("LoanID=%s | \"%s\" -> %s | Loaned=%s | Due=%s%n",
                l.getLoanKey(), title, name,
                l.getLoanDate().format(DF),
                l.getDueDate().format(DF));
    });
}
private static void listOverdueFlow(Library library) {

    System.out.println("\n--- Overdue Loans ---");

    List<Loan> loans = library.listOverdueLoans();

    if (loans.isEmpty()) {
        System.out.println("No overdue loans.");
        return;
    }

    loans.forEach(l -> {
        Book b = library.getBook(l.getBookId()).orElse(null);
        Member m = library.getMember(l.getMemberId()).orElse(null);

        String title = b != null ? b.getTitle() : "(book missing)";
        String name = m != null ? m.getName() : "(member missing)";

        long overdueDays = l.overdueDays();

        System.out.printf("LoanID=%s | \"%s\" -> %s | Due=%s | Overdue by %d day(s)%n",
                l.getLoanKey(), title, name,
                l.getDueDate().format(DF), overdueDays);
    });
}




