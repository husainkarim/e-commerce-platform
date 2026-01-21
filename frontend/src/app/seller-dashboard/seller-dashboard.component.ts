import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, NavigationEnd, Router, RouterModule } from '@angular/router';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';
import { filter } from 'rxjs/operators';

interface TopSellingProduct {
  id: number;
  name: string;
  unitsSold: number;
  revenue: number;
}

@Component({
  selector: 'app-seller-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './seller-dashboard.component.html',
  styleUrl: './seller-dashboard.component.css'
})
export class SellerDashboardComponent {
  products: any[] = [];
  totalRevenue: number = 0;
  totalUnitsSold: number = 0;
  topSellingProducts: TopSellingProduct[] = [];
  chartColors: string[] = [
    '#FF6384',
    '#36A2EB',
    '#FFCE56',
    '#4BC0C0',
    '#9966FF',
    '#FF9F40',
    '#FF6384',
    '#C9CBCF',
  ];

  constructor(private apiService: ApiService, private authServiceService: AuthServiceService, private router: Router, private route: ActivatedRoute) {
    if (!this.authServiceService.isLoggedIn()) {
      console.error('User is not logged in.');
      this.router.navigate(['/login']);
      return;
    }
    // Subscribe to router events to detect when user navigates to this route
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.getUserProducts();
    });
  }

  ngOnInit() {
    this.getUserProducts();
  }

  getUserProducts() {
    const userId = this.authServiceService.getUser().id;
    this.apiService.getUserProducts(userId).subscribe({
      next: (response) => {
        this.products = response.products;
        for (let product of this.products) {
          this.apiService.getImagesByProductId(product.id).subscribe({
            next: (imageResponse) => {
              if (imageResponse.images && imageResponse.images.length > 0) {
                product.image = imageResponse.images[0].imagePath;
              } else {
                product.image = 'assets/product-images/default-product-image.jpg';
              }
            },
            error: (error) => {
              product.image = 'assets/product-images/default-product-image.jpg';
            }
          });
        }
        this.calculateAnalytics();
      },
      error: (error) => {
        console.error('Failed to fetch user products:', error);
      }
    });
  }

  calculateAnalytics() {
    // Generate sample sales data (in real app, this would come from backend orders)
    this.topSellingProducts = this.products
      .map((product: any) => ({
        id: product.id,
        name: product.name,
        unitsSold: product.unitsSold,
        revenue: product.revenue,
      }))
      .sort((a, b) => b.revenue - a.revenue)
      .slice(0, 5);

    // Calculate total metrics
    this.totalUnitsSold = this.topSellingProducts.reduce(
      (sum, item) => sum + item.unitsSold,
      0
    );
    this.totalRevenue = this.topSellingProducts.reduce(
      (sum, item) => sum + item.revenue,
      0
    );
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


