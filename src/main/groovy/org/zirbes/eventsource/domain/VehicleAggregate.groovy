package org.zirbes.eventsource.domain

import com.thirdchannel.eventsource.AbstractAggregate

class VehicleAggregate extends AbstractAggregate {

    String make
    String model
    Integer seatHeight
    Integer wheelSize
    Integer tirePressure
    Integer headingDegrees
    Boolean locked

}
