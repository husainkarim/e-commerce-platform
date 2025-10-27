import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';

@Component({
  selector: 'app-seller-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './seller-dashboard.component.html',
  styleUrl: './seller-dashboard.component.css'
})
export class SellerDashboardComponent {
  products: any[] = [];
  constructor(private apiService: ApiService, private authServiceService: AuthServiceService, private router: Router, private route: ActivatedRoute) {
    const id = this.authServiceService.getUser().id;
    if (!id) {
      console.error('No user ID provided to load products.');
      this.router.navigate(['/not-found']);
      return;
    }
    this.apiService.getUserProducts(id).subscribe({
      next: (response) => {
        this.products = response.products;
      },
      error: (error) => {
        console.error('Failed to fetch user products:', error);
        this.router.navigate(['/not-found']);
      }
    });
  }
}

