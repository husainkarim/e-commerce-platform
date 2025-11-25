import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { AuthServiceService } from './auth-service.service';
import { CommonModule } from '@angular/common';
import { inject, signal, computed } from '@angular/core';
import { Subscription } from 'rxjs';
import { routerMock } from './router-mock';
import { AppNavLink } from './app-nav-link';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'E-Commerce Platform';

  router = routerMock;

  // Inject the REAL AuthServiceService
  private authService = inject(AuthServiceService);
  private subscriptions = new Subscription();

  // --- Reactive State (Signals) ---
  isLoggedInSignal = signal(false);
  userRoleSignal = signal('guest');

  isSellerOrAdminSignal = computed(() => {
    const role = this.userRoleSignal();
    return role === 'seller' || role === 'admin';
  });

  constructor() {
    // 1. Subscribe to the Observables to automatically update local Signals
    this.subscriptions.add(
      this.authService.isLoggedIn$.subscribe(isLoggedIn => {
        this.isLoggedInSignal.set(isLoggedIn);
      })
    );
    this.subscriptions.add(
      this.authService.userRole$.subscribe(role => {
        this.userRoleSignal.set(role);
      })
    );
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
