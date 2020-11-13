# Twoj-ogrod

This app gives you some useful information about your garden.

It takes your actual coordinates, sends them to the weather API (Weatherbit.io) and gets some info about temperature, wind and soil moisture.
You can save your position, so it won't change when you go somewhere else.

You can choose your plants from a list by clicking the green plus button on the main screen (profileactivity).
The list is provided by firebase realtime database, which is built by me, based on informations found on internet.
Database also contains some information about plants requirements, thanks to which application can print simple hints like "Irrigate!" 



Projekt aplikacji pomagającej w uprawie ogrodu

Aplikacja pobiera pozycję na podstawie GPS i przesyła ją do API pogodowego (weatherbit.io), zwracjąc aktualną temperaturę, wilgotność gleby i wiatr.
Pozycję można zapisać, ponieważ ogród nie przemieszcza się razem z użytkownikiem :)

Rośliny wybiera się z listy dostępnej po kliknięciu w zielony plusik na głównym ekranie(profileactivity).
Lista tworzy się automatycznie na podstawie danych z bazy znajdującej się na platformie firebase. 
Baza jest zbudowana przeze mnie, na podstawie danych ogólnodostępnych w internecie.
Baza danych zawiera także wymagania co do temperatury oraz wilgotności gleby dla każdej rośliny.
Porównanie danych z API oraz danych z bazy pozwala na wyświetlanie prostych wskazówek, jak np. "Podlej!"
