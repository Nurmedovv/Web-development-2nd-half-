import { Component, computed, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Trip } from '../trip';
import { TripStore } from '../trip.store';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-add-trip',
  imports: [CommonModule, FormsModule],
  templateUrl: './add-trip.component.html',
  styleUrl: './add-trip.component.css'
})
export class AddTrip {
  tripId = signal(0);
  tripDescription = signal('');
  tripDriver = signal('');
  isEditing = signal(false);

  private store = inject(TripStore);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  trips = this.store.trips;

  drivers = computed(() => [
    ...new Set(this.trips().map((trip) => trip.driver))
  ]);

  formValid = computed(
    () =>
      this.tripId() > 0 &&
      this.tripDescription().trim().length > 0 &&
      this.tripDriver().trim().length > 0 &&
      (this.isEditing()
        ? this.trips().some((trip) => trip.id === this.tripId())
        : !this.trips().some((trip) => trip.id === this.tripId()))
  );

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      const trip = this.trips().find((item) => item.id === +id);
      if (trip) {
        this.tripId.set(trip.id);
        this.tripDescription.set(trip.description);
        this.tripDriver.set(trip.driver);
        this.isEditing.set(true);
      }
    }
  }

  onSubmit() {
    const trip: Trip = {
      id: this.tripId(),
      description: this.tripDescription(),
      driver: this.tripDriver()
    };

    if (this.isEditing()) {
      this.store.update(trip);
    } else {
      this.store.create(trip);
    }

    this.gotoTripList();
  }

  onDelete() {
    if (!this.isEditing()) {
      return;
    }

    this.store.delete({
      id: this.tripId(),
      description: this.tripDescription(),
      driver: this.tripDriver()
    });
    this.gotoTripList();
  }

  gotoTripList() {
    this.router.navigate(['/trip-center']);
  }
}
