package agenda;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description : An agenda that stores events
 */
public class Agenda {
    private final List<Event> myEvents = new ArrayList<>();
    /**
     * Adds an event to this agenda
     *
     * @param e the event to add
     */
    public void addEvent(Event e) {
        myEvents.add(Objects.requireNonNull(e));
    }

    /**
     * Computes the events that occur on a given day
     *
     * @param day the day toi test
     * @return a list of events that occur on that day
     */
    public List<Event> eventsInDay(LocalDate day) {
        List<Event> result = new ArrayList<>();
        for (Event e : myEvents) {
            if (e.isInDay(day)) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * Trouver les événements de l'agenda en fonction de leur titre
     * @param title le titre à rechercher
     * @return les événements qui ont le même titre
     */
    public List<Event> findByTitle(String title) {
        List<Event> res = new ArrayList<>();
        for (Event e : myEvents) {
            if (Objects.equals(e.getTitle(), title)) {
                res.add(e);
            }
        }
        return res;
    }

    /**
     * Déterminer s’il y a de la place dans l'agenda pour un événement (aucun autre événement au même moment)
     * @param e L'événement à tester (on se limitera aux événements sans répétition)
     * @return vrai s’il y a de la place dans l'agenda pour cet événement
     */
    public boolean isFreeFor(Event e) {
        if (e == null) return true;
        if (e.hasRepetition()) {
            throw new IllegalArgumentException("isFreeFor supports only non-repetitive events");
        }
        // compute interval for e
        java.time.LocalDateTime startE = e.getStart();
        java.time.LocalDateTime endE = startE.plus(e.getDuration());
        for (Event other : myEvents) {
            if (other == e) continue;
            if (other.hasRepetition()) continue; // limit to non-repetitive events
            java.time.LocalDateTime s = other.getStart();
            java.time.LocalDateTime t = s.plus(other.getDuration());
            boolean overlap = startE.isBefore(t) && endE.isAfter(s);
            if (overlap) return false;
        }
        return true;
    }
}
