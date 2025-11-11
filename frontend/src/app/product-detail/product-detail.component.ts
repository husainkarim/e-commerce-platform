import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.css'
})
export class ProductDetailComponent {
  product: any;

  constructor(private route: ActivatedRoute, private apiService: ApiService, private authServiceService: AuthServiceService, private router: Router) {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.apiService.getProductById(id).subscribe({
        next: (response) => {
          this.product = response.product;
        },
        error: (error) => {
          console.error('Failed to fetch product details:', error);
        }
      });
      this.apiService.getImagesByProductId(id).subscribe({
        next: (response) => {
          if (this.product) {
            let images: string[] = [];
            response.images.forEach((image: { imagePath: string }) => {
              images.push(image.imagePath);
            });
            console.log('Fetched product images:', images);
            this.product.images = images;
            this.product.image = this.product.images.length > 0 ? this.product.images[0] : null;
          }
        },
        error: (error) => {
          console.error('Failed to fetch product images:', error);
        }
      });
    } else {
      this.product = null;
      console.warn('No product ID found in route parameters.');
      this.router.navigate(['/not-found']);
    }
  }
}
