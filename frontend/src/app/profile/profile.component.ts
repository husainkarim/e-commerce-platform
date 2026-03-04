import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';
import { CommonModule } from '@angular/common';
import { SellerDashboardComponent } from '../seller-dashboard/seller-dashboard.component';
import { ClientDashboard } from '../client-dashboard/client-dashboard';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, SellerDashboardComponent, ClientDashboard],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  user = {
    id: '',
    name: '',
    email: '',
    role: '',
    avatar: ''
  };
  isAllowedToDelete: boolean = false;
  isSeller: boolean = false;
  isClient: boolean = false;
  isAdmin: boolean = false;
  showDashboard: boolean = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly apiService: ApiService,
    private readonly authServiceService: AuthServiceService
  ) {}

  ngOnInit(): void {
    let currentUser = this.authServiceService.getUser();
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
          this.setUserRole();
        },
        error: (error) => {
          console.error('Failed to fetch profile data:', error);
          this.router.navigate(['/not-found']);
        }
      });
    } else {
      this.user = this.authServiceService.getUser();
      this.setUserRole();
    }
    this.isAllowedToDelete = currentUser && (currentUser.role === 'admin' || currentUser.id === this.user.id);
  }

  setUserRole() {
    this.isSeller = this.user.role === 'seller';
    this.isClient = this.user.role === 'client';
    this.isAdmin = this.user.role === 'admin';
    // Show dashboard only when viewing own profile
    const currentUser = this.authServiceService.getUser();
    this.showDashboard = currentUser?.id === this.user.id;
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
