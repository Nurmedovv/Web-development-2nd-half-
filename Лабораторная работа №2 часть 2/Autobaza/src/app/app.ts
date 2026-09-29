import { Component } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { NavigationPath } from './trips/consts/navigation/navigation-path.enum';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  title = 'Autobaza';
  addTripPath = `/${NavigationPath.ADD_TRIP}`;
}
