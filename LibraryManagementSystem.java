
import java.io.*;
import java.util.Scanner;

public class LibraryManagementSystem {

    static final String FILE_NAME = "books.txt";
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;

        do {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. Search Book");
            System.out.println("4. Issue Book");
            System.out.println("5. Return Book");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            while (!sc.hasNextInt()) {
                System.out.print("Enter a valid number: ");
                sc.next();
            }

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    viewBooks();
                    break;
                case 3:
                    searchBook();
                    break;
                case 4:
                    updateStatus("Issued");
                    break;
                case 5:
                    updateStatus("Available");
                    break;
                case 6:
                    System.out.println("Thank you for using the system!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 6);

        sc.close();
    }

    static void addBook() {
        try {
            System.out.print("Enter Book ID: ");
            String id = sc.nextLine().trim();

            if (id.isEmpty() || findBook(id) != null) {
                System.out.println("Invalid or duplicate Book ID.");
                return;
            }

            System.out.print("Enter Book Title: ");
            String title = sc.nextLine().trim();

            System.out.print("Enter Author Name: ");
            String author = sc.nextLine().trim();

            if (title.isEmpty() || author.isEmpty()
                    || title.contains("|") || author.contains("|")) {
                System.out.println("Invalid book details.");
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(
                    new FileWriter(FILE_NAME, true))) {
                bw.write(id + "|" + title + "|" + author + "|Available");
                bw.newLine();
            }

            System.out.println("Book added successfully!");

        } catch (IOException e) {
            System.out.println("Error saving book: " + e.getMessage());
        }
    }

    static void viewBooks() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No books found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(
                new FileReader(FILE_NAME))) {
            String line;
            boolean found = false;

            while ((line = br.readLine()) != null) {
                String[] book = line.split("\\|", -1);

                if (book.length == 4) {
                    System.out.println(
                        "ID: " + book[0]
                        + " | Title: " + book[1]
                        + " | Author: " + book[2]
                        + " | Status: " + book[3]);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No books found.");
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    static String[] findBook(String id) {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return null;
        }

        try (BufferedReader br = new BufferedReader(
                new FileReader(FILE_NAME))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] book = line.split("\\|", -1);

                if (book.length == 4 && book[0].equals(id)) {
                    return book;
                }
            }
        } catch (IOException e) {
            System.out.println("Error searching file: " + e.getMessage());
        }

        return null;
    }

    static void searchBook() {
        System.out.print("Enter Book ID: ");
        String id = sc.nextLine().trim();

        String[] book = findBook(id);

        if (book != null) {
            System.out.println("Book Found!");
            System.out.println("ID: " + book[0]);
            System.out.println("Title: " + book[1]);
            System.out.println("Author: " + book[2]);
            System.out.println("Status: " + book[3]);
        } else {
            System.out.println("Book not found.");
        }
    }

    static void updateStatus(String newStatus) {
        System.out.print("Enter Book ID: ");
        String id = sc.nextLine().trim();

        File original = new File(FILE_NAME);

        if (!original.exists()) {
            System.out.println("No books found.");
            return;
        }

        File temp = new File("books_temp.txt");
        boolean found = false;

        try (BufferedReader br = new BufferedReader(
                    new FileReader(original));
             BufferedWriter bw = new BufferedWriter(
                    new FileWriter(temp))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] book = line.split("\\|", -1);

                if (book.length == 4 && book[0].equals(id)) {
                    found = true;

                    if (newStatus.equals("Issued")
                            && book[3].equals("Issued")) {
                        System.out.println("Book is already issued.");
                    } else if (newStatus.equals("Available")
                            && book[3].equals("Available")) {
                        System.out.println("Book is already available.");
                    } else {
                        book[3] = newStatus;
                        System.out.println("Book status updated to "
                                + newStatus + ".");
                    }

                    line = String.join("|", book);
                }

                bw.write(line);
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error updating file: " + e.getMessage());
            temp.delete();
            return;
        }

        if (!found) {
            System.out.println("Book not found.");
            temp.delete();
            return;
        }

        if (original.delete() && temp.renameTo(original)) {
            System.out.println("File updated successfully.");
        } else {
            System.out.println("Could not replace the book file.");
            System.out.println("Updated data may be in books_temp.txt.");
        }
    }
}
