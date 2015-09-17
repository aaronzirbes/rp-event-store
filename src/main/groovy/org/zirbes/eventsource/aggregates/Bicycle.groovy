package org.zirbes.eventsource.domain

import com.thirdchannel.eventsource.AbstractAggregate

class Bicycle extends AbstractAggregate {

    String make
    String model
    Integer wheelSize

    Integer seatHeight

    BigDecimal velocity
    Integer headingDegrees
    Boolean locked

    Map<String, Tire> tires = [ front: new Tire(), rear: new Tire() ]
    Location location = new Location()

    static class Tire {
        int psi
        boolean holdsPressure
    }

    static class Location {
        BigDecimal lat
        BigDecimal lon

    }

}
