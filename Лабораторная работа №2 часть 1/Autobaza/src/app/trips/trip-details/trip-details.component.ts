import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Trip } from '../trip';
import { TripService } from '../services/trip.service';

@Component({
  selector: 'app-trip-details',
  imports: [CommonModule],
  templateUrl: './trip-details.component.html',
  styleUrl: './trip-details.component.css'
})
export class TripDetails implements OnInit {
  trip: Trip | undefined;

  constructor(
    private route: ActivatedRoute,
    private tripService: TripService
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.tripService.getTrip(id).subscribe(trip => this.trip = trip);
      }
    });
  }
}
