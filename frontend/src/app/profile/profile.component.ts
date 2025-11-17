import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
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
  isAllowedToDelete: boolean = false;
  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService, private authServiceService: AuthServiceService) {
    let currentUser = this.authServiceService.getUser();
    this.isAllowedToDelete = currentUser && (currentUser.role === 'admin' || currentUser.id === this.user.id);
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

  openEditProfileModal() {
    this.router.navigate([`/edit-profile/${this.user.id}`]);
  }

  deleteAccount() {
    if (confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
      this.apiService.deleteAccount(this.user.id).subscribe({
        next: (response) => {
          console.log('Account deleted successfully:', response);
          this.authServiceService.logout();
          this.router.navigate(['/']);
        },
        error: (error) => {
          console.error('Failed to delete account:', error);
          // Handle error (e.g., show error message)
        }
      });
    }
  }
}
