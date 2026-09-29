import { Injectable, signal, computed } from '@angular/core';
import { Trip } from './trip';
import { TRIPS } from './mock-trip-list';

@Injectable({ providedIn: 'root' })
export class TripStore {
  trips = signal<Trip[]>([...TRIPS]);

  getTripById(id: number) {
    return computed(() => this.trips().find((trip) => trip.id === id));
  }

  create(trip: Trip) {
    this.trips.update((list) => [...list, trip]);
  }

  update(trip: Trip) {
    this.trips.update((list) =>
      list.map((item) => (item.id === trip.id ? trip : item))
    );
  }

  delete(trip: Trip) {
    this.trips.update((list) => list.filter((item) => item.id !== trip.id));
  }
}
