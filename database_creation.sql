
DROP TABLE IF EXISTS raccolto CASCADE;
DROP TABLE IF EXISTS piantagione CASCADE;
DROP TABLE IF EXISTS pianta CASCADE;
DROP TABLE IF EXISTS zona CASCADE;
DROP TABLE IF EXISTS stato_piantagione CASCADE;
DROP TABLE IF EXISTS fornitore CASCADE;


CREATE TABLE fornitore (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    indirizzo VARCHAR(100) NOT NULL,
    numero_telefono VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    partita_iva VARCHAR(100),
    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_aggiornamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT uk_fornitore_nome UNIQUE (nome),
    CONSTRAINT uk_fornitore_email UNIQUE (email),
    CONSTRAINT chk_fornitore_email CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Indici per fornitore
CREATE INDEX idx_fornitore_nome ON fornitore(nome);
CREATE INDEX idx_fornitore_email ON fornitore(email);

-- Commenti tabella fornitore
COMMENT ON TABLE fornitore IS 'Tabella dei fornitori di piante e materiali agricoli';
COMMENT ON COLUMN fornitore.nome IS 'Nome del fornitore o azienda';
COMMENT ON COLUMN fornitore.partita_iva IS 'Partita IVA del fornitore';


CREATE TABLE stato_piantagione (
    id SERIAL PRIMARY KEY,
    codice VARCHAR(50) NOT NULL,
    descrizione VARCHAR(100) NOT NULL,
    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_aggiornamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT uk_stato_piantagione_codice UNIQUE (codice)
);

-- Indici per stato_piantagione
CREATE INDEX idx_stato_piantagione_codice ON stato_piantagione(codice);

-- Commenti tabella stato_piantagione
COMMENT ON TABLE stato_piantagione IS 'Stati possibili di una piantagione (ATTIVA, PREPARAZIONE, CRESCITA, etc.)';
COMMENT ON COLUMN stato_piantagione.codice IS 'Codice univoco dello stato (es. ATTIVA, RIMOSSA)';


CREATE TABLE pianta (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL,
    varieta VARCHAR(100) NOT NULL,
    costo DECIMAL(10,2),
    note TEXT,
    fornitore_id INTEGER NOT NULL,
    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_aggiornamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_pianta_fornitore FOREIGN KEY (fornitore_id)
        REFERENCES fornitore(id) ON DELETE RESTRICT ON UPDATE CASCADE,

    -- Constraints
    CONSTRAINT chk_pianta_costo CHECK (costo IS NULL OR costo >= 0)
);

-- Indici per pianta
CREATE INDEX idx_pianta_tipo ON pianta(tipo);
CREATE INDEX idx_pianta_varieta ON pianta(varieta);
CREATE INDEX idx_pianta_fornitore ON pianta(fornitore_id);
CREATE INDEX idx_pianta_tipo_varieta ON pianta(tipo, varieta);

-- Commenti tabella pianta
COMMENT ON TABLE pianta IS 'Catalogo delle varietà di piante disponibili per la coltivazione';
COMMENT ON COLUMN pianta.tipo IS 'Tipo di pianta (es. Pomodoro, Zucchina)';
COMMENT ON COLUMN pianta.varieta IS 'Varietà specifica (es. San Marzano, Ciliegino)';
COMMENT ON COLUMN pianta.costo IS 'Costo unitario della pianta in euro';


CREATE TABLE zona (
    id SERIAL PRIMARY KEY,
    nome VARCHAR NOT NULL,
    dimensione DOUBLE PRECISION NOT NULL,
    tipo_terreno VARCHAR(100) NOT NULL,
    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_aggiornamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT uk_zona_nome UNIQUE (nome),
    CONSTRAINT chk_zona_dimensione CHECK (dimensione > 0)
);

-- Indici per zona
CREATE INDEX idx_zona_nome ON zona(nome);
CREATE INDEX idx_zona_tipo_terreno ON zona(tipo_terreno);

-- Commenti tabella zona
COMMENT ON TABLE zona IS 'Aree geografiche di coltivazione (campi, serre, orti)';
COMMENT ON COLUMN zona.dimensione IS 'Dimensione della zona in ettari';
COMMENT ON COLUMN zona.tipo_terreno IS 'Tipologia di terreno (Argilloso, Sabbioso, Franco, etc.)';


CREATE TABLE piantagione (
    id SERIAL PRIMARY KEY,
    quantita_pianta INTEGER NOT NULL,
    messa_a_dimora DATE NOT NULL,
    id_pianta INTEGER NOT NULL,
    id_zona INTEGER NOT NULL,
    id_stato_piantagione INTEGER NOT NULL,
    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_aggiornamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_piantagione_pianta FOREIGN KEY (id_pianta)
        REFERENCES pianta(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_piantagione_zona FOREIGN KEY (id_zona)
        REFERENCES zona(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_piantagione_stato FOREIGN KEY (id_stato_piantagione)
        REFERENCES stato_piantagione(id) ON DELETE RESTRICT ON UPDATE CASCADE,

    -- Constraints
    CONSTRAINT chk_piantagione_quantita CHECK (quantita_pianta > 0),
    CONSTRAINT chk_piantagione_data CHECK (messa_a_dimora <= CURRENT_DATE)
);

-- Indici per piantagione
CREATE INDEX idx_piantagione_pianta ON piantagione(id_pianta);
CREATE INDEX idx_piantagione_zona ON piantagione(id_zona);
CREATE INDEX idx_piantagione_stato ON piantagione(id_stato_piantagione);
CREATE INDEX idx_piantagione_messa_dimora ON piantagione(messa_a_dimora);
CREATE INDEX idx_piantagione_zona_pianta ON piantagione(id_zona, id_pianta);
CREATE INDEX idx_piantagione_stato_zona ON piantagione(id_stato_piantagione, id_zona);

-- Commenti tabella piantagione
COMMENT ON TABLE piantagione IS 'Piantagioni attive con informazioni su pianta, zona e stato';
COMMENT ON COLUMN piantagione.quantita_pianta IS 'Numero di piante nella piantagione';
COMMENT ON COLUMN piantagione.messa_a_dimora IS 'Data di piantumazione';


CREATE TABLE raccolto (
    id SERIAL PRIMARY KEY,
    data_raccolto DATE NOT NULL,
    quantita_kg DECIMAL(10,2) NOT NULL,
    note TEXT,
    piantagione_id INTEGER NOT NULL,
    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_aggiornamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_raccolto_piantagione FOREIGN KEY (piantagione_id)
        REFERENCES piantagione(id) ON DELETE RESTRICT ON UPDATE CASCADE,

    -- Constraints
    CONSTRAINT chk_raccolto_quantita CHECK (quantita_kg > 0),
    CONSTRAINT chk_raccolto_data CHECK (data_raccolto <= CURRENT_DATE)
);

-- Indici per raccolto
CREATE INDEX idx_raccolto_piantagione ON raccolto(piantagione_id);
CREATE INDEX idx_raccolto_data ON raccolto(data_raccolto);
CREATE INDEX idx_raccolto_data_desc ON raccolto(data_raccolto DESC);
CREATE INDEX idx_raccolto_piantagione_data ON raccolto(piantagione_id, data_raccolto);

-- Commenti tabella raccolto
COMMENT ON TABLE raccolto IS 'Registrazione dei raccolti effettuati per ogni piantagione';
COMMENT ON COLUMN raccolto.quantita_kg IS 'Quantità raccolta in chilogrammi';
COMMENT ON COLUMN raccolto.note IS 'Note sulla qualità del raccolto o altre osservazioni';



-- Funzione generica per aggiornare data_aggiornamento
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.data_aggiornamento = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger per fornitore
CREATE TRIGGER trg_fornitore_update_timestamp
    BEFORE UPDATE ON fornitore
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- Trigger per stato_piantagione
CREATE TRIGGER trg_stato_piantagione_update_timestamp
    BEFORE UPDATE ON stato_piantagione
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- Trigger per pianta
CREATE TRIGGER trg_pianta_update_timestamp
    BEFORE UPDATE ON pianta
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- Trigger per zona
CREATE TRIGGER trg_zona_update_timestamp
    BEFORE UPDATE ON zona
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- Trigger per piantagione
CREATE TRIGGER trg_piantagione_update_timestamp
    BEFORE UPDATE ON piantagione
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- Trigger per raccolto
CREATE TRIGGER trg_raccolto_update_timestamp
    BEFORE UPDATE ON raccolto
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();



-- Funzione per validare che la data raccolto sia dopo la messa a dimora
CREATE OR REPLACE FUNCTION validate_raccolto_date()
RETURNS TRIGGER AS $$
DECLARE
    v_messa_a_dimora DATE;
BEGIN
    -- Recupera la data di messa a dimora della piantagione
    SELECT messa_a_dimora INTO v_messa_a_dimora
    FROM piantagione
    WHERE id = NEW.piantagione_id;

    -- Verifica che la data del raccolto sia dopo la messa a dimora
    IF NEW.data_raccolto < v_messa_a_dimora THEN
        RAISE EXCEPTION 'La data del raccolto (%) non può essere precedente alla messa a dimora (%)',
            NEW.data_raccolto, v_messa_a_dimora;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger per validare data raccolto
CREATE TRIGGER trg_validate_raccolto_date
    BEFORE INSERT OR UPDATE ON raccolto
    FOR EACH ROW
    EXECUTE FUNCTION validate_raccolto_date();



-- Funzione per prevenire eliminazione di piantagioni con raccolti
CREATE OR REPLACE FUNCTION prevent_piantagione_delete()
RETURNS TRIGGER AS $$
DECLARE
    v_raccolti_count INTEGER;
BEGIN
    -- Conta i raccolti associati alla piantagione
    SELECT COUNT(*) INTO v_raccolti_count
    FROM raccolto
    WHERE piantagione_id = OLD.id;

    IF v_raccolti_count > 0 THEN
        RAISE EXCEPTION 'Impossibile eliminare la piantagione ID %: esistono % raccolti associati. Rimuovere prima i raccolti.',
            OLD.id, v_raccolti_count;
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Trigger per prevenire eliminazione piantagioni con raccolti
CREATE TRIGGER trg_prevent_piantagione_delete
    BEFORE DELETE ON piantagione
    FOR EACH ROW
    EXECUTE FUNCTION prevent_piantagione_delete();

-- Funzione per prevenire eliminazione di zone con piantagioni attive
CREATE OR REPLACE FUNCTION prevent_zona_delete()
RETURNS TRIGGER AS $$
DECLARE
    v_piantagioni_count INTEGER;
BEGIN
    -- Conta le piantagioni attive nella zona
    SELECT COUNT(*) INTO v_piantagioni_count
    FROM piantagione
    WHERE id_zona = OLD.id;

    IF v_piantagioni_count > 0 THEN
        RAISE EXCEPTION 'Impossibile eliminare la zona "%": esistono % piantagioni associate. Rimuovere prima le piantagioni.',
            OLD.nome, v_piantagioni_count;
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Trigger per prevenire eliminazione zone con piantagioni
CREATE TRIGGER trg_prevent_zona_delete
    BEFORE DELETE ON zona
    FOR EACH ROW
    EXECUTE FUNCTION prevent_zona_delete();

-- Funzione per prevenire eliminazione di fornitori con piante associate
CREATE OR REPLACE FUNCTION prevent_fornitore_delete()
RETURNS TRIGGER AS $$
DECLARE
    v_piante_count INTEGER;
BEGIN
    -- Conta le piante associate al fornitore
    SELECT COUNT(*) INTO v_piante_count
    FROM pianta
    WHERE fornitore_id = OLD.id;

    IF v_piante_count > 0 THEN
        RAISE EXCEPTION 'Impossibile eliminare il fornitore "%": esistono % piante associate. Rimuovere prima le piante.',
            OLD.nome, v_piante_count;
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Trigger per prevenire eliminazione fornitori con piante
CREATE TRIGGER trg_prevent_fornitore_delete
    BEFORE DELETE ON fornitore
    FOR EACH ROW
    EXECUTE FUNCTION prevent_fornitore_delete();


-- Vista con dettagli completi piantagioni
CREATE OR REPLACE VIEW v_piantagioni_dettaglio AS
SELECT
    p.id as piantagione_id,
    p.quantita_pianta,
    p.messa_a_dimora,
    pi.tipo as pianta_tipo,
    pi.varieta as pianta_varieta,
    z.nome as zona_nome,
    z.tipo_terreno,
    sp.codice as stato_codice,
    sp.descrizione as stato_descrizione,
    f.nome as fornitore_nome,
    p.data_creazione,
    p.data_aggiornamento
FROM piantagione p
JOIN pianta pi ON p.id_pianta = pi.id
JOIN zona z ON p.id_zona = z.id
JOIN stato_piantagione sp ON p.id_stato_piantagione = sp.id
JOIN fornitore f ON pi.fornitore_id = f.id;

COMMENT ON VIEW v_piantagioni_dettaglio IS 'Vista con informazioni complete sulle piantagioni';

-- Vista raccolti con dettagli
CREATE OR REPLACE VIEW v_raccolti_dettaglio AS
SELECT
    r.id as raccolto_id,
    r.data_raccolto,
    r.quantita_kg,
    r.note,
    p.id as piantagione_id,
    pi.tipo as pianta_tipo,
    pi.varieta as pianta_varieta,
    z.nome as zona_nome,
    p.quantita_pianta as numero_piante,
    ROUND(r.quantita_kg / NULLIF(p.quantita_pianta, 0), 2) as kg_per_pianta,
    r.data_creazione
FROM raccolto r
JOIN piantagione p ON r.piantagione_id = p.id
JOIN pianta pi ON p.id_pianta = pi.id
JOIN zona z ON p.id_zona = z.id;

COMMENT ON VIEW v_raccolti_dettaglio IS 'Vista con informazioni complete sui raccolti e metriche calcolate';

-- Vista statistiche produzione per zona
CREATE OR REPLACE VIEW v_statistiche_zone AS
SELECT
    z.id as zona_id,
    z.nome as zona_nome,
    z.tipo_terreno,
    z.dimensione as dimensione_ha,
    COUNT(DISTINCT p.id) as numero_piantagioni,
    COUNT(DISTINCT r.id) as numero_raccolti,
    COALESCE(SUM(r.quantita_kg), 0) as produzione_totale_kg,
    COALESCE(ROUND(SUM(r.quantita_kg) / NULLIF(z.dimensione, 0), 2), 0) as kg_per_ettaro
FROM zona z
LEFT JOIN piantagione p ON z.id = p.id_zona
LEFT JOIN raccolto r ON p.id = r.piantagione_id
GROUP BY z.id, z.nome, z.tipo_terreno, z.dimensione;

COMMENT ON VIEW v_statistiche_zone IS 'Statistiche di produzione aggregate per zona';


-- Funzione per calcolare la produzione totale di una piantagione
CREATE OR REPLACE FUNCTION get_produzione_piantagione(p_piantagione_id INTEGER)
RETURNS DECIMAL(10,2) AS $$
DECLARE
    v_totale DECIMAL(10,2);
BEGIN
    SELECT COALESCE(SUM(quantita_kg), 0) INTO v_totale
    FROM raccolto
    WHERE piantagione_id = p_piantagione_id;

    RETURN v_totale;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION get_produzione_piantagione IS 'Calcola la produzione totale in kg di una piantagione';

-- Funzione per ottenere la produzione media per pianta
CREATE OR REPLACE FUNCTION get_media_produzione_per_pianta(p_piantagione_id INTEGER)
RETURNS DECIMAL(10,2) AS $$
DECLARE
    v_totale_kg DECIMAL(10,2);
    v_num_piante INTEGER;
    v_media DECIMAL(10,2);
BEGIN
    SELECT quantita_pianta INTO v_num_piante
    FROM piantagione
    WHERE id = p_piantagione_id;

    IF v_num_piante IS NULL OR v_num_piante = 0 THEN
        RETURN 0;
    END IF;

    v_totale_kg := get_produzione_piantagione(p_piantagione_id);
    v_media := ROUND(v_totale_kg / v_num_piante, 2);

    RETURN v_media;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION get_media_produzione_per_pianta IS 'Calcola la produzione media in kg per pianta di una piantagione';


-- Query per visualizzare tutte le tabelle create
SELECT
    table_name,
    (SELECT COUNT(*) FROM information_schema.columns
     WHERE table_name = t.table_name AND table_schema = 'public') as num_columns
FROM information_schema.tables t
WHERE table_schema = 'public'
  AND table_type = 'BASE TABLE'
ORDER BY table_name;

-- Query per visualizzare tutti gli indici
SELECT
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE schemaname = 'public'
ORDER BY tablename, indexname;

-- Query per visualizzare tutti i trigger
SELECT
    trigger_name,
    event_object_table as table_name,
    action_timing,
    event_manipulation
FROM information_schema.triggers
WHERE trigger_schema = 'public'
ORDER BY event_object_table, trigger_name;

COMMENT ON DATABASE agromanager IS 'Database per la gestione di aziende agricole - AgroManager';

