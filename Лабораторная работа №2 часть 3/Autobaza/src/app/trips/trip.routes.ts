import { Routes } from '@angular/router';
import { TripCenter } from './trip-center/trip-center.component';
import { TripList } from './trip-list/trip-list.component';
import { TripDetails } from './trip-details/trip-details.component';
import { AddTrip } from './add-trip/add-trip.component';
import { NavigationPath } from './consts/navigation/navigation-path.enum';

export const routes1: Routes = [
  { path: '', redirectTo: NavigationPath.TRIP_CENTER, pathMatch: 'full' },
  {
    path: NavigationPath.TRIP_CENTER,
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
  },
  {
    path: NavigationPath.ADD_TRIP,
    component: AddTrip
  },
  {
    path: `${NavigationPath.ADD_TRIP}/:id`,
    component: AddTrip
  }
];
