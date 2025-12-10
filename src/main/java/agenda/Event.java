package agenda;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class Event {

    /**
     * The myTitle of this event
     */
    private String myTitle;
    
    /**
     * The starting time of the event
     */
    private LocalDateTime myStart;

    /**
     * The durarion of the event 
     */
    private Duration myDuration;
    private Repetition myRepetition = null;


    /**
     * Constructs an event
     *
     * @param title the title of this event
     * @param start the start time of this event
     * @param duration the duration of this event
     */
    public Event(String title, LocalDateTime start, Duration duration) {
        this.myTitle = title;
        this.myStart = start;
        this.myDuration = duration;
    }

    public void setRepetition(ChronoUnit frequency) {
        this.myRepetition = new Repetition(frequency);
    }

    public void addException(LocalDate date) {
        if (myRepetition == null) {
            throw new IllegalStateException("No repetition set");
        }
        myRepetition.addException(date);
    }

    public void setTermination(LocalDate terminationInclusive) {
        if (myRepetition == null) {
            throw new IllegalStateException("No repetition set");
        }
        myRepetition.setTermination(new Termination(myStart.toLocalDate(), myRepetition.getFrequency(), terminationInclusive));
    }

    public void setTermination(long numberOfOccurrences) {
        if (myRepetition == null) {
            throw new IllegalStateException("No repetition set");
        }
        myRepetition.setTermination(new Termination(myStart.toLocalDate(), myRepetition.getFrequency(), numberOfOccurrences));
    }

    public int getNumberOfOccurrences() {
        if (myRepetition == null || myRepetition.getTermination() == null) {
            throw new IllegalStateException("No termination set");
        }
        return (int) myRepetition.getTermination().numberOfOccurrences();
    }

    public LocalDate getTerminationDate() {
        if (myRepetition == null || myRepetition.getTermination() == null) {
            throw new IllegalStateException("No termination set");
        }
        return myRepetition.getTermination().terminationDateInclusive();
    }

    /**
     * Tests if an event occurs on a given day
     *
     * @param aDay the day to test
     * @return true if the event occurs on that day, false otherwise
     */
    public boolean isInDay(LocalDate aDay) {
        Objects.requireNonNull(aDay);
        LocalDateTime dayStart = aDay.atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);

        LocalDateTime eventStart = myStart;
        LocalDateTime eventEnd = myStart.plus(myDuration);

        if (myRepetition == null) {
            // single occurrence
            return eventStart.isBefore(dayEnd) && eventEnd.isAfter(dayStart);
        }

        // repetitive event: if the day itself is an exception, it does not occur on that day
        List<LocalDate> excList = myRepetition.getExceptions();
        if (excList != null && excList.contains(aDay)) return false;

        // repetitive event: need to search for an occurrence that overlaps the day
        LocalDate startDate = myStart.toLocalDate();
        ChronoUnit freq = myRepetition.getFrequency();
        long seconds = myDuration.getSeconds();
        long daysSpan = Math.max(1, (seconds + 86400 - 1) / 86400); // ceil

        // check candidate occurrence start dates that could overlap the day
        for (long delta = 0; delta <= daysSpan; delta++) {
            LocalDate candidateDate = aDay.minusDays(delta);
            if (candidateDate.isBefore(startDate)) continue; // before first occurrence
            // check if candidateDate is an occurrence date according to frequency
            long between = freq.between(startDate, candidateDate);
            if (between < 0) continue;
            LocalDate expected = startDate.plus(between, freq);
            if (!expected.equals(candidateDate)) continue; // not aligned with repetition
            // check termination
            if (myRepetition.getTermination() != null) {
                Termination term = myRepetition.getTermination();
                if (term.numberOfOccurrences() > 0) {
                    if (between + 1 > term.numberOfOccurrences()) continue;
                }
                if (term.terminationDateInclusive() != null) {
                    if (candidateDate.isAfter(term.terminationDateInclusive())) continue;
                }
            }
            // check exceptions
            List<LocalDate> exc = myRepetition.getExceptions();
            if (exc != null && exc.contains(candidateDate)) continue;

            // compute occurrence interval
            LocalDateTime occStart = candidateDate.atTime(myStart.toLocalTime());
            LocalDateTime occEnd = occStart.plus(myDuration);
            if (occStart.isBefore(dayEnd) && occEnd.isAfter(dayStart)) return true;
        }

        return false;
    }

    public boolean hasRepetition() {
        return myRepetition != null;
    }
   
    /**
     * @return the myTitle
     */
    public String getTitle() {
        return myTitle;
    }

    /**
     * @return the myStart
     */
    public LocalDateTime getStart() {
        return myStart;
    }


    /**
     * @return the myDuration
     */
    public Duration getDuration() {
        return myDuration;
    }

    @Override
    public String toString() {
        return "Event{title='%s', start=%s, duration=%s}".formatted(myTitle, myStart, myDuration);
    }
}
