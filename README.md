Ezt az amőba játékot szántam bemutatkozó projektnek, ahol is egy 20 évvel ezelőtti DOS-os **C++** programomat alakítottam át modern webalkalmazássá (**Java** backend és **JQuery**, **Javascript**, **CSS**, **HTML** frontend technológiák alkalmazásával).

A játékot itt nézheted meg működés közben : **[atomcomci.cloud](https://atomcomci.cloud)**

---

### Tervek

Tervben van még az AI kódjának optimalizálása, játékerejének erősítése, és ezzel egyidejűleg a gépi játékos majdani különböző erősségű szintjeihez tartozó külön ranglisták létrehozása. Ezen felül még tesztek írásával is adós vagyok.

### A kódról

A böngészőben játszható amőba REST API alapokra helyezett, Java backenddel kommunikáló webalkalmazás.

Mivel frontend keretrendszert nem ismerek (még), így a HTML, CSS, és Javascript (főképp JQuery) kód mondhatni szövegszerkesztővel készült, bár az IntelliJ fejlesztőkörnyezet azért sokat segített (a backend is ebben készült). A frontend reszponzív, böngészőből és mobilról egyaránt játszható.

A backend a szokásos controller-service-repository felépítésű, a különböző aktív játékokat egy GameManager nevű komponens fogja össze. A játékok azonosítására sessionoket használok, amint a kliens-oldal inaktívvá válik, a hozzá tartozó session lejárta után egy ütemezett komponens kitakarítja ezeket az inaktív játékokat a memóriából. Jelenleg az egyes játékok példányainak minimális memória igénye miatt nem tartom szükségesnek adatbázis használatát a különböző játékok aktuális állapotának tárolására.

A gépi játékos régi logikáját teljesen változatlanul hagytam, csak refaktoráltam a Clean Code elvek nyomán.

### A régi

![fiveinarow_old.png](assets/five-in-a-row-old.png)

*(A régi C++ forráskód megtalálható az assets mappában. Az idő tájt még nem tudtam mi a Clean Code, így nem is törekedtem erre. Kéretik ilyen szemmel nézni...)*

### Az új

![fiveinarownew.png](assets/five-in-a-row-new.png)
