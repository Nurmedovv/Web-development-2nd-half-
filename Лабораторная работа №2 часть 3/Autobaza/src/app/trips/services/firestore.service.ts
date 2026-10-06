import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Trip } from '../trip';
import {
  FIRESTORE_COLLECTION,
  firebaseConfig,
  isFirebaseConfigured
} from './firebase.config';
import { initializeApp, FirebaseApp } from 'firebase/app';
import {
  getFirestore,
  Firestore,
  collection,
  doc,
  setDoc,
  deleteDoc,
  getDocs,
  query,
  orderBy,
  onSnapshot,
  DocumentData
} from 'firebase/firestore';

/**
 * Сервис для работы с Cloud Firestore (часть 3, вариант 8).
 *
 * Методы CRUD:
 *   getTrips()  — чтение всех рейсов
 *   getTrip(id) — чтение одного рейса по id
 *   create()    — добавление документа
 *   update()    — обновление документа
 *   delete()    — удаление документа
 *
 * Если Firebase не сконфигурирован (пустые ключи в firebase.config.ts),
 * сервис возвращает пустые результаты, а UI продолжает работать
 * на mock-данных из TripStore.
 */
@Injectable({ providedIn: 'root' })
export class FirestoreService {
  private readonly collectionName = FIRESTORE_COLLECTION;
  private readonly useFirestore = isFirebaseConfigured();
  private app: FirebaseApp | null = null;
  private db: Firestore | null = null;

  get isAvailable(): boolean {
    return this.useFirestore;
  }

  private ensureDb(): Firestore | null {
    if (!this.useFirestore) {
      return null;
    }
    if (!this.db) {
      this.app = initializeApp(firebaseConfig);
      this.db = getFirestore(this.app);
    }
    return this.db;
  }

  getTrips(): Observable<Trip[]> {
    const db = this.ensureDb();
    if (!db) {
      return of([]);
    }
    return new Observable<Trip[]>((subscriber) => {
      const unsubscribe = onSnapshot(
        collection(db, this.collectionName),
        (snapshot) => {
          const trips = snapshot.docs.map((d) => {
            const data = d.data() as DocumentData;
            return {
              id: Number(data['id'] ?? d.id),
              description: String(data['description'] ?? ''),
              driver: String(data['driver'] ?? '')
            } as Trip;
          });
          subscriber.next(trips);
        },
        (error) => subscriber.error(error)
      );
      return () => unsubscribe();
    });
  }

  getTrip(id: number | string): Observable<Trip | undefined> {
    const db = this.ensureDb();
    if (!db) {
      return of(undefined);
    }
    return new Observable<Trip | undefined>((subscriber) => {
      const unsubscribe = onSnapshot(
        doc(db, this.collectionName, String(id)),
        (snapshot) => {
          if (!snapshot.exists()) {
            subscriber.next(undefined);
            return;
          }
          const data = snapshot.data() as DocumentData;
          subscriber.next({
            id: Number(data['id'] ?? snapshot.id),
            description: String(data['description'] ?? ''),
            driver: String(data['driver'] ?? '')
          } as Trip);
        },
        (error) => subscriber.error(error)
      );
      return () => unsubscribe();
    });
  }

  async create(trip: Trip): Promise<void> {
    const db = this.ensureDb();
    if (!db) {
      return;
    }
    const ref = doc(db, this.collectionName, String(trip.id));
    await setDoc(ref, {
      id: trip.id,
      description: trip.description,
      driver: trip.driver
    });
  }

  async update(trip: Trip): Promise<void> {
    const db = this.ensureDb();
    if (!db) {
      return;
    }
    const ref = doc(db, this.collectionName, String(trip.id));
    await setDoc(
      ref,
      {
        id: trip.id,
        description: trip.description,
        driver: trip.driver
      },
      { merge: true }
    );
  }

  async delete(trip: Trip): Promise<void> {
    const db = this.ensureDb();
    if (!db) {
      return;
    }
    const ref = doc(db, this.collectionName, String(trip.id));
    await deleteDoc(ref);
  }

  /** Синхронизация mock-данных в Firestore (если коллекция пуста). */
  async seedTrips(seed: Trip[]): Promise<void> {
    const db = this.ensureDb();
    if (!db) {
      return;
    }
    const snapshot = await getDocs(
      query(collection(db, this.collectionName), orderBy('id'))
    );
    if (!snapshot.empty) {
      return;
    }
    for (const trip of seed) {
      await this.create(trip);
    }
  }
}
