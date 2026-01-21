## 1. Mitä tekoäly teki hyvin?
- Tekoäly pystyi generoimaan MVP-tasoisen API:n nopeasti annettujen ohjeiden perusteella.
- Se noudatti pääosin annettuja teknisiä vaatimuksia ja rakensi toimivan perusrakenteen (controllerit, endpointit, peruslogiikan).
- Tekoäly nopeutti kehitystä erityisesti boilerplate-koodin ja toistuvien rakenteiden osalta.

## 2. Mitä tekoäly teki huonosti?
- Pitkän chatin aikana tekoäly alkoi hallusinoida ja menetti osittain kontekstin. Tämä aiheutti virheellisiä oletuksia ja epäjohdonmukaista koodia. Ongelma ratkesi tyhjentämällä kontekstin (esim. Claudessa `/clear`-komennolla).
- Tekoälyn generoima koodi ei ollut kaikilta osin RESTful-periaatteiden mukaista.
- Ohjeiden vajavaisuuden vuoksi tekoäly teki omia oletuksia, jotka eivät vastanneet projektin todellisia tarpeita (esimerkiksi HTTP-metodien valinta).

## 3. Mitkä olivat tärkeimmät parannukset, jotka teit tekoälyn tuottamaan koodiin ja miksi?
- Korjasin tekoälyn luomat controller-rajapinnat noudattamaan paremmin RESTful-periaatteita.
- Muutin `DELETE`-endpointom `PATCH`-endpoitiksi, koska entiteettiä ei poisteta tietokannasta, vaan ne merkitään passiivisiksi (soft delete).
- Korjasin tekoälyn generoimia testejä, jotta ne vastasivat oikein sovelluksen todellista toimintaa ja liiketoimintalogiikkaa.
