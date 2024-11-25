1. Singleton

Utworzenie tylko jednej instancji  LibraryCatalog umożliwia 
dzałanie na jedych listach książek i użytkowników.

2. Adapter

Różne formaty danych mogą być przekształcone do standardowego formatu obsługiwanego przez LibraryCatalog.
Dane mogą być zapisywane do różnych formatów, co ułatwia zarządzanie biblioteką.

3. Factory

Tworzenie różnych typów użytkowników za pomocą fabryki UserFactory.
Fabryka umożliwia elastyczne dodawanie nowych typów użytkowników.

4. Observer

Umożliwia monitorowanie książek przez użytkowników. 
Umożliwia proste tworzenie powiadomień o zmianie statusu ksiąki.

5. Facade

Użycie klasy LibraryInterface do uproszczenia interakcji z systemem bibliotecznym.
Wykonanie operacji takich jak dodawanie książki, wypożyczanie i zwracanie, bez potrzeby bezpośredniej interakcji z podsystemami.

6. Iterator

Iteracja po użytkownikach i książkach za pomocą iteratora zdefiniowanego w LibraryCatalog.
Wyświetlenie wszystkich zarejestrowanych użytkowników oraz książek w katalogu.
