# Oletukset

Tämä dokumentti listaa projektin toteutuksessa tehdyt oletukset, koska tehtävänannossa ei määritelty kaikkia yksityiskohtia.

## Projektin tavoite

- **MVP-toteutus**: Tehtävänä oli toteuttaa yksinkertainen kokoushuoneiden varausrajapinta (API). 
- **Tavoitteena** oli toimiva MVP (Minimum Viable Product) käyttäjäpalautteen keräämistä varten
- **Jatkokehitys huomioitu**: Arkkitehtuuri on suunniteltu siten, että sovellus on helppo laajentaa:
  - Siirtyminen oikeaan tietokantaan (PostgreSQL, MySQL) vaatii vain konfiguraatiomuutoksen
  - Puuttuvat endpointit (esim. huoneiden CRUD) voidaan lisätä olemassa olevaan rakenteeseen
  - Spring Security -integraatio autentikointia ja auktorisointia varten on suoraviivaista lisätä

## Huoneet

- **Valmiit huoneet**: Sovellus luo käynnistyessä 5 kokoushuonetta automaattisesti (DataInitializer)
- **Ei huoneiden hallintaa**: Käyttäjät eivät voi lisätä, muokata tai poistaa huoneita API:n kautta

## Varaukset

- **Pehmeä poisto**: Varauksen peruutus ei poista varausta tietokannasta, vaan muuttaa sen statuksen CANCELED-tilaan. Myöhemmin voidaan toteuttaa ajastettu siivoustyö, joka poistaa vanhat peruutetut varaukset, jos tämä tulee käyttäjävaatimukseksi
- **Vain aktiiviset varaukset**: GET-endpoint palauttaa vain BOOKED-statuksella olevat varaukset, ei peruutettuja
- **Varaajan nimi**: Varaus sisältää varaajan nimen (bookerName), joka lähetetään pyynnön mukana

## Aikavyöhykkeet

- **Yksi aikavyöhyke**: Sovellus olettaa kaikkien aikojen olevan samassa aikavyöhykkeessä (LocalDateTime)
- **Ei toistuvia varauksia**: Varaukset ovat yksittäisiä, ei tukea toistuvuudelle (esim. "joka maanantai")

## Tietoturva

- **Ei autentikointia**: API on avoin kaikille ilman kirjautumista
- **Ei auktorisointia**: Kuka tahansa voi peruuttaa minkä tahansa varauksen

## Tietokanta

- **Muistinvarainen tietokanta**: H2 in-memory -tietokanta, joka tyhjenee sovelluksen uudelleenkäynnistyksessä
- **Automaattinen skeeman luonti**: Hibernate luo taulut automaattisesti (ddl-auto=create-drop)

## API-suunnittelu

- **REST-periaatteet**: Flat URL-rakenne (`/bookings`) ilman sisäkkäisiä resursseja
- **PATCH peruutukseen**: Varauksen peruutus käyttää PATCH-metodia (osittainen päivitys statukseen) eikä DELETE-metodia
- **Validointi rajapinnassa**: Syötteiden validointi tapahtuu sekä DTO-tasolla (@Valid) että palvelutasolla
