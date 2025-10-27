package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity 
public class Student {

    // ********************************************************************
    // 1. CHIAVE PRIMARIA
    // ********************************************************************
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    // ********************************************************************
    // 2. IDENTIFICATIVI E ANAGRAFICA (Derivati dal CSV)
    // ********************************************************************
    private String numeroMatricola; // Utile se usato come ID esterno
    private String nome;
    private String cognome;
    private String codiceFiscale; // Chiave cruciale per la deduplicazione
    private String email;
    private String cellulare;
    private String telefono;
    private LocalDate dataNascita;

    // ********************************************************************
    // 3. DETTAGLI CORSO E STATO (Stati da normalizzare)
    // ********************************************************************
    private String corsoITS;      // Tipo di corso ITS (es. BAD, DAI, BID)
    private String sedeCorso;     // Sede del corso (da normalizzare)
    private String statoCandidatura; // Stato finale (es. ACCETTATO, RIFIUTATO, IN_ATTESA)
    private LocalDate dataInserimento; // Data di candidatura
    
    // ********************************************************************
    // 4. VALUTAZIONE E DIPLOMA (Per BI e Filtri)
    // ********************************************************************
    private Double votoDiploma;
    private Integer annoDiploma;
    private Double votoFinaleValutazione; // Uno dei punteggi di valutazione (es. avg_24-26)
    
    // ********************************************************************
    // 5. CAMPI RESIDENZA (Se usati per la BI o gestione)
    // ********************************************************************
    private String indirizzoResidenza;
    private String cittaResidenza;
    private String provResidenza;

    // ********************************************************************
    // COSTRUTTORI
    // ********************************************************************

    public Student() {
        // Costruttore vuoto richiesto da JPA
    }
    
    // Si può aggiungere un costruttore con i parametri se necessario per i test

    // ********************************************************************
    // GETTERS E SETTERS
    // ********************************************************************

    // ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Identificativi
    public String getNumeroMatricola() {
        return numeroMatricola;
    }

    public void setNumeroMatricola(String numeroMatricola) {
        this.numeroMatricola = numeroMatricola;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellulare() {
        return cellulare;
    }

    public void setCellulare(String cellulare) {
        this.cellulare = cellulare;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    // Dettagli Corso e Stato
    public String getCorsoITS() {
        return corsoITS;
    }

    public void setCorsoITS(String corsoITS) {
        this.corsoITS = corsoITS;
    }

    public String getSedeCorso() {
        return sedeCorso;
    }

    public void setSedeCorso(String sedeCorso) {
        this.sedeCorso = sedeCorso;
    }

    public String getStatoCandidatura() {
        return statoCandidatura;
    }

    public void setStatoCandidatura(String statoCandidatura) {
        this.statoCandidatura = statoCandidatura;
    }

    public LocalDate getDataInserimento() {
        return dataInserimento;
    }

    public void setDataInserimento(LocalDate dataInserimento) {
        this.dataInserimento = dataInserimento;
    }

    // Valutazione e Diploma
    public Double getVotoDiploma() {
        return votoDiploma;
    }

    public void setVotoDiploma(Double votoDiploma) {
        this.votoDiploma = votoDiploma;
    }

    public Integer getAnnoDiploma() {
        return annoDiploma;
    }

    public void setAnnoDiploma(Integer annoDiploma) {
        this.annoDiploma = annoDiploma;
    }

    public Double getVotoFinaleValutazione() {
        return votoFinaleValutazione;
    }

    public void setVotoFinaleValutazione(Double votoFinaleValutazione) {
        this.votoFinaleValutazione = votoFinaleValutazione;
    }

    // Residenza
    public String getIndirizzoResidenza() {
        return indirizzoResidenza;
    }

    public void setIndirizzoResidenza(String indirizzoResidenza) {
        this.indirizzoResidenza = indirizzoResidenza;
    }

    public String getCittaResidenza() {
        return cittaResidenza;
    }

    public void setCittaResidenza(String cittaResidenza) {
        this.cittaResidenza = cittaResidenza;
    }

    public String getProvResidenza() {
        return provResidenza;
    }

    public void setProvResidenza(String provResidenza) {
        this.provResidenza = provResidenza;
    }
}