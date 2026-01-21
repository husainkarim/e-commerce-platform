import { Component, inject, signal } from '@angular/core';
import { AuthServiceService } from '../auth-service.service';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  private authService = inject(AuthServiceService);
  private subscriptions = new Subscription();

  isLoggedInSignal = signal(false);

  constructor() {
    this.subscriptions.add(
      this.authService.isLoggedIn$.subscribe(isLoggedIn => {
        this.isLoggedInSignal.set(isLoggedIn);
      })
    );
  }
}
