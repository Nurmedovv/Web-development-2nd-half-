import { Component, signal, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TripStore } from '../trip.store';

@Component({
  selector: 'app-trip-details',
  imports: [CommonModule, RouterLink],
  templateUrl: './trip-details.component.html',
  styleUrl: './trip-details.component.css'
})
export class TripDetails {
  private store = inject(TripStore);
  private route = inject(ActivatedRoute);

  tripId = signal(0);

  trip = computed(() =>
    this.trips().find((item) => item.id === this.tripId())
  );

  private trips = this.store.trips;

  constructor() {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      this.tripId.set(id ? +id : 0);
    });
  }
}
