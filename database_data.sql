
-- Pulizia dati esistenti con TRUNCATE CASCADE
TRUNCATE TABLE raccolto CASCADE;
TRUNCATE TABLE piantagione CASCADE;
TRUNCATE TABLE pianta CASCADE;
TRUNCATE TABLE zona CASCADE;
TRUNCATE TABLE stato_piantagione CASCADE;
TRUNCATE TABLE fornitore CASCADE;

-- Reset delle sequenze per ripartire da 1
ALTER SEQUENCE fornitore_id_seq RESTART WITH 1;
ALTER SEQUENCE pianta_id_seq RESTART WITH 1;
ALTER SEQUENCE zona_id_seq RESTART WITH 1;
ALTER SEQUENCE piantagione_id_seq RESTART WITH 1;
ALTER SEQUENCE raccolto_id_seq RESTART WITH 1;
ALTER SEQUENCE stato_piantagione_id_seq RESTART WITH 1;


INSERT INTO stato_piantagione (codice, descrizione, data_creazione, data_aggiornamento) VALUES
('ATTIVA', 'Piantagione attiva e in produzione', NOW(), NOW()),
('PREPARAZIONE', 'In fase di preparazione del terreno', NOW(), NOW()),
('CRESCITA', 'In fase di crescita vegetativa', NOW(), NOW()),
('MATURAZIONE', 'In fase di maturazione dei frutti', NOW(), NOW()),
('RACCOLTA', 'In fase di raccolta', NOW(), NOW()),
('RIPOSO', 'Terreno a riposo', NOW(), NOW()),
('RIMOSSA', 'Piantagione rimossa o terminata', NOW(), NOW());


INSERT INTO fornitore (nome, indirizzo, numero_telefono, email, partita_iva, data_creazione, data_aggiornamento) VALUES
('Vivai Rossi', 'Via delle Rose 15, 20100 Milano', '+39 02 1234567', 'info@vivairossi.it', 'IT12345678901', NOW(), NOW()),
('AgroSementi Sud', 'Corso Garibaldi 45, 80100 Napoli', '+39 081 9876543', 'vendite@agrosementi.it', 'IT98765432109', NOW(), NOW()),
('Piante Biologiche Verdi', 'Strada Provinciale 28, 50100 Firenze', '+39 055 5551234', 'contatti@pianteverdi.com', 'IT11223344556', NOW(), NOW()),
('Sementi del Nord', 'Via Torino 88, 10100 Torino', '+39 011 4445566', 'ordini@sementidinord.it', 'IT66778899001', NOW(), NOW()),
('Vivaio La Campagna', 'Via Emilia 122, 40100 Bologna', '+39 051 3332211', 'info@vivaiolacampagna.it', 'IT55443322110', NOW(), NOW());


INSERT INTO pianta (tipo, varieta, costo, note, fornitore_id, data_creazione, data_aggiornamento) VALUES
-- Pomodori
('Pomodoro', 'San Marzano', 2.50, 'Varietà tradizionale italiana per salse', 1, NOW(), NOW()),
('Pomodoro', 'Ciliegino', 3.00, 'Ideale per insalate, molto produttivo', 2, NOW(), NOW()),
('Pomodoro', 'Cuore di Bue', 3.50, 'Grandi dimensioni, polpa carnosa', 3, NOW(), NOW()),
('Pomodoro', 'Datterino', 2.80, 'Dolce e resistente', 1, NOW(), NOW()),

-- Zucchine
('Zucchina', 'Romanesca', 2.00, 'Classica varietà italiana', 2, NOW(), NOW()),
('Zucchina', 'Striata d''Italia', 2.20, 'Buccia striata, sapore delicato', 3, NOW(), NOW()),
('Zucchina', 'Tonda di Nizza', 2.50, 'Forma tonda, ideale per ripieni', 4, NOW(), NOW()),

-- Melanzane
('Melanzana', 'Violetta Lunga', 2.30, 'Classica melanzana viola allungata', 1, NOW(), NOW()),
('Melanzana', 'Tonda di Firenze', 2.60, 'Forma tondeggiante, polpa compatta', 3, NOW(), NOW()),

-- Peperoni
('Peperone', 'Quadrato Giallo', 2.70, 'Dolce e croccante', 2, NOW(), NOW()),
('Peperone', 'Corno di Toro Rosso', 2.80, 'Allungato, molto dolce', 4, NOW(), NOW()),
('Peperone', 'Friggitello Verde', 2.40, 'Piccante leggero, da friggere', 5, NOW(), NOW()),

-- Lattuga
('Lattuga', 'Romana', 1.50, 'Foglie croccanti, cuore compatto', 3, NOW(), NOW()),
('Lattuga', 'Iceberg', 1.60, 'Molto croccante, lunga conservazione', 5, NOW(), NOW()),
('Lattuga', 'Lollo Rosso', 1.80, 'Foglie rosse arricciate', 2, NOW(), NOW()),

-- Insalate miste
('Rucola', 'Selvatica', 1.20, 'Sapore intenso e piccante', 4, NOW(), NOW()),
('Basilico', 'Genovese', 1.80, 'Classico basilico profumato', 1, NOW(), NOW()),
('Prezzemolo', 'Riccio', 1.00, 'Aroma intenso', 5, NOW(), NOW()),

-- Ortaggi da radice
('Carota', 'Nantese', 1.50, 'Cilindrica, dolce', 2, NOW(), NOW()),
('Ravanello', 'Cherry Belle', 1.20, 'Rosso tondo, croccante', 3, NOW(), NOW());


INSERT INTO zona (nome, dimensione, tipo_terreno, data_creazione, data_aggiornamento) VALUES
('Campo Nord', 5.5, 'Argilloso', NOW(), NOW()),
('Campo Sud', 4.2, 'Sabbioso', NOW(), NOW()),
('Zona Est - Serra 1', 2.0, 'Franco', NOW(), NOW()),
('Zona Est - Serra 2', 2.0, 'Franco', NOW(), NOW()),
('Campo Ovest', 6.0, 'Limoso', NOW(), NOW()),
('Orto Biologico', 1.5, 'Franco-sabbioso', NOW(), NOW()),
('Serra Principale', 3.5, 'Terreno misto', NOW(), NOW()),
('Campo Sperimentale', 1.0, 'Argilloso-limoso', NOW(), NOW());


INSERT INTO piantagione (quantita_pianta, messa_a_dimora, id_pianta, id_zona, id_stato_piantagione, data_creazione, data_aggiornamento) VALUES
-- Piantagioni attive - primavera 2024
(150, '2024-03-15', 1, 1, 5, NOW(), NOW()),  -- San Marzano in Campo Nord - in raccolta
(200, '2024-03-20', 2, 3, 5, NOW(), NOW()),  -- Ciliegino in Serra 1 - in raccolta
(120, '2024-04-01', 5, 2, 4, NOW(), NOW()),  -- Zucchina Romanesca in Campo Sud - maturazione
(180, '2024-04-10', 8, 1, 4, NOW(), NOW()),  -- Melanzana Violetta in Campo Nord - maturazione

-- Piantagioni estate 2024
(100, '2024-05-15', 10, 4, 5, NOW(), NOW()), -- Peperone Quadrato in Serra 2 - in raccolta
(90, '2024-05-20', 11, 5, 5, NOW(), NOW()),  -- Corno di Toro in Campo Ovest - in raccolta
(250, '2024-06-01', 13, 6, 5, NOW(), NOW()), -- Lattuga Romana in Orto Biologico - in raccolta
(200, '2024-06-10', 16, 7, 4, NOW(), NOW()), -- Rucola in Serra Principale - maturazione

-- Piantagioni autunno 2024
(160, '2024-08-15', 3, 1, 3, NOW(), NOW()),  -- Cuore di Bue in Campo Nord - crescita
(140, '2024-09-01', 6, 2, 3, NOW(), NOW()),  -- Zucchina Striata in Campo Sud - crescita
(120, '2024-09-15', 17, 3, 3, NOW(), NOW()), -- Basilico in Serra 1 - crescita
(180, '2024-10-01', 19, 6, 2, NOW(), NOW()), -- Carota in Orto Biologico - preparazione

-- Piantagioni inverno 2024-2025
(100, '2024-11-01', 14, 4, 1, NOW(), NOW()), -- Iceberg in Serra 2 - attiva
(80, '2024-11-15', 20, 7, 1, NOW(), NOW()),  -- Ravanello in Serra Principale - attiva
(150, '2024-12-01', 7, 3, 1, NOW(), NOW()),  -- Zucchina Tonda in Serra 1 - attiva

-- Piantagioni sperimentali
(50, '2024-07-01', 12, 8, 3, NOW(), NOW()),  -- Friggitello in Campo Sperimentale - crescita
(60, '2024-08-01', 15, 8, 3, NOW(), NOW()),  -- Lollo Rosso in Campo Sperimentale - crescita

-- Piantagioni rimosse (ciclo completato)
(200, '2024-01-15', 4, 5, 7, NOW(), NOW()),  -- Datterino in Campo Ovest - rimossa
(180, '2024-02-01', 9, 2, 7, NOW(), NOW());  -- Melanzana Tonda in Campo Sud - rimossa


INSERT INTO raccolto (data_raccolto, quantita_kg, note, piantagione_id, data_creazione, data_aggiornamento) VALUES
-- Raccolti San Marzano (Piantagione ID 1 - messa a dimora: 2024-03-15)
('2024-06-15', 45.5, 'Prima raccolta, ottima qualità', 1, NOW(), NOW()),
('2024-06-25', 52.3, 'Raccolta principale, frutti perfetti', 1, NOW(), NOW()),
('2024-07-05', 48.7, 'Raccolta tardiva, alcuni frutti troppo maturi', 1, NOW(), NOW()),
('2024-07-15', 38.2, 'Raccolta finale', 1, NOW(), NOW()),

-- Raccolti Ciliegino (Piantagione ID 2 - messa a dimora: 2024-03-20)
('2024-06-20', 38.5, 'Ottima produzione in serra', 2, NOW(), NOW()),
('2024-06-30', 42.8, 'Picco produttivo', 2, NOW(), NOW()),
('2024-07-10', 45.2, 'Raccolta abbondante', 2, NOW(), NOW()),
('2024-07-20', 41.5, 'Produzione sostenuta', 2, NOW(), NOW()),
('2024-07-30', 39.8, 'Ancora buona produzione', 2, NOW(), NOW()),
('2024-08-10', 35.6, 'Produzione in calo', 2, NOW(), NOW()),

-- Raccolti Zucchina Romanesca (Piantagione ID 3 - messa a dimora: 2024-04-01)
('2024-06-01', 55.8, 'Prima raccolta, zucchine piccole e tenere', 3, NOW(), NOW()),
('2024-06-08', 68.3, 'Ottima produzione settimanale', 3, NOW(), NOW()),
('2024-06-15', 72.5, 'Picco produttivo', 3, NOW(), NOW()),
('2024-06-22', 69.8, 'Produzione costante', 3, NOW(), NOW()),
('2024-06-29', 65.4, 'Ancora buona produzione', 3, NOW(), NOW()),
('2024-07-06', 58.2, 'Produzione in calo', 3, NOW(), NOW()),

-- Raccolti Melanzana Violetta (Piantagione ID 4 - messa a dimora: 2024-04-10)
('2024-07-01', 42.3, 'Prime melanzane, dimensioni medie', 4, NOW(), NOW()),
('2024-07-11', 51.7, 'Ottima raccolta', 4, NOW(), NOW()),
('2024-07-21', 48.5, 'Produzione sostenuta', 4, NOW(), NOW()),
('2024-07-31', 45.8, 'Raccolta tardiva', 4, NOW(), NOW()),

-- Raccolti Peperone Quadrato (Piantagione ID 5 - messa a dimora: 2024-05-15)
('2024-07-25', 35.2, 'Prima raccolta, peperoni gialli brillanti', 5, NOW(), NOW()),
('2024-08-04', 42.8, 'Ottima qualità', 5, NOW(), NOW()),
('2024-08-14', 46.5, 'Picco produttivo', 5, NOW(), NOW()),
('2024-08-24', 43.2, 'Produzione costante', 5, NOW(), NOW()),
('2024-09-03', 38.7, 'Raccolta tardiva', 5, NOW(), NOW()),

-- Raccolti Corno di Toro (Piantagione ID 6 - messa a dimora: 2024-05-20)
('2024-07-30', 32.5, 'Peperoni dolcissimi', 6, NOW(), NOW()),
('2024-08-09', 38.9, 'Ottima raccolta', 6, NOW(), NOW()),
('2024-08-19', 41.3, 'Produzione massima', 6, NOW(), NOW()),
('2024-08-29', 36.8, 'Ancora buona produzione', 6, NOW(), NOW()),

-- Raccolti Lattuga Romana (Piantagione ID 7 - messa a dimora: 2024-06-01)
('2024-07-20', 28.5, 'Prime cesate, foglie croccanti', 7, NOW(), NOW()),
('2024-07-30', 32.8, 'Raccolta principale', 7, NOW(), NOW()),
('2024-08-09', 30.2, 'Seconda raccolta', 7, NOW(), NOW()),
('2024-08-19', 26.5, 'Raccolta finale', 7, NOW(), NOW()),

-- Raccolti Rucola (Piantagione ID 8 - messa a dimora: 2024-06-10)
('2024-07-25', 15.8, 'Prima raccolta, foglie tenere', 8, NOW(), NOW()),
('2024-08-04', 18.5, 'Ricrescita abbondante', 8, NOW(), NOW()),
('2024-08-14', 17.2, 'Terza raccolta', 8, NOW(), NOW()),
('2024-08-24', 16.8, 'Produzione continua', 8, NOW(), NOW()),

-- Raccolti Cuore di Bue (Piantagione ID 9 - messa a dimora: 2024-08-15)
('2024-10-01', 42.5, 'Prima raccolta autunnale', 9, NOW(), NOW()),
('2024-10-11', 48.3, 'Ottima produzione', 9, NOW(), NOW()),
('2024-10-21', 45.7, 'Raccolta tardiva', 9, NOW(), NOW()),

-- Raccolti Zucchina Striata (Piantagione ID 10 - messa a dimora: 2024-09-01)
('2024-10-15', 52.8, 'Prime zucchine striate', 10, NOW(), NOW()),
('2024-10-25', 58.5, 'Ottima produzione', 10, NOW(), NOW()),
('2024-11-04', 54.2, 'Raccolta continua', 10, NOW(), NOW()),

-- Raccolti Basilico (Piantagione ID 11 - messa a dimora: 2024-09-15)
('2024-10-20', 8.5, 'Prima raccolta foglie', 11, NOW(), NOW()),
('2024-11-01', 10.2, 'Seconda raccolta', 11, NOW(), NOW()),
('2024-11-15', 9.8, 'Terza raccolta', 11, NOW(), NOW()),

-- Piantagione ID 12 (Carota - messa a dimora: 2024-10-01) - ancora in preparazione, nessun raccolto

-- Raccolti Iceberg (Piantagione ID 13 - messa a dimora: 2024-11-01)
('2024-12-20', 25.3, 'Lattuga Iceberg - raccolta invernale in serra', 13, NOW(), NOW()),
('2025-01-05', 27.8, 'Seconda raccolta invernale', 13, NOW(), NOW()),

-- Raccolti Ravanello (Piantagione ID 14 - messa a dimora: 2024-11-15)
('2024-12-30', 12.8, 'Ravanelli croccanti', 14, NOW(), NOW()),
('2025-01-10', 14.2, 'Ravanelli - seconda raccolta', 14, NOW(), NOW()),

-- Raccolti Zucchina Tonda (Piantagione ID 15 - messa a dimora: 2024-12-01)
('2025-01-15', 22.5, 'Zucchina Tonda - produzione serra', 15, NOW(), NOW()),

-- Raccolti Friggitello (Piantagione ID 16 - messa a dimora: 2024-07-01)
('2024-09-05', 18.5, 'Peperoncini da friggere', 16, NOW(), NOW()),
('2024-09-20', 22.3, 'Seconda raccolta', 16, NOW(), NOW()),

-- Raccolti Lollo Rosso (Piantagione ID 17 - messa a dimora: 2024-08-01)
('2024-09-15', 15.8, 'Prima raccolta foglie rosse', 17, NOW(), NOW()),
('2024-09-30', 18.2, 'Seconda raccolta', 17, NOW(), NOW()),

-- Raccolti Datterino (Piantagione ID 18 - messa a dimora: 2024-01-15, RIMOSSA)
('2024-03-20', 52.3, 'Ottima produzione primaverile', 18, NOW(), NOW()),
('2024-03-30', 58.7, 'Picco produttivo', 18, NOW(), NOW()),
('2024-04-10', 55.2, 'Produzione costante', 18, NOW(), NOW()),
('2024-04-20', 48.5, 'Produzione in calo', 18, NOW(), NOW()),
('2024-04-30', 42.8, 'Raccolta finale prima rimozione', 18, NOW(), NOW()),

-- Raccolti Melanzana Tonda (Piantagione ID 19 - messa a dimora: 2024-02-01, RIMOSSA)
('2024-04-15', 38.5, 'Prime melanzane tonde', 19, NOW(), NOW()),
('2024-04-25', 45.2, 'Ottima produzione', 19, NOW(), NOW()),
('2024-05-05', 42.8, 'Raccolta tardiva', 19, NOW(), NOW());

-- Query di verifica
SELECT 'Fornitori inseriti:' as tipo, COUNT(*) as totale FROM fornitore
UNION ALL
SELECT 'Piante inserite:', COUNT(*) FROM pianta
UNION ALL
SELECT 'Zone inserite:', COUNT(*) FROM zona
UNION ALL
SELECT 'Stati piantagione:', COUNT(*) FROM stato_piantagione
UNION ALL
SELECT 'Piantagioni inserite:', COUNT(*) FROM piantagione
UNION ALL
SELECT 'Raccolti inseriti:', COUNT(*) FROM raccolto;
