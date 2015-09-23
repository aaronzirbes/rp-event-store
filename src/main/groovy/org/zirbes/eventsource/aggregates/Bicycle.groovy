package org.zirbes.eventsource.aggregates

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

    Map<String, Light> lights = [
        front: new Light(color: 'white'),
        rear: new Light(color: 'red')
    ]

    Location location = new Location()

    static class Tire {
        int psi
        boolean holdsPressure
    }

    static class Light {
        String color
        boolean on = false
        BigDecimal battery = 100.0
    }

    static class Location {
        BigDecimal lat
        BigDecimal lon

    }

}
