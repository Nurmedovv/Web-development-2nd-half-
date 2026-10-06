import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadChildren: () =>
      import('./trips/trip.routes').then((m) => m.routes1)
  }
];
