import { Injectable, inject } from '@angular/core';
import { Trip } from '../trip';
import { TRIPS } from '../mock-trip-list';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { FirestoreService } from './firestore.service';
import { isFirebaseConfigured } from './firebase.config';

/**
 * CRUD-сервис рейсов (часть 1).
 * Работает на mock-данных; при настроенном Firebase дублирует операции
 * в Cloud Firestore через FirestoreService (часть 3).
 */
@Injectable({
  providedIn: 'root'
})
export class TripService {
  private readonly firestore = inject(FirestoreService);
  private trips: Trip[] = [...TRIPS];

  getTrips(): Observable<Trip[]> {
    if (isFirebaseConfigured()) {
      return this.firestore.getTrips().pipe(
        map((remote) => (remote.length > 0 ? remote : this.trips))
      );
    }
    return of(this.trips);
  }

  getTrip(id?: number | string): Observable<Trip | undefined> {
    if (id) {
      return this.getTrips().pipe(
        map((trips: Trip[]) => trips.find((trip) => trip.id === +id!))
      );
    } else {
      throw new Error('Parameter "id" is "undefined".');
    }
  }

  async create(trip: Trip) {
    this.trips.push(trip);
    if (isFirebaseConfigured()) {
      await this.firestore.create(trip);
    }
  }

  async delete(trip: Trip) {
    const ind = this.trips.findIndex((obj) => obj.id === trip.id);
    if (ind !== -1) {
      this.trips.splice(ind, 1);
    }
    if (isFirebaseConfigured()) {
      await this.firestore.delete(trip);
    }
  }

  async update(trip: Trip) {
    const ind = this.trips.findIndex((obj) => obj.id === trip.id);
    if (ind !== -1) {
      this.trips[ind] = trip;
    }
    if (isFirebaseConfigured()) {
      await this.firestore.update(trip);
    }
  }
}
