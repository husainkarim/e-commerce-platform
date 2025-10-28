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
    if (!this.authServiceService.isLoggedIn()) {
      console.error('User is not logged in.');
      this.router.navigate(['/login']);
      return;
    }
  }

  ngOnInit() {
    this.getUserProducts();
  }

  getUserProducts() {
    const userId = this.authServiceService.getUser().id;
    this.apiService.getUserProducts(userId).subscribe({
      next: (response) => {
        this.products = response.products;
      },
      error: (error) => {
        console.error('Failed to fetch user products:', error);
        this.router.navigate(['/not-found']);
      }
    });
  }

  deleteProduct(productId: string) {
    this.apiService.deleteProduct(productId).subscribe({
      next: (response) => {
        console.log('Product deleted successfully:', response);
        this.getUserProducts();
      },
      error: (error) => {
        console.error('Failed to delete product:', error);
        alert('Failed to delete product. Please try again.');
      }
    });
  }
}

