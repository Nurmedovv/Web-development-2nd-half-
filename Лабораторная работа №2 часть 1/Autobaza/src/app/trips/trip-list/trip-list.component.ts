import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, ActivatedRoute } from '@angular/router';
import { Trip } from '../trip';
import { TripService } from '../services/trip.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-trip-list',
  imports: [CommonModule, RouterOutlet, RouterLink],
  templateUrl: './trip-list.component.html',
  styleUrl: './trip-list.component.css'
})
export class TripList implements OnInit {
  trips$!: Observable<Trip[]>;
  selectedId: number = 0;

  constructor(private tripService: TripService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.trips$ = this.tripService.getTrips();
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      this.selectedId = id ? +id : 0;
    });
  }
}
