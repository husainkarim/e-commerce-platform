import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { AuthServiceService } from './auth-service.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'E-Commerce Platform';
  isSeller: boolean = false;
  isLoggedIn: boolean = false;

  constructor(private authServiceService: AuthServiceService, private router: Router) {
    // You can add any initialization logic here
    this.isLoggedIn = this.authServiceService.isLoggedIn();
    if (!this.isLoggedIn) {
      console.error('User is not logged in.');
    } else {
      this.isSeller = this.authServiceService.isSeller() || this.authServiceService.isAdmin();
    }
  }

  logout() {
    this.authServiceService.logout();
    this.router.navigate(['/login']);
  }
}
