const books = [
    { title: "Wiedźmin", author: "Andrzej Sapkowski", available: true },
    { title: "Harry Potter i Kamień Filozoficzny", author: "J.K. Rowling", available: true },
    { title: "Pan Tadeusz", author: "Adam Mickiewicz", available: true },
    { title: "Zbrodnia i kara", author: "Fiodor Dostojewski", available: true },
    { title: "Sto lat samotności", author: "Gabriel García Márquez", available: true },
    { title: "Mistrz i Małgorzata", author: "Michaił Bułhakow", available: true },
    { title: "1984", author: "George Orwell", available: true },
    { title: "Władca Pierścieni: Drużyna Pierścienia", author: "J.R.R. Tolkien", available: true },
    { title: "Mały Książę", author: "Antoine de Saint-Exupéry", available: true },
    { title: "Cień wiatru", author: "Carlos Ruiz Zafón", available: true }
];

async function addBooks() {
    for (const book of books) {
        try {
            const response = await fetch('/book', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(book)
            });
            if (!response.ok) {
                console.error(`Błąd przy dodawaniu książki: ${book.title}`);
            } else {
                console.log(`Dodano książkę: ${book.title}`);
            }
        } catch (err) {
            console.error(`Błąd sieci przy dodawaniu książki: ${book.title}`, err);
        }
    }
}

addBooks();
