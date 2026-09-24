import { Injectable } from '@angular/core';
import { Trip } from '../trip';
import { TRIPS } from '../mock-trip-list';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TripService {
  private trips: Trip[] = [];

  constructor() {
    this.trips = TRIPS;
  }

  getTrips(): Observable<Trip[]> {
    return of(this.trips);
  }

  getTrip(id?: number | string): Observable<Trip | undefined> {
    if (id) {
      return this.getTrips().pipe(
        map((trips: Trip[]) => trips.find(trip => trip.id === +id!))
      );
    } else {
      throw new Error('Parameter "id" is "undefined".');
    }
  }

  async create(trip: Trip) {
    this.trips.push(trip);
  }

  async delete(trip: Trip) {
    const ind = this.trips.findIndex((obj) => obj.id === trip.id);
    if (ind !== -1) {
      this.trips.splice(ind, 1);
    }
  }

  async update(trip: Trip) {
    const ind = this.trips.findIndex((obj) => obj.id === trip.id);
    this.trips[ind] = trip;
  }
}
