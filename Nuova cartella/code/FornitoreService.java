public class FornitoreService {
    private final FornitoreDAO fornitoreDAO;
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public FornitoreService(FornitoreDAO fornitoreDAO) {
        this.fornitoreDAO = fornitoreDAO;
    }

    private void validaFornitore(Fornitore fornitore) throws ValidationException {
        if (fornitore == null) {
            throw new ValidationException("Fornitore non può essere null");
        }

        if (fornitore.getNome() == null || fornitore.getNome().trim().isEmpty()) {
            throw ValidationException.requiredField("Nome fornitore");
        }

        if (fornitore.getIndirizzo() == null || fornitore.getIndirizzo().trim().isEmpty()) {
            throw ValidationException.requiredField("Indirizzo");
        }

        if (fornitore.getEmail() != null && !fornitore.getEmail().trim().isEmpty()
            && !EMAIL_PATTERN.matcher(fornitore.getEmail()).matches()) {
            throw ValidationException.invalidFormat("Email", "formato@esempio.com");
        }

        if (fornitore.getNumeroTelefono() != null && !fornitore.getNumeroTelefono().trim().isEmpty()
            && fornitore.getNumeroTelefono().trim().length() < 8) {
            throw new ValidationException("numeroTelefono", fornitore.getNumeroTelefono(),
                    "deve contenere almeno 8 caratteri");
        }
    }

    public void aggiungiFornitore(Fornitore fornitore) throws ValidationException, DataAccessException, BusinessLogicException {
        validaFornitore(fornitore);

        try {
            List<Fornitore> esistenti = fornitoreDAO.findAll();
            boolean duplicato = esistenti.stream()
                    .anyMatch(f -> f.getNome().equalsIgnoreCase(fornitore.getNome()) ||
                                  (f.getEmail() != null && fornitore.getEmail() != null &&
                                   f.getEmail().equalsIgnoreCase(fornitore.getEmail())));

            if (duplicato) {
                throw BusinessLogicException.duplicateEntry("Fornitore", "nome o email", fornitore.getNome());
            }

            fornitoreDAO.create(fornitore);
        } catch (SQLException e) {
            throw DataAccessException.queryError("inserimento fornitore", e);
        }
    }

    // Altri metodi di servizio (aggiornaFornitore, rimuoviFornitore, getFornitoreById, getAllFornitori)
}

public void aggiungiFornitore(Fornitore fornitore) throwsValidationException, DataAccessException, BusinessLogicException {
    validaFornitore(fornitore);
    try {
        List<Fornitore> esistenti = fornitoreDAO.findAll();
        boolean duplicato = esistenti.stream()
                .anyMatch(f -> f.getNome().equalsIgnoreCase(fornitore.getNome()) ||
                              (f.getEmail() != null && fornitore.getEmail() != null &&
                               f.getEmail().equalsIgnoreCase(fornitore.getEmail())));
        if (duplicato) {
            throw BusinessLogicException.duplicateEntry("Fornitore", "nome o email", fornitore.getNome());
        }
        fornitoreDAO.create(fornitore);
    } catch (SQLException e) {
        throw DataAccessException.queryError("inserimento fornitore", e);
    }
}