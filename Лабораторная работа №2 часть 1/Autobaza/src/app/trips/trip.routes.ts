import { Routes } from '@angular/router';
import { TripCenter } from './trip-center/trip-center.component';
import { TripList } from './trip-list/trip-list.component';
import { TripDetails } from './trip-details/trip-details.component';

export const routes1: Routes = [
  { path: '', redirectTo: 'trip-center', pathMatch: 'full' },
  {
    path: 'trip-center',
    component: TripCenter,
    children: [
      {
        path: '',
        component: TripList,
        children: [
          { path: ':id', component: TripDetails }
        ]
      }
    ]
  }
];
