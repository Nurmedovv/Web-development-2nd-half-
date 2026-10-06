import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { NavigationPath } from './trips/consts/navigation/navigation-path.enum';
import { TripStore } from './trips/trip.store';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  title = 'Autobaza';
  addTripPath = `/${NavigationPath.ADD_TRIP}`;

  private readonly store = inject(TripStore);

  ngOnInit(): void {
    // Часть 3: подтягиваем данные из Firestore, если Firebase настроен
    void this.store.loadFromFirestore();
  }
}
