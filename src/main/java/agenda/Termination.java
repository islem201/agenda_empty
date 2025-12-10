package agenda;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public class Termination {

    private final LocalDate myTerminationDateInclusive;
    private final long myNumberOfOccurrences;

    public LocalDate terminationDateInclusive() {
        return myTerminationDateInclusive;
    }

    public long numberOfOccurrences() {
        return myNumberOfOccurrences;
    }


    /**
     * Constructs a  termination at a given date
     * @param start the start time of this event
     * @param frequency one of :
     * <UL>
     * <LI>ChronoUnit.DAYS for daily repetitions</LI>
     * <LI>ChronoUnit.WEEKS for weekly repetitions</LI>
     * <LI>ChronoUnit.MONTHS for monthly repetitions</LI>
     * </UL>
     * @param terminationInclusive the date when this event ends
     * @see ChronoUnit#between(Temporal, Temporal)
     */
    public Termination(LocalDate start, ChronoUnit frequency, LocalDate terminationInclusive) {
        if (start == null || terminationInclusive == null || frequency == null) {
            throw new IllegalArgumentException("Les arguments ne doivent pas être nuls");
        }
        this.myTerminationDateInclusive = terminationInclusive;
        long between = frequency.between(start, terminationInclusive);
        this.myNumberOfOccurrences = between + 1;
        
    }

    /**
     * Constructs a fixed termination event ending after a number of iterations
     * @param start the start time of this event
     * @param frequency one of :
     * <UL>
     * <LI>ChronoUnit.DAYS for daily repetitions</LI>
     * <LI>ChronoUnit.WEEKS for weekly repetitions</LI>
     * <LI>ChronoUnit.MONTHS for monthly repetitions</LI>
     * </UL>
     * @param numberOfOccurrences the number of occurrences of this repetitive event
     */
    public Termination(LocalDate start, ChronoUnit frequency, long numberOfOccurrences) {
        if (start == null || frequency == null) {
            throw new IllegalArgumentException("Les arguments ne doivent pas être nuls");
        }
        if (numberOfOccurrences <= 0) {
            throw new IllegalArgumentException("Le nombre d'occurrences doit être > 0");
        }

        this.myNumberOfOccurrences = numberOfOccurrences;
        this.myTerminationDateInclusive = start.plus(numberOfOccurrences - 1, frequency);
    }
}
