import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, ActivatedRoute } from '@angular/router';
import { TripStore } from '../trip.store';

@Component({
  selector: 'app-trip-list',
  imports: [CommonModule, RouterOutlet, RouterLink],
  templateUrl: './trip-list.component.html',
  styleUrl: './trip-list.component.css'
})
export class TripList {
  private store = inject(TripStore);
  private route = inject(ActivatedRoute);

  trips = this.store.trips;
  selectedId = signal(0);

  constructor() {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      this.selectedId.set(id ? +id : 0);
    });
  }
}
