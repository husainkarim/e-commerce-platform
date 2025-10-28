import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  user = {
    id: '',
    name: '',
    email: '',
    role: '',
    avatar: ''
  };
  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService, private authServiceService: AuthServiceService) {
    if (!this.authServiceService.isLoggedIn()) {
      console.error('User is not logged in.');
      this.router.navigate(['/login']);
      return;
    }
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      const userId = id;
      this.apiService.profile(userId).subscribe({
        next: (response) => {
          console.log('Profile data fetched successfully:', response);
          this.user = response.user;
        },
        error: (error) => {
          console.error('Failed to fetch profile data:', error);
          // Handle error (e.g., show error message, redirect)
          this.router.navigate(['/not-found']);
        }
      });
    } else {
      this.user = this.authServiceService.getUser();
    }
  }
}
