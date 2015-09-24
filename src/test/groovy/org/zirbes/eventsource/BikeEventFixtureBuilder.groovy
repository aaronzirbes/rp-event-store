package org.zirbes.eventsource

import org.zirbes.eventsource.events.AdjustSeatHeightEvent
import org.zirbes.eventsource.events.FlatTireEvent
import org.zirbes.eventsource.events.InflateTireEvent
import org.zirbes.eventsource.events.LockBikeEvent
import org.zirbes.eventsource.events.PurchaseBikeEvent
import org.zirbes.eventsource.events.RepairFlatEvent
import org.zirbes.eventsource.events.RideBikeEvent
import org.zirbes.eventsource.events.TurnOffLightEvent
import org.zirbes.eventsource.events.TurnOnLightEvent
import org.zirbes.eventsource.events.UnockBikeEvent
import org.zirbes.eventsource.events.VehicleEvent


class BikeEventFixtureBuilder {

    List<VehicleEvent> buildEvents() {
        List<VehicleEvent> events = [
            new PurchaseBikeEvent(make: 'Surly', model: 'Steamroller', wheelSize: 700),
            new AdjustSeatHeightEvent(height: 4),
            new TurnOnLightEvent(light: 'rear'),
            new InflateTireEvent(tire: 'front', psi: 80),
            new InflateTireEvent(tire: 'rear', psi: 80),
            new FlatTireEvent(tire: 'front'),
            new InflateTireEvent(tire: 'front', psi: 20),
            new RepairFlatEvent(tire: 'front'),
            new TurnOffLightEvent(light: 'rear'),
            new InflateTireEvent(tire: 'front', psi: 70),
            new LockBikeEvent(),
            new UnockBikeEvent(),
            new RideBikeEvent(lat: 45.003966, lon: -93.247044, headingDegrees: 0, velocity: 12)
        ]
    }
}
