import { Injectable, signal, computed, inject } from '@angular/core';
import { Trip } from './trip';
import { TRIPS } from './mock-trip-list';
import { FirestoreService } from './services/firestore.service';
import { isFirebaseConfigured } from './services/firebase.config';

/**
 * Signal Store — единый источник данных (часть 2).
 * CRUD выполняется через signal/computed/update.
 * Если Firebase настроен, операции дублируются в Cloud Firestore (часть 3).
 */
@Injectable({ providedIn: 'root' })
export class TripStore {
  private readonly firestore = inject(FirestoreService);

  trips = signal<Trip[]>([...TRIPS]);

  getTripById(id: number) {
    return computed(() => this.trips().find((trip) => trip.id === id));
  }

  create(trip: Trip) {
    this.trips.update((list) => [...list, trip]);
    if (isFirebaseConfigured()) {
      void this.firestore.create(trip);
    }
  }

  update(trip: Trip) {
    this.trips.update((list) =>
      list.map((item) => (item.id === trip.id ? trip : item))
    );
    if (isFirebaseConfigured()) {
      void this.firestore.update(trip);
    }
  }

  delete(trip: Trip) {
    this.trips.update((list) => list.filter((item) => item.id !== trip.id));
    if (isFirebaseConfigured()) {
      void this.firestore.delete(trip);
    }
  }

  /** Загрузить рейсы из Firestore (если настроен), иначе оставить mock. */
  async loadFromFirestore(): Promise<void> {
    if (!isFirebaseConfigured()) {
      return;
    }
    await this.firestore.seedTrips([...TRIPS]);
    this.firestore.getTrips().subscribe((remote) => {
      if (remote.length > 0) {
        this.trips.set(remote);
      }
    });
  }
}
