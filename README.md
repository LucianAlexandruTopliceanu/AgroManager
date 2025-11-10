# AgroManager

`AgroManager` è un'applicazione desktop sviluppata in Java per la gestione e il tracciamento delle attività agricole. Fornisce strumenti per monitorare l'intero ciclo di vita delle colture, dalla gestione dei fornitori e dei terreni fino alla registrazione dei raccolti e all'analisi dei dati di produttività.

## Tecnologie Utilizzate

  * **Linguaggio:** Java (JDK 17+)
  * **Interfaccia Grafica:** JavaFX
  * **Database:** PostgreSQL
  * **Build Tool:** Gradle
  * **Containerizzazione:** Docker (per la configurazione opzionale del database)

## Getting Started

### Prerequisiti

  * Java JDK 17 o versione successiva.
  * Un server PostgreSQL in esecuzione (Vedi la sezione "Configurazione Database").
  * (Opzionale) Docker Desktop, se si sceglie l'Opzione 2 per il database.

### Build

Il progetto utilizza un wrapper Gradle, quindi non è richiesta un'installazione manuale di Gradle.

Per compilare il progetto e scaricare tutte le dipendenze, esegui il comando appropriato dalla root del progetto:

**Windows:**

```bash
./gradlew.bat build
```

**macOS/Linux:**

```bash
./gradlew build
```

## Configurazione Database

L'applicazione richiede un'istanza PostgreSQL in esecuzione. Ci sono due modi per configurare il database:

### Opzione 1: Setup Manuale (Consigliato)

Questo metodo fornisce il pieno controllo sull'istanza del database.

1.  Assicurati che un server PostgreSQL sia in esecuzione.
2.  Crea un nuovo database (es. `agromanager`).
3.  Connettiti al database appena creato ed esegui lo script `database_creation.sql` per creare l'intero schema (tabelle, chiavi, vincoli).
4.  (Opzionale) Esegui lo script `database_data.sql` per popolare il database con dati di esempio standard.
5.  Aggiorna le credenziali di connessione (URL, utente e password) nel file sorgente `src/main/java/ORM/DatabaseConnection.java` per farle corrispondere alla configurazione del tuo server.

### Opzione 2: Ripristino da Volume Docker

Il file `volume_agromanager.tar.gz` è un backup di un volume Docker che contiene il database già configurato e popolato.

## Esecuzione dell'Applicazione

Dopo aver configurato il database e compilato il progetto, puoi avviare l'applicazione usando il task `run` di Gradle:

**Windows:**

```bash
./gradlew.bat run
```

**macOS/Linux:**

```bash
./gradlew run
```

## Documentazione

Una relazione completa del progetto, che include l'analisi dei requisiti, i diagrammi UML, lo schema ER e le giustificazioni delle scelte di progettazione, è disponibile in questo repository:

  * **Documento Finale:** `Documentazione.pdf`
  * **Sorgenti LaTeX:** `documentazione_latex/`
